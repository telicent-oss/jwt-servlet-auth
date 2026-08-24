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

import io.telicent.servlet.auth.jwt.JwtHttpConstants;
import io.telicent.servlet.auth.jwt.challenges.TokenCandidate;
import io.telicent.servlet.auth.jwt.verification.FakeTokenVerifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Verifies that a JVM level {@link Error} is not swallowed and converted into an HTTP error response, which would
 * otherwise hide a classpath or memory fault behind an opaque 500 on every request.
 */
public class TestFakeEngineErrors {

    private static final String SIMULATED = "Simulated classpath failure";

    private static final class ErrorThrowingEngine extends FakeEngine {
        @Override
        protected List<TokenCandidate> extractTokens(FakeRequest fakeRequest) {
            throw new NoClassDefFoundError(SIMULATED);
        }
    }

    @Test
    public void givenEngineThrowingError_whenAuthenticating_thenErrorPropagates_andNoResponseIsSent() {
        // Given
        ErrorThrowingEngine engine = new ErrorThrowingEngine();
        FakeRequest request = new FakeRequest(Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer test"));
        FakeResponse response = new FakeResponse();

        // When
        try {
            engine.authenticate(request, response, new FakeTokenVerifier());
            Assert.fail("Expected the Error to propagate rather than being converted into a response");
        } catch (NoClassDefFoundError e) {
            // Then
            Assert.assertEquals(e.getMessage(), SIMULATED);
        }

        // And
        Assert.assertEquals(response.status, -1, "No error response should be sent for a JVM level Error");
    }
}
