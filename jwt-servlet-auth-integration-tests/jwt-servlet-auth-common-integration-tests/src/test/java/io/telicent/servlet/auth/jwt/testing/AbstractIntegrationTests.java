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
package io.telicent.servlet.auth.jwt.testing;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.JwkSet;
import io.jsonwebtoken.security.Jwks;
import io.telicent.servlet.auth.jwt.JwtHttpConstants;
import io.telicent.servlet.auth.jwt.OAuth2Constants;
import io.telicent.servlet.auth.jwt.ServletConstants;
import io.telicent.servlet.auth.jwt.verification.TestKeyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract integration tests for implementation modules
 */
public abstract class AbstractIntegrationTests {
    private final HttpClient client = HttpClient.newBuilder().build();

    public static final int OK = 200;
    private static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int NOT_FOUND = 404;

    private static final AtomicInteger TEST_PORT = new AtomicInteger(12345);
    private SecretKey secretKey;
    private PrivateKey privateKey;
    private JwkSet jwks;
    private PublicKey publicKey;

    protected static void ensureWebAppExists(String appName) {
        File webApp = new File("src/test/apps/" + appName);
        if (!webApp.exists()) {
            throw new SkipException("This web.xml based application not yet provided for this implementation");
        }
    }

    @BeforeClass
    public void ensureTestKeys() throws IOException {
        File secretKeyFile = new File("test.key");
        if (this.secretKey == null) {
            SecretKey key = Jwts.SIG.HS256.key().build();
            File tempKey = TestKeyUtils.saveKeyToFile(Base64.getEncoder().encode(key.getEncoded()));
            Files.move(tempKey.toPath(), secretKeyFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.secretKey = key;
        }

        File publicKeyFile = new File("public.key");
        if (this.privateKey == null) {
            KeyPair keyPair = Jwts.SIG.RS512.keyPair().build();
            File tempPublicKey =
                    TestKeyUtils.saveKeyToFile(Base64.getEncoder().encode(keyPair.getPublic().getEncoded()));
            Files.move(tempPublicKey.toPath(), publicKeyFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
        }

        File jwksFile = new File("jwks.json");
        if (this.jwks == null) {
            JwkSet jwks = Jwks.set()
                              .add(Jwks.builder().key(this.secretKey).id("secret").build())
                              .add(Jwks.builder().key(this.publicKey).id("public").build())
                              .build();
            this.jwks = jwks;
            try (FileOutputStream output = new FileOutputStream(jwksFile)) {
                new JacksonSerializer<>().serialize(jwks, output);
            }
        }
    }

    /**
     * Makes a HTTP Request
     *
     * @param server  Server
     * @param url     URL
     * @param headers Headers
     * @param handler Body Handler
     * @param <T>     Body Handler type
     * @return Response
     * @throws IOException
     * @throws InterruptedException
     */
    public <T> HttpResponse<T> makeRequest(AbstractServer server, String url, Map<String, String> headers,
                                           HttpResponse.BodyHandler<T> handler) throws IOException,
            InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(server.getBaseUrl()).resolve(url));
        headers.entrySet().forEach(e -> builder.header(e.getKey(), e.getValue()));
        return client.send(builder.build(), handler);
    }

    /**
     * Verifies that making a given request results in a given response status
     *
     * @param server         Server
     * @param url            Request URL (relative to the server URL)
     * @param headers        Headers to add to the request
     * @param expectedStatus Expected status code
     * @return HTTP Response for further verification
     * @throws Exception May be thrown if the request fails unexpectedly
     */
    private HttpResponse<InputStream> verifyRequestResponse(AbstractServer server, String url,
                                                            Map<String, String> headers, int expectedStatus) throws
            Exception {
        try {
            // When
            server.start();
            HttpResponse<InputStream> response =
                    makeRequest(server, url, headers, HttpResponse.BodyHandlers.ofInputStream());

            // Then
            Assert.assertEquals(response.statusCode(), expectedStatus);
            return response;
        } finally {
            server.stop();
        }
    }

    /**
     * Builds a web application programmatically i.e. without using a {@code web.xml} file
     *
     * @param keyFile Key file that the server should be configured to use for JWT verification
     * @param port    The port the server should run on
     * @return Server runtime
     */
    protected abstract AbstractServer buildProgrammaticApplication(File keyFile, int port);

    /**
     * Builds a web application from the given web application name, where that name indicates a web application that
     * contains a suitable {@code WEB-INF/web.xml} file.
     * <p>
     * See the constants in {@link ApplicationConstants} to see the descriptions of the web applications an
     * implementation module <strong>SHOULD</strong> provide for testing.  If the implementation module does not contain
     * a suitable application then it should throw a {@link org.testng.SkipException} to indicate that and mark the
     * relevant test(s) as skipped.
     * </p>
     *
     * @param port    Port the server should run on
     * @param appName Web application name
     * @return Server runtime
     * @throws IOException
     */
    protected abstract AbstractServer buildWebXmlApplication(int port, String appName) throws IOException;

    @Test
    public void givenProgrammaticApplication_whenMakingRequests_thenAuthenticationIsRequired() throws Exception {
        // Given
        SecretKey key = Jwts.SIG.HS256.key().build();
        File keyFile = TestKeyUtils.saveKeyToFile(key.getEncoded());
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildProgrammaticApplication(keyFile, port);

        // When and Then
        verifyRequestResponse(server, "/hello", Map.of(), UNAUTHORIZED);
    }

    @Test
    public void givenProgrammaticApplication_whenMakingRequestsToNonExistentUrl_thenAuthenticationIsRequired() throws
            Exception {
        // Given
        SecretKey key = Jwts.SIG.HS256.key().build();
        File keyFile = TestKeyUtils.saveKeyToFile(key.getEncoded());
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildProgrammaticApplication(keyFile, port);

        // When and Then
        verifyRequestResponse(server, "/foo", Map.of(), areNonExistentUrlsFiltered() ? UNAUTHORIZED : NOT_FOUND);
    }

    /**
     * Indicates whether requests to non-existent URLs that would normally produce 404 errors are subject to auth
     * filtering and thus should produce 401s instead
     *
     * @return True if non-existent URLs are subject to filtering, false otherwise
     */
    protected boolean areNonExistentUrlsFiltered() {
        return true;
    }

    @Test
    public void givenProgrammaticApplication_whenMakingRequestsWithAuthHeader_thenRequestSucceeds() throws Exception {
        // Given
        SecretKey key = Jwts.SIG.HS256.key().build();
        File keyFile = TestKeyUtils.saveKeyToFile(key.getEncoded());
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildProgrammaticApplication(keyFile, port);
        String jwt = Jwts.builder().subject("test").signWith(key).compact();

        // When and Then
        verifyRequestResponse(server, "/hello",
                              Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                              OK);
    }

    @Test
    public void givenProgrammaticApplication_whenMakingRequestsWithAuthHeader_thenRequestSucceeds_andModifyingVerifierConfigurationHasNoEffect() throws
            Exception {
        // Given
        SecretKey key = Jwts.SIG.HS256.key().build();
        File keyFile = TestKeyUtils.saveKeyToFile(key.getEncoded());
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildProgrammaticApplication(keyFile, port);
        String jwt = Jwts.builder().subject("test").signWith(key).compact();

        try {
            // When and Then
            server.start();
            HttpResponse<InputStream> response = makeRequest(server, "/hello",
                                                             Map.of(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                                    JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                                                             HttpResponse.BodyHandlers.ofInputStream());
            Assert.assertEquals(response.statusCode(), OK);

            // And
            server.getRuntimeConfiguration()
                  .setAttribute(ServletConstants.ATTRIBUTE_JWT_VERIFIER, new RejectAllVerifier());
            response = makeRequest(server, "/hello", Map.of(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                            JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                                   HttpResponse.BodyHandlers.ofInputStream());
            Assert.assertEquals(response.statusCode(), OK);
        } finally {
            server.stop();
        }
    }

    @Test
    public void givenProgrammaticApplication_whenMakingRequestsWithExpiredJwt_thenAuthenticationIsRequired() throws
            Exception {
        // Given
        SecretKey key = Jwts.SIG.HS256.key().build();
        File keyFile = TestKeyUtils.saveKeyToFile(key.getEncoded());
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildProgrammaticApplication(keyFile, port);
        String expiredJwt = Jwts.builder()
                                .subject("test")
                                .expiration(Date.from(Instant.now().minusSeconds(60)))
                                .signWith(key)
                                .compact();

        // When and Then
        verifyRequestResponse(server, "/hello", Map.of(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                       JwtHttpConstants.AUTH_SCHEME_BEARER + " " + expiredJwt),
                              UNAUTHORIZED);
    }

    @Test
    public void givenProgrammaticApplication_whenMakingRequestsWithJwtSignedWithDifferentKey_thenAuthenticationIsRequired() throws
            Exception {
        // Given
        SecretKey key = Jwts.SIG.HS256.key().build();
        File keyFile = TestKeyUtils.saveKeyToFile(key.getEncoded());
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildProgrammaticApplication(keyFile, port);
        SecretKey badKey = Jwts.SIG.HS256.key().build();
        String jwt = Jwts.builder().subject("test").signWith(badKey).compact();

        // When and Then
        verifyRequestResponse(server, "/hello",
                              Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                              UNAUTHORIZED);
    }

    @Test
    public void givenBasicWebXmlApplication_whenMakingRequests_thenAuthenticationIsRequired() throws Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.BASIC);

        verifyRequestResponse(server, "/hello", Map.of(), UNAUTHORIZED);
    }

    @Test
    public void givenBasicWebXmlApplication_whenMakingRequestsToNonExistentUrl_thenAuthenticationIsRequired() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.BASIC);

        verifyRequestResponse(server, "/foo", Map.of(), areNonExistentUrlsFiltered() ? UNAUTHORIZED : NOT_FOUND);
    }

    @Test
    public void givenBasicWebXmlApplication_whenMakingRequestsWithAuthHeader_thenRequestSucceeds() throws Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.BASIC);
        String jwt = Jwts.builder().subject("test").signWith(this.secretKey).compact();

        verifyRequestResponse(server, "/hello",
                              Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                              OK);
    }

    @Test
    public void givenExclusionsWebXmlApplication_whenMakingRequests_thenAuthenticationIsRequired() throws Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.EXCLUSIONS);

        verifyRequestResponse(server, "/private/hello", Map.of(), UNAUTHORIZED);
    }

    @Test
    public void givenExclusionsWebXmlApplication_whenMakingRequestsWithAuthHeader_thenRequestSucceeds() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.EXCLUSIONS);
        String jwt = Jwts.builder().subject("test").signWith(this.secretKey).compact();

        verifyRequestResponse(server, "/private/hello",
                              Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                              OK);
    }

    @Test
    public void givenExclusionsWebXmlApplication_whenMakingRequestsToExcludedUrls_thenRequestSucceeds() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.EXCLUSIONS);

        // When and Then
        HttpResponse<InputStream> response = verifyRequestResponse(server, "/public/hello", Map.of(), OK);
        Assert.assertEquals(StringUtils.join(IOUtils.readLines(response.body(), StandardCharsets.UTF_8), "\n"),
                            "Hello World!");
    }

    @Test
    public void givenMappingWebXmlApplication_whenMakingRequests_thenAuthenticationIsRequired() throws Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.MAPPING);

        verifyRequestResponse(server, "/private/hello", Map.of(), UNAUTHORIZED);
    }

    @Test
    public void givenMappingWebXmlApplication_whenMakingRequestsWithAuthHeader_thenRequestSucceeds() throws Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.MAPPING);
        String jwt = Jwts.builder().subject("test").signWith(this.secretKey).compact();

        verifyRequestResponse(server, "/private/hello",
                              Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                              OK);
    }

    @Test
    public void givenMappingWebXmlApplication_whenMakingRequestsToUnfilteredUrls_thenRequestSucceeds() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.MAPPING);

        // When and Then
        HttpResponse<InputStream> response = verifyRequestResponse(server, "/public/hello", Map.of(), OK);
        Assert.assertEquals(StringUtils.join(IOUtils.readLines(response.body(), StandardCharsets.UTF_8), "\n"),
                            "Hello World!");
    }

    @Test
    public void givenPublicKeyWebXmlApplication_whenMakingRequests_thenAuthenticationIsRequired() throws Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.PUBLIC_KEY);

        verifyRequestResponse(server, "/hello", Map.of(), UNAUTHORIZED);
    }

    @Test
    public void givenPublicKeyWebXmlApplication_whenMakingRequestsWithAuthHeader_thenRequestSucceeds() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.PUBLIC_KEY);
        String jwt = Jwts.builder().subject("test").signWith(this.privateKey).compact();

        verifyRequestResponse(server, "/hello",
                              Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                              OK);
    }

    @Test
    public void givenJwksWebXmlApplication_whenMakingRequests_thenAuthenticationIsRequired() throws Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.JWKS);

        verifyRequestResponse(server, "/hello", Map.of(), UNAUTHORIZED);
    }

    @Test
    public void givenJwksWebXmlApplication_whenMakingRequestsWithAuthHeaderSignedWithPublicKey_thenRequestSucceeds() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.JWKS);
        String jwt = Jwts.builder().header().keyId("public").and().subject("test").signWith(this.privateKey).compact();

        verifyRequestResponse(server, "/hello",
                              Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                              OK);
    }

    @Test
    public void givenJwksWebXmlApplication_whenMakingRequestsWithAuthHeaderSignedWithSecretKey_thenRequestSucceeds() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.JWKS);
        String jwt = Jwts.builder().header().keyId("secret").and().subject("test").signWith(this.secretKey).compact();

        verifyRequestResponse(server, "/hello",
                              Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                              OK);
    }

    @Test
    public void givenJwksWebXmlApplication_whenMakingRequestsWithAuthHeaderSignedWithUnknownKey_thenAuthenticationIsRequired() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.JWKS);
        String jwt = Jwts.builder()
                         .header()
                         .keyId("another")
                         .and()
                         .subject("test")
                         .signWith(Jwts.SIG.PS256.keyPair().build().getPrivate())
                         .compact();

        verifyRequestResponse(server, "/hello",
                              Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                              UNAUTHORIZED);
    }

    @Test
    public void givenCustomClaimsWebXmlApplication_whenMakingRequests_thenAuthenticationIsRequired() throws Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.CUSTOM_CLAIMS);

        verifyRequestResponse(server, "/hello", Map.of(), UNAUTHORIZED);
    }

    @Test
    public void givenCustomClaimsWebXmlApplication_whenMakingRequestsToNonExistentUrl_thenAuthenticationIsRequired() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.CUSTOM_CLAIMS);

        verifyRequestResponse(server, "/foo", Map.of(), areNonExistentUrlsFiltered() ? UNAUTHORIZED : NOT_FOUND);
    }

    @Test
    public void givenCustomClaimsWebXmlApplication_whenMakingRequestsWithDefaultAuthHeader_thenAuthenticationIsRequired() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.CUSTOM_CLAIMS);
        String jwt = Jwts.builder().subject("test").signWith(this.secretKey).compact();

        verifyRequestResponse(server, "/hello",
                              Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                              UNAUTHORIZED);
    }

    @Test
    public void givenCustomClaimsWebXmlApplication_whenMakingRequestsWithCustomAuthHeader_thenRequestSucceeds() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.CUSTOM_CLAIMS);
        String jwt = Jwts.builder().subject("test").signWith(this.secretKey).compact();

        verifyRequestResponse(server, "/hello", Map.of("X-API-Key", jwt), OK);
    }

    @Test
    public void givenMultipleFiltersWebXmlApplication_whenMakingRequestsWithSecretKey_thenRequestSucceeds() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.MULTIPLE_FILTERS);
        String jwt = Jwts.builder().subject("test").signWith(this.secretKey).compact();

        verifyRequestResponse(server, "/secret-key/hello",
                              Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                              OK);
    }

    @Test
    public void givenMultipleFiltersWebXmlApplication_whenMakingRequestsWithPublicKey_thenRequestSucceeds() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.MULTIPLE_FILTERS);
        String jwt = Jwts.builder().subject("test").signWith(this.privateKey).compact();

        verifyRequestResponse(server, "/public-key/hello",
                              Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                              OK);
    }

    @Test
    public void givenMultipleFiltersWebXmlApplication_whenMakingRequestsWithSecretKeyToPublicKeyResource_thenAuthenticationIsRequired() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.MULTIPLE_FILTERS);
        String jwt = Jwts.builder().subject("test").signWith(this.privateKey).compact();

        // When
        HttpResponse<InputStream> response = verifyRequestResponse(server, "/secret-key/hello",
                                                                   Map.of(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                                          JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                                                                   BAD_REQUEST);

        // Then
        String authHeader = response.headers().firstValue(JwtHttpConstants.HEADER_WWW_AUTHENTICATE).orElse(null);
        Assert.assertNotNull(authHeader);
        Assert.assertTrue(StringUtils.contains(authHeader, OAuth2Constants.ERROR_INVALID_REQUEST));
    }

    @Test
    public void givenMultipleFiltersWebXmlApplication_whenMakingRequestsWithPublicKeyToSecretKeyResource_thenBadRequest() throws
            Exception {
        // Given
        int port = TEST_PORT.getAndIncrement();
        AbstractServer server = buildWebXmlApplication(port, ApplicationConstants.MULTIPLE_FILTERS);
        String jwt = Jwts.builder().subject("test").signWith(this.secretKey).compact();

        // When
        HttpResponse<InputStream> response = verifyRequestResponse(server, "/public-key/hello",
                                                                   Map.of(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                                          JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt),
                                                                   BAD_REQUEST);

        // Then
        String authHeader = response.headers().firstValue(JwtHttpConstants.HEADER_WWW_AUTHENTICATE).orElse(null);
        Assert.assertNotNull(authHeader);
        Assert.assertTrue(StringUtils.contains(authHeader, OAuth2Constants.ERROR_INVALID_REQUEST));
    }
}
