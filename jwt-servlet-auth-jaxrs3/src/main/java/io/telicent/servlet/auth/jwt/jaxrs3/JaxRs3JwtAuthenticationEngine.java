/**
 * Copyright (C) Telicent Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.telicent.servlet.auth.jwt.jaxrs3;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.telicent.servlet.auth.jwt.HeaderBasedJwtAuthenticationEngine;
import io.telicent.servlet.auth.jwt.JwtHttpConstants;
import io.telicent.servlet.auth.jwt.challenges.Challenge;
import io.telicent.servlet.auth.jwt.challenges.TokenCandidate;
import io.telicent.servlet.auth.jwt.sources.HeaderSource;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.Strings;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A JWT authentication engine for use with JAX-RS 3.x web applications
 */
public class JaxRs3JwtAuthenticationEngine
        extends HeaderBasedJwtAuthenticationEngine<ContainerRequestContext, ContainerResponseContext> {

    /**
     * Creates a new authentication engine using default configuration
     */
    public JaxRs3JwtAuthenticationEngine() {
        this(JwtHttpConstants.DEFAULT_HEADER_SOURCES, null, null, null);
    }

    /**
     * Creates a new authentication engine using the provided configuration
     *
     * @param headers        Header sources
     * @param realm          Realm
     * @param usernameClaims Username claims
     * @param rolesClaim     Roles claim
     */
    public JaxRs3JwtAuthenticationEngine(Collection<HeaderSource> headers, String realm,
                                         Collection<String> usernameClaims, String[] rolesClaim) {
        super(headers, realm, usernameClaims, rolesClaim);
    }

    @Override
    protected boolean hasRequiredParameters(ContainerRequestContext request) {
        return this.headers.stream()
                           .anyMatch(h -> request.getHeaders()
                                                 .keySet()
                                                 .stream()
                                                 .anyMatch(k -> Strings.CI.equals(k, h.getHeader())));
    }

    @Override
    protected List<TokenCandidate> extractTokens(ContainerRequestContext request) {
        return this.headers.stream()
                           .flatMap(h -> request.getHeaders()
                                                .entrySet()
                                                .stream()
                                                .filter(e -> Strings.CI.equals(e.getKey(), h.getHeader()))
                                                .flatMap(e -> e.getValue().stream().map(v -> new TokenCandidate(h, v))))
                           .collect(Collectors.toList());
    }

    @Override
    protected ContainerRequestContext prepareRequest(ContainerRequestContext request, Jws<Claims> jws,
                                                     String username) {
        request.setSecurityContext(new JwtSecurityContext(jws, username, Strings.CS.equals(
                request.getUriInfo().getBaseUri().getScheme(), "https"), null));
        return request;
    }

    @Override
    protected void sendChallenge(ContainerRequestContext request, ContainerResponseContext response,
                                 Challenge challenge) {
        String realm = selectRealm(JwtHttpConstants.sanitiseHeaderParameterValue(getRequestUrl(request)));
        Map<String, String> additionalParams =
                buildChallengeParameters(challenge.errorCode(), challenge.errorDescription());
        String authChallenge = buildAuthorizationHeader(realm, additionalParams);

        // Explicitly abort the request with the relevant status and HTTP Authentication challenge
        request.abortWith(buildChallengeResponse(authChallenge, challenge));
    }

    /**
     * Builds the challenge response, intended for derived classes to inject a custom response entity if they so desire
     *
     * @param authChallenge Authentication challenge that should be included in the response as a
     *                      {@value JwtHttpConstants#HEADER_WWW_AUTHENTICATE} header.
     * @param challenge     Challenge parameters
     * @return Challenge response
     */
    protected Response buildChallengeResponse(String authChallenge, Challenge challenge) {
        return Response.status(Response.Status.fromStatusCode(challenge.statusCode()))
                       .header(JwtHttpConstants.HEADER_WWW_AUTHENTICATE, authChallenge)
                       .build();
    }

    @Override
    protected void sendError(ContainerResponseContext response, Throwable err) {
        // In a JAX-RS context we can just throw the exception and rely on JAX-RS handling the exception to abort the
        // request processing
        throw new RuntimeException(UNEXPECTED_ERROR_MESSAGE, err);
    }

    @Override
    protected String getRequestUrl(ContainerRequestContext request) {
        return request.getUriInfo().getRequestUri().toString();
    }

    @Override
    protected void setRequestAttribute(ContainerRequestContext request, String attribute, Object value) {
        request.setProperty(attribute, value);
    }

}
