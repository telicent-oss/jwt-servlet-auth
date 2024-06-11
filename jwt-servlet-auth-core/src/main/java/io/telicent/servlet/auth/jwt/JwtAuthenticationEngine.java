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

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.KeyException;
import io.jsonwebtoken.security.SignatureException;
import io.telicent.servlet.auth.jwt.challenges.Challenge;
import io.telicent.servlet.auth.jwt.challenges.TokenCandidate;
import io.telicent.servlet.auth.jwt.challenges.VerifiedToken;
import io.telicent.servlet.auth.jwt.verification.JwtVerifier;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An authentication engine that verifies JSON Web Tokens (JWT)
 *
 * @param <TRequest>  Request type
 * @param <TResponse> Response type
 */
public abstract class JwtAuthenticationEngine<TRequest, TResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationEngine.class);

    /**
     * No longer used.
     */
    @Deprecated
    protected static final String NO_AUTH_TOKEN_FOUND = null;

    /**
     * Attempts to authenticate a request, returning either an authenticated request object upon success or {@code null}
     * on failure.  Upon failure the engine will have called its {@link #sendChallenge(Object, Object, Challenge)} or
     * {@link #sendError(Object, Throwable)} methods as appropriate so that failure will already have been communicated
     * and the caller of the engine can simply cease any further processing of the request.
     *
     * @param request  Request
     * @param response Response
     * @param verifier JWT Verifier
     * @return Authenticated request or {@code null} if authentication failed
     */
    public final TRequest authenticate(TRequest request, TResponse response, JwtVerifier verifier) {
        try {
            MDC.put(JwtLoggingConstants.MDC_JWT_USER, null);
            if (!hasRequiredParameters(request)) {
                // No authentication parameters provided so abort immediately
                sendChallenge(request, response, new Challenge(401, "", ""));
                return null;
            }

            // Extract all the possible raw tokens from the request
            List<TokenCandidate> rawTokens = extractTokens(request);
            if (rawTokens.isEmpty()) {
                sendChallenge(request, response,
                              new Challenge(400, OAuth2Constants.ERROR_INVALID_REQUEST, "No Bearer token(s) provided"));
                return null;
            }

            // Consider each candidate token and try and verify it
            List<Challenge> challenges = new ArrayList<>();
            List<VerifiedToken> validTokens = new ArrayList<>();
            for (TokenCandidate candidateToken : rawTokens) {
                // Verify the token and record a challenge if it fails verification
                try {
                    String rawToken = candidateToken.source().getRawToken(candidateToken.value());
                    if (StringUtils.isBlank(rawToken)) {
                        challenges.add(new Challenge(400, OAuth2Constants.ERROR_INVALID_REQUEST,
                                                     "No Bearer token(s) provided"));
                        continue;
                    }
                    Jws<Claims> jws = verifier.verify(rawToken);
                    validTokens.add(new VerifiedToken(rawToken, jws));
                } catch (KeyException keyErr) {
                    challenges.add(new Challenge(401, OAuth2Constants.ERROR_INVALID_TOKEN,
                                                 "Invalid/weak key: " + keyErr.getMessage()));
                } catch (SignatureException sigErr) {
                    challenges.add(new Challenge(401, OAuth2Constants.ERROR_INVALID_TOKEN,
                                                 "Token failed signature verification: " + sigErr.getMessage()));
                } catch (MalformedJwtException malformedErr) {
                    challenges.add(new Challenge(401, OAuth2Constants.ERROR_INVALID_TOKEN,
                                                 "Token is malformed: " + malformedErr.getMessage()));
                } catch (UnsupportedJwtException unsupportedErr) {
                    challenges.add(new Challenge(400, OAuth2Constants.ERROR_INVALID_REQUEST,
                                                 "Token uses an unsupported JWT feature: " + unsupportedErr.getMessage()));
                } catch (ExpiredJwtException expiredErr) {
                    challenges.add(new Challenge(401, OAuth2Constants.ERROR_INVALID_TOKEN,
                                                 "Token expired: " + expiredErr.getMessage()));
                } catch (PrematureJwtException prematureErr) {
                    challenges.add(new Challenge(401, OAuth2Constants.ERROR_INVALID_TOKEN,
                                                 "Token is not yet valid, are server clocks out of sync?"));
                } catch (InvalidClaimException claimErr) {
                    challenges.add(new Challenge(401, OAuth2Constants.ERROR_INVALID_TOKEN, claimErr.getMessage()));
                } catch (JwtException jwtErr) {
                    challenges.add(new Challenge(401, OAuth2Constants.ERROR_INVALID_TOKEN, jwtErr.getMessage()));
                }
            }

            // Consider all the valid tokens to try and extract a valid username
            String username = null;
            VerifiedToken jws = null;
            for (VerifiedToken validToken : validTokens) {
                username = extractUsername(validToken.verifiedToken());
                if (StringUtils.isBlank(username)) {
                    challenges.add(new Challenge(401, OAuth2Constants.ERROR_INVALID_TOKEN,
                                                 "Failed to find a username for the user"));
                } else {
                    jws = validToken;
                    break;
                }
            }

            // If there was no valid token with a valid username present we need to send a challenge at this point
            if (jws == null) {
                // Should be at least one challenge if we reach here so just send the first challenge from our list
                Challenge challenge = challenges.get(0);
                LOGGER.warn("Request to {} not authenticated, {} challenge(s) recorded: {}", getRequestUrl(request),
                            challenges.size(), StringUtils.join(challenges, ", "));
                sendChallenge(request, response, challenge);
                return null;
            }

            // If we reach here then at least one token was considered valid, so we go ahead and prepare an
            // authenticated request that records the authenticated user identity
            MDC.put(JwtLoggingConstants.MDC_JWT_USER, username);
            LOGGER.info("Request to {} successfully authenticated as {}", getRequestUrl(request), username);
            return prepareRequest(request, jws, username);
        } catch (Throwable e) {
            sendError(response, e);
        }

        return null;
    }

    /**
     * Checks whether the request has the necessary authentication parameters present.
     * <p>
     * Depending on the implementation this may be HTTP Headers but it could be some other mechanism e.g. API key in the
     * querystring, token in the authentication part of the URL etc.  Implementations do not need to make any decisions
     * about whether the parameters are valid, that will happen later.
     * </p>
     * <p>
     * If no required parameters are present then the authentication engine aborts early.
     * </p>
     *
     * @param request Request
     * @return True if required parameters are present, false otherwise
     */
    protected abstract boolean hasRequiredParameters(TRequest request);

    /**
     * Extracts the raw token(s) from the request
     *
     * @param request Request
     * @return Raw token(s), or an empty list if no token(s) could be extracted
     */
    protected abstract List<TokenCandidate> extractTokens(TRequest request);

    /**
     * Extracts the username from the parsed JWT
     *
     * @param jws Parsed JWT
     * @return Username, or {@code null} if no username could be extracted
     */
    protected abstract String extractUsername(Jws<Claims> jws);

    /**
     * Prepares the authenticated request
     *
     * @param request  Request
     * @param jws      JSON Web Token
     * @param username Username
     * @return Authenticated request
     */
    protected abstract TRequest prepareRequest(TRequest request, VerifiedToken jws, String username);

    /**
     * Sends an authentication challenge
     *
     * @param request   HTTP Request
     * @param response  HTTP Response
     * @param challenge Challenge
     */
    protected abstract void sendChallenge(TRequest request, TResponse response, Challenge challenge);

    /**
     * Builds the Authorization header
     * @param realm Realm
     * @param additionalParams Map of extra parameters to potentailly apply
     * @return Authorization header
     */
    protected String buildAuthorizationHeader(String realm, Map<String, String> additionalParams) {
        StringBuilder builder = new StringBuilder();
        builder.append(JwtHttpConstants.AUTH_SCHEME_BEARER).append(' ');
        if (StringUtils.isNotBlank(realm)) {
            builder.append(JwtHttpConstants.CHALLENGE_PARAMETER_REALM).append("=\"").append(realm).append("\"");
        }
        for (Map.Entry<String, String> param : additionalParams.entrySet()) {
            builder.append(", ").append(param.getKey()).append("=");
            builder.append("\"").append(param.getValue()).append("\"");
        }
        return builder.toString();
    }

    /**
     * Builds HTTP Challenge parameters
     *
     * @param errorCode        OAuth2 Error Code
     * @param errorDescription OAuth2 Error Description
     * @return Challenge parameters to be included in the generated challenge header
     */
    protected Map<String, String> buildChallengeParameters(String errorCode, String errorDescription) {
        Map<String, String> additionalParams = new HashMap<>();
        if (StringUtils.isNotBlank(errorCode)) {
            additionalParams.put(OAuth2Constants.CHALLENGE_PARAMETER_ERROR, errorCode);
        }
        if (StringUtils.isNotBlank(errorDescription)) {
            additionalParams.put(OAuth2Constants.CHALLENGE_PARAMETER_ERROR_DESCRIPTION, errorDescription);
        }
        return additionalParams;
    }

    /**
     * Sends a server error i.e. HTTP 500
     *
     * @param response Response
     * @param err      Unexpected error
     */
    protected abstract void sendError(TResponse response, Throwable err);

    /**
     * Gets the Request URL
     *
     * @param request Request
     * @return Request URL
     */
    protected abstract String getRequestUrl(TRequest request);

}
