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

import io.telicent.servlet.auth.jwt.errors.AuthenticationConfigurationError;
import io.telicent.servlet.auth.jwt.verification.FakeTokenVerifier;
import io.telicent.servlet.auth.jwt.verification.InvalidTokenVerifier;
import io.telicent.servlet.auth.jwt.verification.JwtVerifier;
import io.telicent.servlet.auth.jwt.verification.SubjectlessTokenVerifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractFilterTests<TRequest, TResponse, TFilter extends AbstractJwtAuthFilter<TRequest, TResponse>>
        extends AbstractTests<TRequest, TResponse> {

    /**
     * Creates a mock request with the given Request URI and headers
     *
     * @param requestUri Request URI
     * @param headers    Headers
     * @return Mock request
     */
    protected abstract TRequest createMockRequest(String requestUri, Map<String, String> headers);

    /**
     * Creates an instance of the filter configured based on the given parameters
     *
     * @param engine     Authentication engine
     * @param verifier   JWT Verifier
     * @param exclusions Path exclusions, may be {@code null} to indicate no exclusions
     * @return Filter
     */
    protected abstract TFilter createFilter(JwtAuthenticationEngine<TRequest, TResponse> engine, JwtVerifier verifier,
                                            List<PathExclusion> exclusions);

    /**
     * Invokes the filter with the given request and response objects
     *
     * @param filter   Filter
     * @param request  Request
     * @param response Response
     */
    protected abstract void invokeFilter(TFilter filter, TRequest request, TResponse response);

    /**
     * Verifies that no Authentication challenge was issued
     *
     * @param request  Request
     * @param response Response
     */
    protected abstract void verifyNoChallenge(TRequest request, TResponse response);

    /**
     * Gets the authenticated user (if any)
     * @param filter Filter
     * @param request Request
     * @return Authenticated user, or {@code null} if not authenticated
     */
    protected abstract String getAuthenticatedUser(TFilter filter, TRequest request);

    @Test(expectedExceptions = AuthenticationConfigurationError.class)
    public void givenUnconfiguredFilter_whenFiltering_thenErrors() {
        TFilter filter = createFilter(null, null, null);
        invokeFilter(filter, createMockRequest(Collections.emptyMap()), createMockResponse());
    }

    @Test(expectedExceptions = AuthenticationConfigurationError.class)
    public void givenPartiallyConfiguredFilter_whenFiltering_thenErrors() {
        TFilter filter = createFilter(createEngine(), null, null);
        invokeFilter(filter, createMockRequest(Collections.emptyMap()), createMockResponse());
    }

    @Test
    public void givenNoAuthHeaders_whenFiltering_thenRejected() throws IOException {
        // Given
        TFilter filter = createFilter(createEngine(), new FakeTokenVerifier(), null);
        TRequest request = createMockRequest(Collections.emptyMap());
        TResponse response = createMockResponse();

        // When
        invokeFilter(filter, request, response);

        // Then
        verifyChallenge(request, response, 401, "Bearer", "No authentication");
    }

    @Test
    public void givenIncompleteAuthHeader_whenFiltering_thenRejected() throws IOException {
        // Given
        TFilter filter = createFilter(createEngine(), new FakeTokenVerifier(), null);
        TRequest request = createMockRequest(Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer"));
        TResponse response = createMockResponse();

        // When
        invokeFilter(filter, request, response);

        // Then
        verifyChallenge(request, response, 400, "Bearer", "invalid_request");
    }

    @Test
    public void givenInvalidToken_whenFiltering_thenRejected() throws IOException {
        // Given
        TFilter filter = createFilter(createEngine(), new InvalidTokenVerifier(), null);
        TRequest request = createMockRequest(Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer foo"));
        TResponse response = createMockResponse();

        // When
        invokeFilter(filter, request, response);

        // Then
        verifyChallenge(request, response, 401, "Bearer", "invalid_token");
    }

    @Test
    public void givenTokenWithoutUsername_whenFiltering_thenRejected() throws IOException {
        // Given
        TFilter filter = createFilter(createEngine(), new SubjectlessTokenVerifier(), null);
        TRequest request = createMockRequest(Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer foo"));
        TResponse response = createMockResponse();

        // When
        invokeFilter(filter, request, response);

        // Then
        verifyChallenge(request, response, 401, "Bearer", "invalid_token", "Failed to find a username");
    }

    @Test
    public void givenPathExclusions_whenFilteringForExcludedPath_thenNoChallenge() {
        // Given
        TFilter filter =
                createFilter(createEngine(), new FakeTokenVerifier(), PathExclusion.parsePathPatterns("/healthz"));
        TRequest request = createMockRequest("/healthz", Collections.emptyMap());
        TResponse response = createMockResponse();

        // When
        invokeFilter(filter, request, response);

        // Then
        verifyNoChallenge(request, response);
    }

    @Test
    public void givenPathExclusions_whenFilteringForNonExcludedPath_thenRejected() throws IOException {
        // Given
        TFilter filter =
                createFilter(createEngine(), new FakeTokenVerifier(), PathExclusion.parsePathPatterns("/healthz"));
        TRequest request = createMockRequest("/foo", Collections.emptyMap());
        TResponse response = createMockResponse();

        // When
        invokeFilter(filter, request, response);

        // Then
        verifyChallenge(request, response, 401, "Bearer");
    }

    @Test
    public void givenWildcardPathExclusion_whenFilteringForMatchingPath_thenNoChallenge_andNonExcludedPathsAreRejected() throws IOException {
        // Given
        TFilter filter =
                createFilter(createEngine(), new FakeTokenVerifier(), PathExclusion.parsePathPatterns("/status/*"));
        TRequest request = createMockRequest("/status/health", Collections.emptyMap());
        TResponse response = createMockResponse();

        // When
        invokeFilter(filter, request, response);

        // Then
        verifyNoChallenge(request, response);

        // And
        request = createMockRequest("/other", Collections.emptyMap());
        response = createMockResponse();
        invokeFilter(filter, request, response);
        verifyChallenge(request, response, 401);
    }

    @Test
    public void givenPathExclusions_whenFilteringForNonExcludedPath_thenRejected_andRequestsWithAuthPermitted() throws IOException {
        // Given
        TFilter filter =
                createFilter(createEngine(), new FakeTokenVerifier(), PathExclusion.parsePathPatterns("/status/*"));
        TRequest request = createMockRequest("/other", Collections.emptyMap());
        TResponse response = createMockResponse();

        // When
        invokeFilter(filter, request, response);

        // Then
        verifyChallenge(request, response, 401);

        // And
        request = createMockRequest("/other", Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer foo"));
        response = createMockResponse();
        invokeFilter(filter, request, response);
        Assert.assertEquals(getAuthenticatedUser(filter, request), "foo");
    }

    @Test
    public void givenValidToken_whenFiltering_thenAuthenticated() {
        // Given
        TFilter filter = createFilter(createEngine(), new FakeTokenVerifier(), null);
        TRequest request = createMockRequest(Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer foo"));
        TResponse response = createMockResponse();

        // When
        invokeFilter(filter, request, response);

        // Then
        Assert.assertEquals(getAuthenticatedUser(filter, request), "foo");
    }
}
