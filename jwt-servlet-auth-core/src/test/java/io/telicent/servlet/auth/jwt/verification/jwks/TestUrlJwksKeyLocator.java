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
import io.jsonwebtoken.Locator;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Jwk;
import io.jsonwebtoken.security.JwkSet;
import io.telicent.servlet.auth.jwt.verification.SignedJwtVerifier;
import io.telicent.servlet.auth.jwt.verification.TestKeyUtils;
import org.apache.commons.lang3.Strings;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.security.Key;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUrlJwksKeyLocator {

    private final HttpClient client = HttpClient.newBuilder().build();

    private JwkSet jwks;
    private JwksServer server;
    private Object[][] keyIds;
    private URI jwksFile;

    private static final Random RANDOM = new Random();
    private static final AtomicInteger TEST_PORT = new AtomicInteger(50000 + RANDOM.nextInt(50));

    @BeforeClass
    public void setup() throws Exception {
        // Generate a JWKS and save it to a file
        this.jwks = TestKeyUtils.buildComplexJwks();
        try {
            this.jwksFile = URI.create("file://" + TestKeyUtils.saveJwks(this.jwks).getAbsolutePath());
        } catch (IllegalArgumentException e) {
            // Ignore, probably means we're running on Windows where file:// URIs don't work nicely because of the drive
            // letter component in the absolute path
        }

        // Build the array of Key IDs for data provider for other tests
        int totalKeys = this.jwks.getKeys().size();
        this.keyIds = new Object[totalKeys][1];
        Jwk<?>[] keys = this.jwks.getKeys().toArray(new Jwk[0]);
        for (int i = 0; i < totalKeys; i++) {
            this.keyIds[i][0] = keys[i].getId();
        }

        // Run up a server that provides the JWKS over HTTP
        this.server = new JwksServer(TEST_PORT.getAndIncrement(), this.jwks);
        this.server.start();
    }

    @AfterClass
    public void teardown() throws Exception {
        this.server.stop();
    }

    private void ensureJwksFileUri() {
        if (this.jwksFile == null) {
            throw new SkipException("Skipping due to inability of this OS to create valid file:// URIs");
        }
    }

    @DataProvider(name = "keyIds")
    public Object[][] keyIds() {
        return this.keyIds;
    }

    @Test(dataProvider = "keyIds")
    public void givenValidKeyId_whenLocatingViaHttp_thenKeyIsReturned(String keyId) {
        // Given
        UrlJwksKeyLocator locator = new UrlJwksKeyLocator(URI.create(this.server.getUrl()), this.client);

        // When
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn(keyId);
        Key key = locator.locate(header);

        // Then
        Assert.assertNotNull(key);
    }

    @Test(dataProvider = "keyIds")
    public void givenValidKeyId_whenLocatingViaHttpRepeatedly_thenKeyIsReturned(String keyId) {
        // Given
        UrlJwksKeyLocator locator = new UrlJwksKeyLocator(URI.create(this.server.getUrl()), this.client);
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn(keyId);

        for (int i = 0; i < 10; i++) {
            // When
            Key key = locator.locate(header);

            // Then
            Assert.assertNotNull(key);
        }
    }

    @Test(dataProvider = "keyIds")
    public void givenValidKeyIdAndCachingLocator_whenLocatingViaHttpRepeatedly_thenKeyIsReturned(String keyId) {
        // Given
        Locator<Key> locator =
                new CachedJwksKeyLocator(URI.create(this.server.getUrl()), this.client, Duration.ofMinutes(5));
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn(keyId);

        for (int i = 0; i < 10; i++) {
            // When
            Key key = locator.locate(header);

            // Then
            Assert.assertNotNull(key);
        }
    }

    @Test(expectedExceptions = InvalidKeyException.class)
    public void givenInvalidKeyId_whenLocatingViaHttp_thenErrorIsThrown() {
        // Given
        UrlJwksKeyLocator locator = new UrlJwksKeyLocator(URI.create(this.server.getUrl()), this.client);
        String keyId = "no-such-key";

        // When and Then
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn(keyId);
        locator.locate(header);
    }

    @Test(expectedExceptions = InvalidKeyException.class)
    public void givenMissingKeyId_whenLocatingViaHttp_thenErrorIsThrown() {
        // Given
        UrlJwksKeyLocator locator = new UrlJwksKeyLocator(URI.create(this.server.getUrl()), this.client);

        // When and Then
        JwsHeader header = mock(JwsHeader.class);
        locator.locate(header);
    }

    @Test(dataProvider = "keyIds")
    public void givenValidKeyId_whenLocatingViaFile_thenKeyIsReturned(String keyId) {
        // Given
        ensureJwksFileUri();
        UrlJwksKeyLocator locator = new UrlJwksKeyLocator(this.jwksFile, this.client);

        // When
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn(keyId);
        Key key = locator.locate(header);

        // Then
        Assert.assertNotNull(key);
    }

    @Test(dataProvider = "keyIds")
    public void givenValidKeyId_whenLocatingViaFileRepeatedly_thenKeyIsReturned(String keyId) {
        // Given
        ensureJwksFileUri();
        Locator<Key> locator = new UrlJwksKeyLocator(this.jwksFile, this.client);
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn(keyId);

        for (int i = 0; i < 10; i++) {
            // When
            Key key = locator.locate(header);

            // Then
            Assert.assertNotNull(key);
        }
    }

    @Test(dataProvider = "keyIds")
    public void givenValidKeyIdAndCachingLocator_whenLocatingViaFileRepeatedly_thenKeyIsReturned(String keyId) {
        // Given
        ensureJwksFileUri();
        Locator<Key> locator = new CachedJwksKeyLocator(this.jwksFile, Duration.ofMinutes(5));
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn(keyId);

        for (int i = 0; i < 10; i++) {
            // When
            Key key = locator.locate(header);

            // Then
            Assert.assertNotNull(key);
        }
    }

    @Test(expectedExceptions = InvalidKeyException.class)
    public void givenInvalidKeyId_whenLocatingViaFile_thenErrorIsThrown() {
        // Given
        ensureJwksFileUri();
        UrlJwksKeyLocator locator = new UrlJwksKeyLocator(this.jwksFile, this.client);
        String keyId = "no-such-key";

        // When and Then
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn(keyId);
        locator.locate(header);
    }

    @Test(expectedExceptions = InvalidKeyException.class)
    public void givenMissingKeyId_whenLocatingViaFile_thenErrorIsThrown() {
        // Given
        ensureJwksFileUri();
        UrlJwksKeyLocator locator = new UrlJwksKeyLocator(this.jwksFile, this.client);

        // When and Then
        JwsHeader header = mock(JwsHeader.class);
        locator.locate(header);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".* any of the supported schemes.*")
    public void givenUnsupportedJwksUrl_whenCreatingLocator_thenErrorIsThrown() {
        // Given
        URI jwksUri = URI.create("ftp://example.org/files/jwks.json");

        // When and Then
        new UrlJwksKeyLocator(jwksUri);
    }

    @Test(dataProvider = "keyIds", expectedExceptions = InvalidKeyException.class)
    public void givenNonExistentFileJwksUrl_whenLocatingViaFile_thenErrorIsThrown(String keyId) {
        // Given
        URI jwksUri = URI.create("file:///no-such-file.json");
        UrlJwksKeyLocator locator = new UrlJwksKeyLocator(jwksUri);

        // When and Then
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn(keyId);
        locator.locate(header);
    }

    @Test
    public void givenValidJwksUrl_whenCreatingSignedVerifier_thenUrlIsPresentInToString() {
        // Given
        ensureJwksFileUri();

        // When
        SignedJwtVerifier verifier = new SignedJwtVerifier(new UrlJwksKeyLocator(this.jwksFile));

        // Then
        Assert.assertTrue(Strings.CS.contains(verifier.toString(), "verificationMethod=Locator"));
        Assert.assertTrue(Strings.CS.contains(verifier.toString(), "jwksUrl=" + this.jwksFile.toString()));
    }

    @Test
    public void givenValidJwksUrl_whenCreatingSignedVerifierWithCachedLocator_thenUrlAndCacheDurationIsPresentInToString() {
        // Given
        ensureJwksFileUri();
        Duration cacheKeysFor = Duration.ofMinutes(5);

        // When
        SignedJwtVerifier verifier = new SignedJwtVerifier(new CachedJwksKeyLocator(this.jwksFile, cacheKeysFor));

        // Then
        Assert.assertTrue(Strings.CS.contains(verifier.toString(), "verificationMethod=Locator"));
        Assert.assertTrue(Strings.CS.contains(verifier.toString(), "jwksUrl=" + this.jwksFile.toString()));
        Assert.assertTrue(Strings.CS.contains(verifier.toString(), "cacheKeysFor=" + cacheKeysFor.toString()));
    }
}
