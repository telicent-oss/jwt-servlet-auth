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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Jwk;
import io.jsonwebtoken.security.JwkSet;
import io.telicent.servlet.auth.jwt.configuration.AbstractFactoryTests;
import io.telicent.servlet.auth.jwt.configuration.ConfigurationParameters;
import io.telicent.servlet.auth.jwt.configuration.VerificationFactory;
import io.telicent.servlet.auth.jwt.verification.JwtVerifier;
import io.telicent.servlet.auth.jwt.verification.TestKeyUtils;
import io.telicent.servlet.auth.jwt.verification.jwks.OidcServer;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.URI;
import java.time.Duration;
import java.security.Key;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TestOidcVerificationProvider extends AbstractFactoryTests {

    private static final Random RANDOM = new Random();
    private static final AtomicInteger TEST_PORT = new AtomicInteger(51000 + RANDOM.nextInt(50));

    private JwkSet jwks;
    private OidcServer server;

    @BeforeClass
    public void setup() throws Exception {
        this.jwks = TestKeyUtils.buildComplexJwks();
        this.server = new OidcServer(TEST_PORT.getAndIncrement(), this.jwks);
        this.server.start();
        OidcRegistry.reset();
    }

    @AfterMethod
    public void cleanup() {
        this.server.resetDiscoveryRequestsCount();
        OidcRegistry.reset();
    }

    @AfterClass
    public void teardown() throws Exception {
        this.server.stop();
    }


    @Test
    public void givenRawDiscoveryUriWithCorrectSuffix_whenPreparing_thenUnchanged() {
        // Given
        String rawUri = "https://example.com" + OidcVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION;

        // When
        URI preparedUri = OidcVerificationProvider.prepareDiscoveryUri(rawUri);

        // Then
        Assert.assertEquals(preparedUri.toString(), rawUri);
    }

    @DataProvider(name = "suffixes")
    private Object[][] discoveryUrisWithSuffixes() {
        return new Object[][] {
                { "/test", OidcVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION },
                { "/test/", "/test" + OidcVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION },
                { "/foo/bar", "/foo" + OidcVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION },
                { "/.well-known/wrong", OidcVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION },
                { "/.well-known/", OidcVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION },
                { "/.well-known", OidcVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION },
                {
                        "/really/deeply/nested/",
                        "/really/deeply/nested" + OidcVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION
                },
                {
                        "/realms/my-realm/.well-known/openid-configuration",
                        "/realms/my-realm/.well-known/openid-configuration"
                }
                };
    }

    @Test(dataProvider = "suffixes")
    public void givenRawDiscoveryUriWithVariousSuffixes_whenPreparing_thenCorrectSuffixApplied(String suffix,
                                                                                               String expectedSuffix) {
        // Given
        String rawUri = "https://example.com" + suffix;

        // When
        URI preparedUri = OidcVerificationProvider.prepareDiscoveryUri(rawUri);

        // Then
        Assert.assertEquals(preparedUri.toString(),
                            "https://example.com" + expectedSuffix);
    }

    @Test
    public void givenRawDiscoveryUriWithNoSuffix_whenPreparing_thenSuffixAdded() {
        // Given
        String rawUri = "https://example.com";

        // When
        URI preparedUri = OidcVerificationProvider.prepareDiscoveryUri(rawUri);

        // Then
        Assert.assertEquals(preparedUri.toString(),
                            rawUri + OidcVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION);
    }

    @Test(expectedExceptions = InvalidKeyException.class, expectedExceptionsMessageRegExp = "Unable to resolve JWKS URL.*")
    public void givenNonExistentDiscoveryUri_whenConfiguring_thenOk_andTryingToVerifyKeyFails() {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_OIDC_PROVIDER_URL, "http://localhost:65432");

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNotNull(configured.get());

        // And
        JwtVerifier verifier = configured.get();
        Key key = Jwts.SIG.HS256.key().build();
        String jwt = Jwts.builder().header().keyId("test").and().subject("test").signWith(key).compact();
        verifier.verify(jwt);
    }

    @Test(expectedExceptions = InvalidKeyException.class, expectedExceptionsMessageRegExp = "Unable to resolve JWKS URL.*and retry interval.*has not yet elapsed.*")
    public void givenNonExistentDiscoveryUri_whenConfiguring_thenOk_andTryingToVerifyKeyTwiceFailsWithRetryIntervalError() {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_OIDC_PROVIDER_URL, "http://localhost:65432");

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNotNull(configured.get());

        // And
        JwtVerifier verifier = configured.get();
        Key key = Jwts.SIG.HS256.key().build();
        String jwt = Jwts.builder().header().keyId("test").and().subject("test").signWith(key).compact();
        try {
            verifier.verify(jwt);
        } catch (InvalidKeyException e) {
            // Ignore, expect this the first time
        }
        // NB - Calling a second time should trigger a different error due to retry interval
        verifier.verify(jwt);
    }

    @Test
    public void givenValidDiscoveryUri_whenConfiguring_thenOk_andVerifyingKeySucceeds() {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        Map<String, String> config =
                Map.of(ConfigurationParameters.PARAM_OIDC_PROVIDER_URL, this.server.getConfigurationUrl());

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNotNull(configured.get());

        // And
        JwtVerifier verifier = configured.get();
        verifyCorrectlySignedJwt(verifier);
    }

    private void verifyCorrectlySignedJwt(JwtVerifier verifier) {
        String keyId = this.jwks.getKeys().stream().findFirst().map(Jwk::getId).orElse(null);
        Assert.assertNotNull(keyId);
        Key key = this.jwks.getKeys()
                           .stream()
                           .filter(k -> Objects.equals(k.getId(), keyId))
                           .map(Jwk::toKey)
                           .findFirst()
                           .orElse(null);
        Assert.assertNotNull(key);
        String jwt = Jwts.builder().header().keyId(keyId).and().subject("test").signWith(key).compact();
        Jws<Claims> jws = verifier.verify(jwt);
        Assert.assertNotNull(jws);
    }

    @Test
    public void givenTemporarilyUnavailableDiscoveryUri_whenConfiguring_thenOk_andVerifyingKeySucceedsAfterDelay() throws
            Exception {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        Map<String, String> config =
                Map.of(ConfigurationParameters.PARAM_OIDC_PROVIDER_URL, this.server.getConfigurationUrl(),
                       ConfigurationParameters.PARAM_OIDC_RETRY_INTERVAL, "1");

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNotNull(configured.get());

        // And
        JwtVerifier verifier = configured.get();
        server.stop();
        try {
            verifyCorrectlySignedJwt(verifier);
        } catch (InvalidKeyException e) {
            // Expected, ignore
        }
        server.start();
        // NB - The locator gates re-discovery on the 1 second wall-clock retry interval configured above, so poll
        //      until verification succeeds rather than sleeping for a fixed period.  The previous Thread.sleep(1500)
        //      left only a 500ms margin against that gate, which is one GC pause or one loaded CI runner away from
        //      failing, and cost a flat 1.5s even on success.
        awaitSuccessfulVerification(verifier, Duration.ofSeconds(10));
    }

    /**
     * Polls until the given verifier can successfully verify a correctly signed JWT, or the timeout expires
     *
     * @param verifier Verifier under test
     * @param timeout  Maximum time to wait
     */
    // Sonar S2925 - the remaining sleep is a short poll interval inside a bounded wait loop, not a fixed guess at
    // how long the condition takes.  Awaitility would express this more neatly but is not currently a dependency of
    // this project.
    @SuppressWarnings("java:S2925")
    private void awaitSuccessfulVerification(JwtVerifier verifier, Duration timeout) throws InterruptedException {
        long deadlineNanos = System.nanoTime() + timeout.toNanos();
        RuntimeException lastError = null;
        while (System.nanoTime() < deadlineNanos) {
            try {
                verifyCorrectlySignedJwt(verifier);
                return;
            } catch (InvalidKeyException e) {
                lastError = e;
                Thread.sleep(100);
            }
        }
        throw new AssertionError(
                "JWT verification did not succeed within " + timeout + ", last error: " + (lastError != null ?
                                                                                           lastError.getMessage() :
                                                                                           "none"), lastError);
    }

    @Test
    public void givenValidDiscoveryUri_whenConfiguring_thenOk_andVerifyingKeySucceedsPostDiscoveryEvenOnceServerIsUnavailable() throws
            Exception {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        Map<String, String> config =
                Map.of(ConfigurationParameters.PARAM_OIDC_PROVIDER_URL, this.server.getConfigurationUrl());

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNotNull(configured.get());

        // And
        JwtVerifier verifier = configured.get();
        verifyCorrectlySignedJwt(verifier);
        Assert.assertEquals(server.getDiscoveryRequestsCount(), 1);
        try {
            this.server.stop();
            verifyCorrectlySignedJwt(verifier);
            Assert.assertEquals(server.getDiscoveryRequestsCount(), 1);
        } finally {
            this.server.start();
        }
    }
}
