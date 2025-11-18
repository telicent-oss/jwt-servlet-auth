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
package io.telicent.servlet.auth.jwt.verification.jwks;

import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Jwk;
import io.jsonwebtoken.security.JwkSet;
import io.telicent.servlet.auth.jwt.verification.TestKeyUtils;
import org.apache.commons.lang3.Strings;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.when;

public class TestOidcDiscoveryLocator {

    private static final Random RANDOM = new Random();
    private static final AtomicInteger TEST_PORT = new AtomicInteger(52000 + RANDOM.nextInt(50));

    private JwkSet jwks;
    private OidcServer server;

    @BeforeClass
    public void setup() throws Exception {
        this.jwks = TestKeyUtils.buildComplexJwks();
        this.server = new OidcServer(TEST_PORT.getAndIncrement(), this.jwks);
        this.server.start();
    }

    @AfterMethod
    public void cleanup() {
        this.server.resetDiscoveryRequestsCount();
    }

    @AfterClass
    public void teardown() throws Exception {
        this.server.stop();
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*cannot be negative")
    public void givenNegativeRetryInterval_whenCreatingLocator_thenIllegalArgument() {
        // Given, When and Then
        new OidcDiscoveryLocator(URI.create(this.server.getConfigurationUrl()), Duration.ofSeconds(-3));
    }

    @Test
    public void givenCompliantDiscoveryUrl_whenUsingLocator_thenKeyLocated_andSubsequentLocationDoesNotRequireDiscovery() throws
            Exception {
        // Given
        OidcDiscoveryLocator locator =
                new OidcDiscoveryLocator(URI.create(this.server.getConfigurationUrl()));
        JwsHeader header = Mockito.mock(JwsHeader.class);
        String keyId = this.jwks.getKeys().stream().findFirst().map(Jwk::getId).orElse(null);
        Assert.assertNotNull(keyId);
        when(header.getKeyId()).thenReturn(keyId);
        Assert.assertEquals(server.getDiscoveryRequestsCount(), 0);

        // When and Then
        Assert.assertNotNull(locator.locate(header));
        Assert.assertEquals(server.getDiscoveryRequestsCount(), 1);

        // And
        Assert.assertNotNull(locator.locate(header));
        Assert.assertEquals(server.getDiscoveryRequestsCount(), 1);
    }

    @Test
    public void givenLocator_whenToString_thenHasNoJwksUrlPriorToDiscovery_andHasJwksUrlAfterDiscovery() throws
            Exception {
        // Given
        OidcDiscoveryLocator locator =
                new OidcDiscoveryLocator(URI.create(this.server.getConfigurationUrl()));

        // When
        String stringForm = locator.toString();

        // Then
        Assert.assertTrue(Strings.CI.contains(stringForm, "<not yet discovered>"));

        // And
        JwsHeader header = Mockito.mock(JwsHeader.class);
        String keyId = this.jwks.getKeys().stream().findFirst().map(Jwk::getId).orElse(null);
        Assert.assertNotNull(keyId);
        when(header.getKeyId()).thenReturn(keyId);
        Assert.assertNotNull(locator.locate(header));
        Assert.assertTrue(Strings.CI.contains(locator.toString(), this.server.getUrl()));
    }

    @Test
    public void givenNonStandardDiscoveryUrl_whenUsingLocator_thenWarningIssued() {
        // Given and When
        OidcDiscoveryLocator locator =
                new OidcDiscoveryLocator(URI.create(this.server.getNonStandardConfigurationUrl()));
        JwsHeader header = Mockito.mock(JwsHeader.class);
        String keyId = this.jwks.getKeys().stream().findFirst().map(Jwk::getId).orElse(null);
        Assert.assertNotNull(keyId);
        when(header.getKeyId()).thenReturn(keyId);

        // Then
        Assert.assertNotNull(locator.locate(header));
    }

    @Test(expectedExceptions = InvalidKeyException.class, expectedExceptionsMessageRegExp = "Unable to resolve JWKS.*")
    public void givenDiscoveryUrlWhereConfigurationDoesNotContainJwksUrl_whenUsingLocator_thenInvalidKeyException() {
        // Given and When
        OidcDiscoveryLocator locator =
                new OidcDiscoveryLocator(URI.create(this.server.getEmptyConfigurationUrl()));
        JwsHeader header = Mockito.mock(JwsHeader.class);
        String keyId = this.jwks.getKeys().stream().findFirst().map(Jwk::getId).orElse(null);
        Assert.assertNotNull(keyId);
        when(header.getKeyId()).thenReturn(keyId);

        // Then
        locator.locate(header);
    }

    @Test(expectedExceptions = InvalidKeyException.class, expectedExceptionsMessageRegExp = "Unable to resolve JWKS.*")
    public void givenDiscoveryUrlWithNon200Response_whenUsingLocator_thenInvalidKeyException() {
        // Given and When
        OidcDiscoveryLocator locator =
                new OidcDiscoveryLocator(URI.create(this.server.getNotFoundConfigurationUrl()));
        JwsHeader header = Mockito.mock(JwsHeader.class);
        String keyId = this.jwks.getKeys().stream().findFirst().map(Jwk::getId).orElse(null);
        Assert.assertNotNull(keyId);
        when(header.getKeyId()).thenReturn(keyId);

        // Then
        locator.locate(header);
    }
}
