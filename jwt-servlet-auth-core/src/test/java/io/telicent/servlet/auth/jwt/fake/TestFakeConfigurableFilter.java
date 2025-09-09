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
package io.telicent.servlet.auth.jwt.fake;

import io.telicent.servlet.auth.jwt.*;
import io.telicent.servlet.auth.jwt.configuration.MapRuntimeConfigAdaptor;
import io.telicent.servlet.auth.jwt.configuration.RuntimeConfigurationAdaptor;
import io.telicent.servlet.auth.jwt.sources.HeaderSource;
import io.telicent.servlet.auth.jwt.verification.JwtVerifier;
import org.testng.Assert;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestFakeConfigurableFilter
        extends AbstractConfigurableFilterTests<FakeRequest, FakeResponse, FakeConfigurableFilter> {
    @Override
    protected RuntimeConfigurationAdaptor createConfigAdaptor(Map configuration) {
        return new MapRuntimeConfigAdaptor(configuration);
    }

    @Override
    protected FakeConfigurableFilter createUnconfiguredFilter() {
        return new FakeConfigurableFilter();
    }

    @Override
    protected FakeRequest createMockRequest(FilterConfigAdaptorWrapper config, Map<String, String> headers) {
        return new FakeRequest(headers);
    }

    @Override
    protected FakeRequest createMockRequest(String requestUri, Map<String, String> headers) {
        return new FakeRequest(headers, requestUri);
    }

    @Override
    protected FakeConfigurableFilter createFilter(JwtAuthenticationEngine<FakeRequest, FakeResponse> engine,
                                                  JwtVerifier verifier, List<PathExclusion> exclusions) {
        return new FakeConfigurableFilter(engine, verifier, exclusions);
    }

    @Override
    protected void invokeFilter(FakeConfigurableFilter filter, FakeRequest fakeRequest, FakeResponse fakeResponse) {
        filter.doFilter(fakeRequest, fakeResponse, (authenticatedRequest, authenticatedResponse) -> {});
    }

    @Override
    protected void verifyNoChallenge(FakeRequest fakeRequest, FakeResponse fakeResponse) {
        Assert.assertNull(verifyHeaderPresent(fakeRequest, fakeResponse, JwtHttpConstants.HEADER_WWW_AUTHENTICATE));
    }

    @Override
    protected String getAuthenticatedUser(FakeConfigurableFilter filter, FakeRequest fakeRequest) {
        return fakeRequest.username;
    }

    @Override
    protected FakeRequest createMockRequest(Map<String, String> headers) {
        return new FakeRequest(headers);
    }

    @Override
    protected FakeResponse createMockResponse() {
        return new FakeResponse();
    }

    @Override
    protected JwtAuthenticationEngine<FakeRequest, FakeResponse> createEngine() {
        return new FakeEngine();
    }

    @Override
    protected JwtAuthenticationEngine<FakeRequest, FakeResponse> createEngine(String authHeader,
                                                                              String authHeaderPrefix, String realm,
                                                                              String usernameClaim) {
        return new FakeEngine(authHeader, authHeaderPrefix, realm, usernameClaim);
    }

    @Override
    protected JwtAuthenticationEngine<FakeRequest, FakeResponse> createEngine(List<HeaderSource> authHeaders,
                                                                              String realm,
                                                                              List<String> usernameClaims) {
        return new FakeEngine(authHeaders, realm, usernameClaims, null);
    }

    @Override
    protected void verifyStatusCode(FakeRequest fakeRequest, FakeResponse fakeResponse, int expectedStatus) throws
            IOException {
        Assert.assertEquals(fakeResponse.status, expectedStatus);
    }

    @Override
    protected String verifyHeaderPresent(FakeRequest fakeRequest, FakeResponse fakeResponse, String expectedHeader) {
        List<String> headers = fakeResponse.headers.get(expectedHeader);
        if (headers == null || headers.isEmpty()) {
            return null;
        } else {
            return headers.get(0);
        }
    }

    @Override
    protected String getAuthenticatedUser(FakeRequest authenticatedRequest) {
        return authenticatedRequest.username;
    }

    @Override
    protected Object verifyRequestAttribute(FakeRequest fakeRequest, String attribute) {
        Object value = fakeRequest.getAttribute(attribute);
        Assert.assertNotNull(value);
        return value;
    }
}
