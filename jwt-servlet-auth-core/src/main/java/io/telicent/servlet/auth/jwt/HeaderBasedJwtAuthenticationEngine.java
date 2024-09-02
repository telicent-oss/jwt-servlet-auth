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
package io.telicent.servlet.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.RequiredTypeException;
import io.telicent.servlet.auth.jwt.challenges.VerifiedToken;
import io.telicent.servlet.auth.jwt.sources.HeaderSource;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * A JWT authentication engine that uses HTTP Headers to find the authentication token
 *
 * @param <TRequest>  Request type
 * @param <TResponse> Response type
 */
public abstract class HeaderBasedJwtAuthenticationEngine<TRequest, TResponse>
        extends JwtAuthenticationEngine<TRequest, TResponse> {
    /**
     * A generic error message that is sent when authentication fails unexpectedly
     */
    public static final String UNEXPECTED_ERROR_MESSAGE = "Unexpected error during JWT authentication";
    /**
     * Possible header sources
     */
    protected final List<HeaderSource> headers = new ArrayList<>();
    /**
     * The realm used for issuing challenges
     */
    protected final String realm;
    /**
     * The claim(s) from which to extract the username
     */
    protected final List<String> usernameClaims;

    /**
     * Creates a new engine
     *
     * @param headers        HTTP Headers that may be used to provide a token
     * @param realm          Realm to use in challenges
     * @param usernameClaims Username claim(s) from which the username should be extracted.  The first claim that
     *                       contains a non-empty value that is a valid string will be used.
     */
    public HeaderBasedJwtAuthenticationEngine(Collection<HeaderSource> headers, String realm,
                                              Collection<String> usernameClaims) {
        Objects.requireNonNull(headers, "Header sources cannot be null");
        if (headers.isEmpty()) {
            throw new IllegalArgumentException("Header sources cannot be empty");
        }
        this.headers.addAll(headers);
        this.realm = realm;
        this.usernameClaims = usernameClaims != null ? List.copyOf(usernameClaims) : List.of();
    }

    @Override
    protected String extractUsername(Jws<Claims> jws) {
        // Try all the configured username claims in the provided order
        for (String claim : this.usernameClaims) {
            try {
                if (!jws.getPayload().containsKey(claim)) {
                    continue;
                }

                // Claims could be present but have a blank value (#17) in which case we want to continue to try another
                // configured claim, or fallback to the subject of the JWS
                String username = jws.getPayload().get(claim, String.class);
                if (StringUtils.isNotBlank(username)) {
                    return username;
                }
            } catch (RequiredTypeException e) {
                // Claim value was not a string, so we'll try the next claim, or fallback to the subject of the JWS
            }
        }
        // Fallback to the subject of the JWS if none of the other claims provided a valid username
        return jws.getPayload().getSubject();
    }

    /**
     * Selects the realm to use for HTTP Challenge responses
     *
     * @param defaultRealm Default realm to use if one has not been explicitly configured
     * @return Realm
     */
    protected String selectRealm(String defaultRealm) {
        return JwtHttpConstants.sanitiseHeaderParameterValue(
                StringUtils.isNotBlank(this.realm) ? this.realm : defaultRealm);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName())
               .append("{")
               .append("headers=[")
               .append(StringUtils.join(this.headers, ", "))
               .append("], realm=")
               .append(this.realm)
               .append(", usernameClaims=[")
               .append(StringUtils.join(this.usernameClaims, ", "))
               .append("]}");
        return builder.toString();
    }
}
