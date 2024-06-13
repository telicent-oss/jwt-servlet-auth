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
package io.telicent.servlet.auth.jwt.servlet5;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.telicent.servlet.auth.jwt.HeaderBasedJwtAuthenticationEngine;
import io.telicent.servlet.auth.jwt.JwtHttpConstants;
import io.telicent.servlet.auth.jwt.JwtServletConstants;
import io.telicent.servlet.auth.jwt.challenges.Challenge;
import io.telicent.servlet.auth.jwt.challenges.TokenCandidate;
import io.telicent.servlet.auth.jwt.challenges.VerifiedToken;
import io.telicent.servlet.auth.jwt.sources.HeaderSource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * A JSON Web Token (JWT) authentication engine for use with {@code jakarta.servlet} based applications
 */
public class Servlet5JwtAuthenticationEngine extends
        HeaderBasedJwtAuthenticationEngine<HttpServletRequest, HttpServletResponse> {

    /**
     * Creates a new authentication engine using default configuration
     */
    public Servlet5JwtAuthenticationEngine() {
        this(JwtHttpConstants.DEFAULT_HEADER_SOURCES, null,
             null);
    }

    /**
     * Creates a new authentication engine using the provided configuration
     *
     * @param headers        Header sources
     * @param realm          Realm
     * @param usernameClaims Username claims
     */
    public Servlet5JwtAuthenticationEngine(Collection<HeaderSource> headers, String realm, Collection<String> usernameClaims) {
        super(headers, realm, usernameClaims);
    }

    @Override
    protected boolean hasRequiredParameters(HttpServletRequest request) {
        return this.headers.stream().anyMatch(h -> StringUtils.isNotBlank(request.getHeader(h.getHeader())));
    }

    @Override
    protected List<TokenCandidate> extractTokens(HttpServletRequest request) {
        List<TokenCandidate> candidates = new ArrayList<>();
        for (HeaderSource header : this.headers) {
            Enumeration<String> headerValues = request.getHeaders(header.getHeader());
            if (headerValues == null) {
                continue;
            }
            while (headerValues.hasMoreElements()) {
                candidates.add(new TokenCandidate(header, headerValues.nextElement()));
            }
        }
        return candidates;
    }

    @Override
    protected HttpServletRequest prepareRequest(HttpServletRequest request, Jws<Claims> jws, String username) {
        return new AuthenticatedHttpServletRequest(request, jws, username);
    }

    @Override
    protected void sendChallenge(HttpServletRequest request, HttpServletResponse response, Challenge challenge) {
        String realm = selectRealm(request.getRequestURI());
        Map<String, String> additionalParams = buildChallengeParameters(challenge.errorCode(),
                                                                        challenge.errorDescription());
        response.addHeader(JwtHttpConstants.HEADER_WWW_AUTHENTICATE,
                           buildAuthorizationHeader(realm, additionalParams));
        try {
            response.sendError(challenge.statusCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void sendError(HttpServletResponse response, Throwable err) {
        try {
            response.sendError(500, err.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getRequestUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @Override
    protected void setRequestAttribute(HttpServletRequest request, String attribute, Object value) {
        request.setAttribute(attribute, value);
    }
}
