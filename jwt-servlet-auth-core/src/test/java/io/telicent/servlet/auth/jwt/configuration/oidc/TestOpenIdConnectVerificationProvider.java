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
import java.security.Key;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TestOpenIdConnectVerificationProvider extends AbstractFactoryTests {

    private static final Random RANDOM = new Random();
    private static final AtomicInteger TEST_PORT = new AtomicInteger(51000 + RANDOM.nextInt(50));

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


    @Test
    public void givenRawDiscoveryUriWithCorrectSuffix_whenPreparing_thenUnchanged() {
        // Given
        String rawUri = "https://example.com" + OpenIdConnectVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION;

        // When
        URI preparedUri = OpenIdConnectVerificationProvider.prepareDiscoveryUri(rawUri);

        // Then
        Assert.assertEquals(preparedUri.toString(), rawUri);
    }

    @DataProvider(name = "wrongSuffixes")
    private Object[][] discoveryUrisWithWrongSuffix() {
        return new Object[][] {
                { "/test" }, { "/test/" }, { "/foo/bar" }, { "/.well-known/wrong" }
        };
    }

    @Test(dataProvider = "wrongSuffixes")
    public void givenRawDiscoveryUriWithWrongSuffix_whenPreparing_thenCorrectSuffixApplied(String wrongSuffix) {
        // Given
        String rawUri = "https://example.com" + wrongSuffix;

        // When
        URI preparedUri = OpenIdConnectVerificationProvider.prepareDiscoveryUri(rawUri);

        // Then
        Assert.assertEquals(preparedUri.toString(),
                            "https://example.com" + OpenIdConnectVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION);
    }

    @Test
    public void givenRawDiscoveryUriWithNoSuffix_whenPreparing_thenSuffixAdded() {
        // Given
        String rawUri = "https://example.com";

        // When
        URI preparedUri = OpenIdConnectVerificationProvider.prepareDiscoveryUri(rawUri);

        // Then
        Assert.assertEquals(preparedUri.toString(),
                            rawUri + OpenIdConnectVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION);
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
        Thread.sleep(1500);
        verifyCorrectlySignedJwt(verifier);
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
