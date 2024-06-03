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
package io.telicent.servlet.auth.jwt.verification;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.JwkSet;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.MacAlgorithm;
import io.telicent.servlet.auth.jwt.errors.KeyLoadException;
import io.telicent.servlet.auth.jwt.verification.jwks.JwksServer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BrokenInputStream;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TestKeyUtils {

    public static final String TEST_ECDSA_KEY_RESOURCE = "/test-ecdsa-key.pem";
    public static final String TEST_RSA_KEY_RESOURCE = "/test-rsa-key.pem";
    private String rawEcdsaKey, rawRsaKey;

    public static String readKeyFromResource(String resource) throws IOException {
        try (InputStream input = TestKeyUtils.class.getResourceAsStream(resource)) {
            if (input == null) {
                throw new IOException("No such resource " + resource);
            }
            List<String> lines = IOUtils.readLines(input, StandardCharsets.US_ASCII);
            return StringUtils.join(lines, '\n');
        }
    }

    @BeforeClass
    public void setup() {
        try {
            this.rawEcdsaKey = readKeyFromResource(TEST_ECDSA_KEY_RESOURCE);
            this.rawRsaKey = readKeyFromResource(TEST_RSA_KEY_RESOURCE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void load_rsa_key_bad() throws KeyLoadException {
        KeyUtils.loadRsaPublicKeyFromPem(this.rawEcdsaKey);
    }

    @Test
    public void load_rsa_key_good_01() throws KeyLoadException {
        KeyUtils.loadRsaPublicKeyFromPem(this.rawRsaKey);
    }

    @Test
    public void load_rsa_key_good_02() throws KeyLoadException, IOException {
        try (InputStream input = this.getClass().getResourceAsStream(TEST_RSA_KEY_RESOURCE)) {
            KeyUtils.loadPublicKey(KeyUtils.RSA, input);
        }
    }

    @Test
    public void load_rsa_key_good_03() throws KeyLoadException, IOException {
        KeyUtils.loadPublicKey(KeyUtils.RSA, new File("src/test/resources" + TEST_RSA_KEY_RESOURCE));
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void load_ecdsa_key_bad() throws KeyLoadException {
        KeyUtils.loadEcdsaPublicKeyFromPem(this.rawRsaKey);
    }

    @Test
    public void load_ecdsa_key_good_01() throws KeyLoadException {
        KeyUtils.loadEcdsaPublicKeyFromPem(this.rawEcdsaKey);
    }

    @Test
    public void load_ecdsa_key_good_02() throws KeyLoadException, IOException {
        try (InputStream input = this.getClass().getResourceAsStream(TEST_ECDSA_KEY_RESOURCE)) {
            KeyUtils.loadPublicKey(KeyUtils.EC, input);
        }
    }

    @Test
    public void load_ecdsa_key_good_03() throws KeyLoadException, IOException {
        KeyUtils.loadPublicKey(KeyUtils.EC, new File("src/test/resources" + TEST_ECDSA_KEY_RESOURCE));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void load_bad_01() throws KeyLoadException {
        KeyUtils.loadPublicKey(KeyUtils.RSA, (InputStream) null);
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void load_bad_02() throws KeyLoadException {
        KeyUtils.loadPublicKey(KeyUtils.RSA, new BrokenInputStream());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void load_bad_03() throws KeyLoadException {
        KeyUtils.loadPublicKey(KeyUtils.RSA, (File) null);
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void load_bad_04() throws KeyLoadException {
        KeyUtils.loadPublicKey(KeyUtils.RSA, new File("no-such-key"));
    }

    @DataProvider(name = "secretKeyAlgorithms")
    public static Object[][] secretKeyAlgorithms() {
        return new Object[][] {
                { Jwts.SIG.HS256 }, { Jwts.SIG.HS384 }, { Jwts.SIG.HS512 }
        };
    }

    /**
     * Saves a key to a new temporary file
     *
     * @param keyBytes Key Bytes
     * @return Temporary file containing the key data
     * @throws IOException Thrown if the temporary file can't be created/written
     */
    public static File saveKeyToFile(byte[] keyBytes) throws IOException {
        File f = Files.createTempFile("secret", ".key").toFile();
        try (FileOutputStream output = new FileOutputStream(f)) {
            output.write(keyBytes);
        }
        return f;
    }

    @Test(dataProvider = "secretKeyAlgorithms")
    public void givenASecretKey_whenLoadingFromFile_thenAValidKeyIsLoaded(MacAlgorithm algorithm) throws IOException,
            KeyLoadException {
        // Given
        SecretKey generated = algorithm.key().build();
        byte[] data = generated.getEncoded();
        File keyFile = saveKeyToFile(data);

        // When
        SecretKey loaded = KeyUtils.loadSecretKey(keyFile);

        // Then
        Assert.assertEquals(loaded.getEncoded(), data);
    }

    @Test(dataProvider = "secretKeyAlgorithms")
    public void givenASecretKeyBase64Encoded_whenLoadingFromFile_thenAValidKeyIsLoaded(MacAlgorithm algorithm) throws
            IOException, KeyLoadException {
        // Given
        SecretKey generated = algorithm.key().build();
        byte[] data = generated.getEncoded();
        File keyFile = saveKeyToFile(Base64.getEncoder().encode(data));

        // When
        SecretKey loaded = KeyUtils.loadSecretKey(keyFile);

        // Then
        Assert.assertEquals(loaded.getEncoded(), data);
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void givenEmptySecretKey_whenLoadingFromFile_thenErrorIsThrown() throws IOException, KeyLoadException {
        // Given
        File keyFile = Files.createTempFile("secret", ".key").toFile();

        // When and Then
        KeyUtils.loadSecretKey(keyFile);
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void givenNonExistentSecretKey_whenLoadingFromFile_thenErrorIsThrown() throws IOException, KeyLoadException {
        // Given
        File keyFile = new File("no-such-file.key");

        // When and Then
        KeyUtils.loadSecretKey(keyFile);
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void givenNullSecretKey_whenLoadingFromBytes_thenErrorIsThrown() throws KeyLoadException {
        // When and Then
        KeyUtils.loadSecretKey((byte[]) null);
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void givenNullSecretKey_whenLoadingFromFile_thenErrorIsThrown() throws KeyLoadException {
        // When and Then
        KeyUtils.loadSecretKey((File) null);
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void givenEmptySecretKey_whenLoadingFromBytes_thenErrorIsThrown() throws KeyLoadException {
        // Given
        byte[] data = new byte[0];

        // When and Then
        KeyUtils.loadSecretKey(data);
    }

    public static File saveJwks(JwkSet jwks) throws IOException {
        File jwksFile = Files.createTempFile("jwks", ".json").toFile();
        try (FileOutputStream output = new FileOutputStream(jwksFile)) {
            new JacksonSerializer<>().serialize(jwks, output);
        }
        return jwksFile;
    }

    @Test
    public void givenJwksFile_whenLoadingFromFile_thenSameKeysAreReturned() throws IOException, KeyLoadException {
        // Given
        JwkSet jwks =
                Jwks.set().add(Jwks.builder().key(Jwts.SIG.HS256.key().build()).idFromThumbprint().build()).build();
        File jwksFile = this.saveJwks(jwks);

        // When
        JwkSet loaded = KeyUtils.loadJwks(jwksFile);

        // Then
        Assert.assertTrue(loaded.equals(jwks));
    }

    @Test
    public void givenComplexJwksFile_whenLoadingFromFile_thenSameKeysAreReturned() throws IOException,
            KeyLoadException {
        // Given
        JwkSet jwks = buildComplexJwks();
        File jwksFile = this.saveJwks(jwks);

        // When
        JwkSet loaded = KeyUtils.loadJwks(jwksFile);

        // Then
        Assert.assertTrue(loaded.equals(jwks));
    }

    public static JwkSet buildComplexJwks() {
        return Jwks.set()
                   .add(Jwks.builder().key(Jwts.SIG.HS256.key().build()).idFromThumbprint().build())
                   .add(Jwks.builder().ecKeyPair(Jwts.SIG.ES384.keyPair().build()).idFromThumbprint().build())
                   .add(Jwks.builder().key(Jwts.SIG.RS512.keyPair().build().getPublic()).idFromThumbprint().build())
                   .build();
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void givenEmptyJwksFile_whenLoadingFromFile_thenErrorIsThrown() throws IOException, KeyLoadException {
        // Given
        File jwksFile = Files.createTempFile("jwks", ".json").toFile();

        // When and Then
        KeyUtils.loadJwks(jwksFile);
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void givenNonExistentJwksFile_whenLoadingFromFile_thenErrorIsThrown() throws IOException, KeyLoadException {
        // Given
        File jwksFile = new File("no-such-file.json");

        // When and Then
        KeyUtils.loadJwks(jwksFile);
    }

    public static final AtomicInteger JWKS_TEST_PORT = new AtomicInteger(34567);

    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();

    @Test
    public void givenJwksUrl_whenLoadingFromUrl_thenSameKeysAreReturned() throws Exception {
        // Given
        JwkSet jwks =
                Jwks.set().add(Jwks.builder().key(Jwts.SIG.HS256.key().build()).idFromThumbprint().build()).build();
        int port = JWKS_TEST_PORT.getAndIncrement();
        JwksServer server = new JwksServer(port, jwks);

        // When
        try {
            server.start();
            JwkSet loaded = KeyUtils.loadJwks(URI.create(server.getUrl()), HTTP_CLIENT);

            // Then
            Assert.assertTrue(loaded.equals(jwks));
        } finally {
            server.stop();
        }
    }

    @Test
    public void givenComplexJwksUrl_whenLoadingFromUrl_thenSameKeysAreReturned() throws Exception {
        // Given
        JwkSet jwks = buildComplexJwks();
        int port = JWKS_TEST_PORT.getAndIncrement();
        JwksServer server = new JwksServer(port, jwks);

        // When
        try {
            server.start();
            JwkSet loaded = KeyUtils.loadJwks(URI.create(server.getUrl()), HTTP_CLIENT);

            // Then
            Assert.assertTrue(loaded.equals(jwks));
        } finally {
            server.stop();
        }
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void givenBadJwksUrl_whenLoadingFromUrl_thenErrorIsThrown() throws Exception {
        // Given
        int port = JWKS_TEST_PORT.getAndIncrement();
        JwksServer server = new JwksServer(port, null);

        try {
            server.start();

            // When and Then
            KeyUtils.loadJwks(URI.create(server.getUrl()), HTTP_CLIENT);
        } finally {
            server.stop();
        }
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void givenUnreachableJwksUrl_whenLoadingFromUrl_thenErrorIsThrown() throws IOException, KeyLoadException {
        // Given
        URI jwksURI = URI.create("http://localhost:" + JWKS_TEST_PORT.get() + "/jwks.json");

        // When and Then
        KeyUtils.loadJwks(jwksURI, HTTP_CLIENT);
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void givenNoJwksUrl_whenLoadingFromUrl_thenErrorIsThrown() throws IOException, KeyLoadException {
        // Given
        URI jwksURI = null;

        // When and Then
        KeyUtils.loadJwks(jwksURI, HTTP_CLIENT);
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void givenJwksUrlAndNullClient_whenLoadingFromUrl_thenErrorIsThrown() throws IOException, KeyLoadException {
        // Given
        URI jwksURI = URI.create("https://example.org/jwks.json");

        // When and Then
        KeyUtils.loadJwks(jwksURI, null);
    }

    @Test(expectedExceptions = KeyLoadException.class)
    public void givenUnsupportedSchemaJwksUrl_whenLoadingFromUrl_thenErrorIsThrown() throws IOException, KeyLoadException {
        // Given
        URI jwksURI = URI.create("ftp://example.org/files/jwks.json");

        // When and Then
        KeyUtils.loadJwks(jwksURI, HTTP_CLIENT);
    }
}
