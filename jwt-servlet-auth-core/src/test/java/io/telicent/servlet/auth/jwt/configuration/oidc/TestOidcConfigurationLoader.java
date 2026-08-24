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
package io.telicent.servlet.auth.jwt.configuration.oidc;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestOidcConfigurationLoader {

    private static final URI DISCOVERY_URI = URI.create("https://example.org/.well-known/openid-configuration");

    @Test(expectedExceptions = NullPointerException.class)
    public void givenNullHttpClient_whenCreatingLoader_thenNPE() {
        // Given, When and Then
        new OidcConfigurationLoader(null);
    }

    @Test
    public void givenInterruptedHttpClient_whenLoadingConfiguration_thenNull_andInterruptFlagRestored() throws
            Exception {
        // Given
        HttpClient client = mock(HttpClient.class);
        when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(
                new InterruptedException("Simulated interrupt"));
        OidcConfigurationLoader loader = new OidcConfigurationLoader(client);

        // When
        OidcConfiguration configuration = loader.load(DISCOVERY_URI);
        // NB - Thread.interrupted() also clears the flag so that it cannot leak into subsequent tests
        boolean interrupted = Thread.interrupted();

        // Then
        Assert.assertNull(configuration, "Loading should fail softly and return null");
        Assert.assertTrue(interrupted, "Expected the interrupt flag to have been restored on the current thread");
    }
}
