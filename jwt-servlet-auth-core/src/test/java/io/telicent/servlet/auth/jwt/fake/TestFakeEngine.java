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

import io.telicent.servlet.auth.jwt.AbstractHeaderBasedEngineTests;
import io.telicent.servlet.auth.jwt.JwtHttpConstants;
import io.telicent.servlet.auth.jwt.JwtAuthenticationEngine;
import io.telicent.servlet.auth.jwt.sources.HeaderSource;
import io.telicent.servlet.auth.jwt.verification.JwtVerifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class TestFakeEngine extends AbstractHeaderBasedEngineTests<FakeRequest, FakeResponse> {
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
        return new FakeEngine(authHeaders, realm, usernameClaims);
    }

    @Override
    protected void verifyStatusCode(FakeRequest fakeRequest, FakeResponse fakeResponse, int expectedStatus) {
        Assert.assertEquals(fakeResponse.status, expectedStatus);
    }

    @Override
    protected String verifyHeaderPresent(FakeRequest fakeRequest, FakeResponse fakeResponse, String expectedHeader) {
        Assert.assertTrue(fakeResponse.headers.containsKey(expectedHeader));
        return fakeResponse.headers.get(expectedHeader).get(0);
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

    private static final class NoHeadersFakeEngine extends FakeEngine {
        public NoHeadersFakeEngine() {
            super(List.of(), null, null);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*cannot be empty")
    public void no_headers_configured_01() {
        new NoHeadersFakeEngine();
    }

    private static final class NoRequirementsFakeEngine extends FakeEngine {
        public NoRequirementsFakeEngine(String header, String headerPrefix) {
            super(header, headerPrefix, null, null);
        }


        @Override
        protected boolean hasRequiredParameters(FakeRequest fakeRequest) {
            return true;
        }
    }

    @Test
    public void no_headers_required_01() {
        NoRequirementsFakeEngine engine =
                new NoRequirementsFakeEngine(CUSTOM_AUTH_HEADER, JwtHttpConstants.AUTH_SCHEME_BEARER);
        FakeRequest request = createMockRequest(Collections.emptyMap());
        FakeResponse response = createMockResponse();
        JwtVerifier verifier = mock(JwtVerifier.class);

        engine.authenticate(request, response, verifier);

        verifyStatusCode(request, response, 400);
    }
}
