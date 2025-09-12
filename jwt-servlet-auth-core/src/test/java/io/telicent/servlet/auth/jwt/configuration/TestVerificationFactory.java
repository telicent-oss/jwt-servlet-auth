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
package io.telicent.servlet.auth.jwt.configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.MacAlgorithm;
import io.jsonwebtoken.security.PublicJwk;
import io.jsonwebtoken.security.RsaPublicJwk;
import io.telicent.servlet.auth.jwt.verification.JwtVerifier;
import io.telicent.servlet.auth.jwt.verification.KeyUtils;
import io.telicent.servlet.auth.jwt.verification.TestKeyUtils;
import org.apache.commons.lang3.Strings;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TestVerificationFactory extends AbstractFactoryTests {

    @Test
    public void givenNoConfig_whenConfiguringVerifier_thenNothingIsConfigured() {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();

        // When
        VerificationFactory.configure(NULL_PARAM_SUPPLIER, x -> configured.set(x));

        // Then
        Assert.assertNull(configured.get());
    }

    @DataProvider(name = "secretKeyAlgorithms")
    public static Object[][] secretKeyAlgorithms() {
        return TestKeyUtils.secretKeyAlgorithms();
    }

    @DataProvider(name = "publicKeyAlgorithms")
    public static Object[][] publicKeyAlgorithms() throws IOException {
        String rsaKey = TestKeyUtils.readKeyFromResource(TestKeyUtils.TEST_RSA_KEY_RESOURCE);
        String ecKey = TestKeyUtils.readKeyFromResource(TestKeyUtils.TEST_ECDSA_KEY_RESOURCE);

        return new Object[][] {
                { KeyUtils.RSA, TestKeyUtils.saveKeyToFile(rsaKey.getBytes(StandardCharsets.US_ASCII)) },
                { KeyUtils.EC, TestKeyUtils.saveKeyToFile(ecKey.getBytes(StandardCharsets.US_ASCII)) }
        };
    }

    @Test
    public void givenNoKeyConfiguration_whenConfiguringVerifier_thenNothingIsConfigured() throws
            IOException {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_ALLOWED_CLOCK_SKEW, "10");

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNull(configured.get());
    }

    @Test(dataProvider = "publicKeyAlgorithms")
    public void givenPublicKeyConfiguration_whenConfiguringVerifier_thenVerifierIsConfigured(
            String algorithm, File keyFile) {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_PUBLIC_KEY, keyFile.getAbsolutePath(),
                                            ConfigurationParameters.PARAM_KEY_ALGORITHM, algorithm);

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNotNull(configured.get());
        Assert.assertTrue(Strings.CS.contains(configured.get().toString(), "verificationMethod=PublicKey"));
        Assert.assertTrue(Strings.CS.contains(configured.get().toString(), "fingerprint="));
    }

    @Test(dataProvider = "publicKeyAlgorithms")
    public void givenBadPublicKeyAlgorithmConfiguration_whenConfiguringVerifier_thenNothingIsConfigured(
            String algorithm, File keyFile) {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_PUBLIC_KEY, keyFile.getAbsolutePath(),
                                            ConfigurationParameters.PARAM_KEY_ALGORITHM, "no-such-algorithm");

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNull(configured.get());
    }

    @Test(dataProvider = "publicKeyAlgorithms")
    public void givenBadPublicKeyNoAlgorithmConfiguration_whenConfiguringVerifier_thenNothingIsConfigured(
            String algorithm, File keyFile) {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_PUBLIC_KEY, keyFile.getAbsolutePath());

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNull(configured.get());
    }

    @Test(dataProvider = "publicKeyAlgorithms")
    public void givenBadPublicKeyNonExistentFileConfiguration_whenConfiguringVerifier_thenNothingIsConfigured(
            String algorithm, File keyFile) {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_PUBLIC_KEY, "no-such-file.key",
                                            ConfigurationParameters.PARAM_KEY_ALGORITHM, algorithm);

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNull(configured.get());
    }

    @Test(dataProvider = "secretKeyAlgorithms")
    public void givenSecretKeyConfiguration_whenConfiguringVerifier_thenVerifierIsConfigured(
            MacAlgorithm algorithm) throws
            IOException {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        byte[] data = algorithm.key().build().getEncoded();
        File secretKey = TestKeyUtils.saveKeyToFile(data);
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_SECRET_KEY, secretKey.getAbsolutePath());

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNotNull(configured.get());
        Assert.assertTrue(Strings.CS.contains(configured.get().toString(), "verificationMethod=SecretKey"));
    }

    @Test
    public void givenBadSecretKeyConfiguration_whenConfiguringVerifier_thenNothingIsConfigured() throws
            IOException {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        byte[] data = new byte[10];
        File secretKey = TestKeyUtils.saveKeyToFile(data);
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_SECRET_KEY, secretKey.getAbsolutePath());

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNull(configured.get());
    }

    @Test(dataProvider = "secretKeyAlgorithms")
    public void givenSecretKeyPlusAdditionalConfiguration_whenConfiguringVerifier_thenVerifierIsConfigured(
            MacAlgorithm algorithm) throws
            IOException {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        byte[] data = algorithm.key().build().getEncoded();
        File secretKey = TestKeyUtils.saveKeyToFile(data);
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_SECRET_KEY, secretKey.getAbsolutePath(),
                                            ConfigurationParameters.PARAM_ALLOWED_CLOCK_SKEW, "10");

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNotNull(configured.get());
    }

    @Test(dataProvider = "secretKeyAlgorithms")
    public void givenSecretKeyPlusMalformedAdditionalConfiguration_whenConfiguringVerifier_thenVerifierIsConfigured(
            MacAlgorithm algorithm) throws
            IOException {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        byte[] data = algorithm.key().build().getEncoded();
        File secretKey = TestKeyUtils.saveKeyToFile(data);
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_SECRET_KEY, secretKey.getAbsolutePath(),
                                            ConfigurationParameters.PARAM_ALLOWED_CLOCK_SKEW, "not a number");

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNotNull(configured.get());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends PublicKey> File saveJwks(PublicJwk<T> jwks) throws IOException {
        File file = Files.createTempFile("jwks", ".json").toFile();
        try (FileOutputStream output = new FileOutputStream(file)) {
            new JacksonSerializer().serialize(jwks, output);
        }
        return file;
    }

    @Test
    public void givenJwksConfiguration_whenConfiguringVerifier_thenVerifierIsConfigured() throws IOException {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        RSAPublicKey key = (RSAPublicKey) Jwts.SIG.RS512.keyPair().build().getPublic();
        RsaPublicJwk jwk = Jwks.builder().key(key).idFromThumbprint().build();
        File jwksFile = saveJwks(jwk);
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_JWKS_URL, jwksFile.toURI().toString());

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNotNull(configured.get());
        Assert.assertTrue(Strings.CS.contains(configured.get().toString(), "verificationMethod=Locator"));
        Assert.assertTrue(Strings.CS.contains(configured.get().toString(), "jwksUrl=" + jwksFile.toURI()));
    }

    @Test
    public void givenJwksConfigurationUsingPlainFilename_whenConfiguringVerifier_thenVerifierIsConfigured() throws
            IOException {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        RSAPublicKey key = (RSAPublicKey) Jwts.SIG.RS512.keyPair().build().getPublic();
        RsaPublicJwk jwk = Jwks.builder().key(key).idFromThumbprint().build();
        File jwksFile = saveJwks(jwk);
        URI jwksUri;
        try {
            jwksUri = URI.create("file://" + jwksFile.getAbsolutePath());
        } catch (IllegalArgumentException e) {
            throw new SkipException("File URIs are not usable on this OS");
        }
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_JWKS_URL, jwksUri.toString());

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNotNull(configured.get());
        Assert.assertTrue(Strings.CS.contains(configured.get().toString(), "verificationMethod=Locator"));
        Assert.assertTrue(Strings.CS.contains(configured.get().toString(), jwksFile.getAbsolutePath()));
    }

    @Test
    public void givenBadJwksConfiguration_whenConfiguringVerifier_thenNothingIsConfigured() throws IOException {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_JWKS_URL, "not a valid URL");

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNull(configured.get());
    }

    @Test
    public void givenBadJwksConfigurationUsingPlainFilename_whenConfiguringVerifier_thenNothingIsConfigured() throws
            IOException {
        // Given
        AtomicReference<JwtVerifier> configured = new AtomicReference<>();
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_JWKS_URL, "no-such-file.json");

        // When
        VerificationFactory.configure(supplierForMap(config), configured::set);

        // Then
        Assert.assertNull(configured.get());
    }
}
