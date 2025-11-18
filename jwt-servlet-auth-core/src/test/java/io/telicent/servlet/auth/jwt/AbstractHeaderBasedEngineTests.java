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
package io.telicent.servlet.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import io.telicent.servlet.auth.jwt.configuration.ClaimPath;
import io.telicent.servlet.auth.jwt.errors.KeyLoadException;
import io.telicent.servlet.auth.jwt.sources.HeaderSource;
import io.telicent.servlet.auth.jwt.verification.*;
import org.apache.commons.lang3.Strings;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;

public abstract class AbstractHeaderBasedEngineTests<TRequest, TResponse> extends AbstractTests<TRequest, TResponse> {

    private static final List<String> USER_AND_ADMIN = List.of("USER", "ADMIN");

    @Test
    public void engine_no_headers_present() throws IOException {
        TRequest request = createMockRequest(Collections.emptyMap());
        TResponse response = createMockResponse();
        JwtVerifier verifier = mock(JwtVerifier.class);
        JwtAuthenticationEngine<TRequest, TResponse> engine = createEngine();

        engine.authenticate(request, response, verifier);

        verifyStatusCode(request, response, 401);
        String challenge = verifyHeaderPresent(request, response, JwtHttpConstants.HEADER_WWW_AUTHENTICATE);
        Assert.assertFalse(Strings.CS.contains(challenge, "error="), "No error code expected");
        Assert.assertTrue(Strings.CI.contains(challenge, "No authentication"));
    }

    @Test
    public void engine_token_in_wrong_header() throws IOException {
        TRequest request = createMockRequest(Map.of(CUSTOM_AUTH_HEADER, "Bearer test"));
        TResponse response = createMockResponse();
        JwtVerifier verifier = mock(JwtVerifier.class);
        JwtAuthenticationEngine<TRequest, TResponse> engine = createEngine();

        engine.authenticate(request, response, verifier);

        verifyStatusCode(request, response, 401);
        String challenge = verifyHeaderPresent(request, response, JwtHttpConstants.HEADER_WWW_AUTHENTICATE);
        Assert.assertFalse(Strings.CS.contains(challenge, "error="), "No error code expected");
    }

    @Test
    public void engine_wrong_auth_scheme_01() throws IOException {
        verifyChallenge("Basic foo", mock(JwtVerifier.class), 400, "error=\"invalid_request\"");
    }

    @Test
    public void engine_wrong_auth_scheme_02() throws IOException {
        verifyChallenge("Digest foo", mock(JwtVerifier.class), 400, "error=\"invalid_request\"");
    }

    @Test
    public void engine_wrong_auth_scheme_03() throws IOException {
        verifyChallenge("Custom foo", mock(JwtVerifier.class), 400, "error=\"invalid_request\"");
    }

    @Test
    public void engine_blank_token_01() throws IOException {
        verifyChallenge("Bearer", mock(JwtVerifier.class), 400, "error=\"invalid_request\"");
    }

    @Test
    public void engine_blank_token_02() throws IOException {
        verifyChallenge("Bearer        ", mock(JwtVerifier.class), 400, "error=\"invalid_request\"");
    }

    @Test
    public void engine_invalid_token_01() throws IOException {
        verifyChallenge("Bearer test", new InvalidTokenVerifier(), 401, "error=\"invalid_token\"");
    }

    @Test
    public void engine_invalid_token_02() throws IOException {
        verifyChallenge("Bearer test", new FakeTokenVerifier(-1, ChronoUnit.MINUTES), 401, "error=\"invalid_token\"",
                        "Token expired");
    }

    @Test
    public void engine_invalid_token_03() throws IOException {
        verifyChallenge("Bearer test", new InvalidTokenVerifier(token -> new WeakKeyException("key no good")), 401,
                        "error=\"invalid_token\"", "weak key", "key no good");
    }

    @Test
    public void engine_invalid_token_04() throws IOException {
        verifyChallenge("Bearer test",
                        new InvalidTokenVerifier(token -> new SignatureException("JWT tampering detected")), 401,
                        "error=\"invalid_token\"", "failed signature verification", "JWT tampering detected");
    }

    @Test
    public void engine_invalid_token_05() throws IOException {
        verifyChallenge("Bearer test", new InvalidTokenVerifier(token -> new MalformedJwtException("not base64")), 401,
                        "error=\"invalid_token\"", "malformed", "not base64");
    }

    @Test
    public void engine_invalid_token_06() throws IOException {
        verifyChallenge("Bearer test",
                        new InvalidTokenVerifier(token -> new UnsupportedJwtException("algorithm foo not supported")),
                        400, "error=\"invalid_request\"", "unsupported", "algorithm foo");
    }

    @Test
    public void engine_invalid_token_07() throws IOException {
        verifyChallenge("Bearer test", new InvalidTokenVerifier(
                                token -> new PrematureJwtException(mock(Header.class), mock(Claims.class), "not yet valid")), 401,
                        "error=\"invalid_token\"", "out of sync", "not yet valid");
    }

    @Test
    public void engine_invalid_token_08() throws IOException {
        verifyChallenge("Bearer test", new InvalidTokenVerifier(
                                token -> new IncorrectClaimException(mock(Header.class), mock(Claims.class), "ClaimName",
                                                                     mock(Object.class), "Incorrect issuer")), 401,
                        "error=\"invalid_token\"", "Incorrect issuer");
    }

    @Test
    public void engine_invalid_token_09() throws IOException {
        verifyChallenge("Bearer test", new InvalidTokenVerifier(
                                token -> new MissingClaimException(mock(Header.class), mock(Claims.class), "ClaimName",
                                                                   mock(Object.class), "No issuer")), 401, "error=\"invalid_token\"",
                        "No issuer");
    }

    @Test
    public void engine_invalid_token_10() throws IOException {
        verifyChallenge("Bearer test", new SubjectlessTokenVerifier(), 401, "error=\"invalid_token\"",
                        "Failed to find a username");
    }

    @Test
    public void engine_invalid_token_11() throws IOException {
        verifyChallenge("Bearer test",
                        this.createEngine(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER,
                                          null, ClaimPath.topLevel(CUSTOM_CLAIM)), new SubjectlessTokenVerifier(), 401,
                        "error=\"invalid_token\"", "Failed to find a username");
    }

    @Test
    public void engine_invalid_token_12() throws IOException {
        verifyChallenge("Bearer test",
                        this.createEngine(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER,
                                          null, ClaimPath.topLevel(CUSTOM_CLAIM)),
                        new WrongTypeTokenVerifier(CUSTOM_CLAIM), 401,
                        "error=\"invalid_token\"", "Failed to find a username");
    }

    @Test
    public void engine_invalid_token_13() throws IOException {
        if (this.throwsOnUnexpectedErrors()) {
            throw new SkipException("Not relevant for this engine");
        }
        verifyChallenge("Bearer test", new InvalidTokenVerifier(token -> new RuntimeException("Unexpected error")),
                        500);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void engine_invalid_token_14() throws IOException {
        if (!this.throwsOnUnexpectedErrors()) {
            throw new SkipException("Not relevant for this engine");
        }
        verifyChallenge("Bearer test", new InvalidTokenVerifier(token -> new RuntimeException("Unexpected error")),
                        500);
    }

    @Test
    public void engine_custom_realm_01() throws IOException {
        JwtAuthenticationEngine<TRequest, TResponse> engine =
                createEngine(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER, "test-realm",
                             null);
        verifyChallenge("Bearer test", engine, new InvalidTokenVerifier(), 401, "realm=\"test-realm\"");
    }

    @Test
    public void engine_custom_realm_02() throws IOException {
        JwtAuthenticationEngine<TRequest, TResponse> engine =
                createEngine(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER,
                             "Secret Squirrels Only", null);
        verifyChallenge("Bearer test", engine, new InvalidTokenVerifier(), 401, "realm=\"Secret Squirrels Only\"");
    }

    @Test
    public void engine_authenticated_01() throws IOException {
        Assert.assertNotNull(verifyAuthenticated("Bearer test", new FakeTokenVerifier(), "test"));
    }

    @Test
    public void engine_authenticated_02() throws IOException {
        Assert.assertNotNull(verifyAuthenticated("Bearer eve", new FakeTokenVerifier(), "eve"));
    }

    @Test
    public void engine_authenticated_03() throws IOException {
        // HTTP Headers should be treated as case-insensitive
        Assert.assertNotNull(
                verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION.toLowerCase(Locale.ROOT), "Bearer lower",
                                    this.createEngine(), new FakeTokenVerifier(), "lower"));
    }

    @Test
    public void engine_authenticated_04() throws IOException {
        // HTTP Headers should be treated as case-insensitive
        Assert.assertNotNull(
                verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION.toUpperCase(Locale.ROOT), "Bearer upper",
                                    this.createEngine(), new FakeTokenVerifier(), "upper"));
    }

    @Test
    public void engine_authenticated_custom_header_01() throws IOException {
        Assert.assertNotNull(verifyAuthenticated(CUSTOM_AUTH_HEADER, "Bearer adam",
                                                 this.createEngine(CUSTOM_AUTH_HEADER,
                                                                   JwtHttpConstants.AUTH_SCHEME_BEARER, null, null),
                                                 new FakeTokenVerifier(), "adam"));
    }

    @Test
    public void engine_authenticated_custom_header_02() throws IOException {
        Assert.assertNotNull(
                verifyAuthenticated(CUSTOM_AUTH_HEADER, "eve", this.createEngine(CUSTOM_AUTH_HEADER, null, null, null),
                                    new FakeTokenVerifier(), "eve"));
    }

    @Test
    public void engine_authenticated_custom_claim_01() {
        Assert.assertNotNull(verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer charles",
                                                 this.createEngine(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                                   JwtHttpConstants.AUTH_SCHEME_BEARER, null,
                                                                   ClaimPath.topLevel(CUSTOM_CLAIM)),
                                                 new AlternateClaimTokenVerifier(CUSTOM_CLAIM), "charles"));
    }

    @Test
    public void givenCustomLightlyNestedUsernameClaim_whenAuthenticating_thenCorrectUser() {
        // Given
        SecretKey key = Jwts.SIG.HS256.key().build();
        String jwt = Jwts.builder()
                         .subject(UUID.randomUUID().toString())
                         .claims(Map.of("details", Map.of("name", "doug")))
                         .signWith(key)
                         .compact();

        // When and Then
        Assert.assertNotNull(verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer " + jwt,
                                                 this.createEngine(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                                   JwtHttpConstants.AUTH_SCHEME_BEARER, null,
                                                                   ClaimPath.of("details", "name")),
                                                 new SignedJwtVerifier(key), "doug"));
    }

    @Test
    public void givenCustomDeeplyNestedUsernameClaim_whenAuthenticating_thenCorrectUser() {
        // Given
        SecretKey key = Jwts.SIG.HS256.key().build();
        String jwt = Jwts.builder()
                         .subject(UUID.randomUUID().toString())
                         .claims(Map.of("a", Map.of("really", Map.of("really", Map.of("deeply", Map.of("nested",
                                                                                                       Map.of("claim",
                                                                                                              "doug")))))))
                         .signWith(key)
                         .compact();

        // When and Then
        Assert.assertNotNull(verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer " + jwt,
                                                 this.createEngine(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                                   JwtHttpConstants.AUTH_SCHEME_BEARER, null,
                                                                   ClaimPath.of("a", "really", "really", "deeply",
                                                                                "nested", "claim")),
                                                 new SignedJwtVerifier(key), "doug"));
    }

    @Test
    public void engine_authenticated_multiple_token_sources_01() {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiHeaderSourceEngine();
        Assert.assertNotNull(verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer test", engine,
                                                 new FakeTokenVerifier(), "test"));
    }

    @Test
    public void engine_authenticated_multiple_token_sources_02() {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiHeaderSourceEngine();
        Assert.assertNotNull(
                verifyAuthenticated(CUSTOM_AUTH_HEADER, "Bearer test", engine, new FakeTokenVerifier(), "test"));
    }

    @Test
    public void engine_authenticated_multiple_token_sources_03() {
        JwtAuthenticationEngine<TRequest, TResponse> engine =
                createMultiHeaderSourceEngine(null, ClaimPath.topLevel(CUSTOM_CLAIM));
        Assert.assertNotNull(verifyAuthenticated(CUSTOM_AUTH_HEADER, "Bearer test", engine,
                                                 new AlternateClaimTokenVerifier(CUSTOM_CLAIM), "test"));
    }

    @Test
    public void engine_authenticated_multiple_token_sources_04() throws IOException {
        JwtAuthenticationEngine<TRequest, TResponse> engine =
                createMultiHeaderSourceEngine(null, ClaimPath.topLevel(CUSTOM_CLAIM));
        Assert.assertNotNull(verifyAuthenticated(CUSTOM_AUTH_HEADER, "Bearer test", engine,
                                                 new AlternateClaimTokenVerifier(CUSTOM_CLAIM), "test"));
    }

    @Test
    public void engine_authenticated_multiple_token_sources_05() {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiHeaderSourceEngine();
        // Order of headers in request is irrelevant as engine tries the headers in its configured order of preference
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(CUSTOM_AUTH_HEADER, "Bearer bar");
        headers.put(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer foo");
        Assert.assertNotNull(verifyAuthenticated(headers, engine, new FakeTokenVerifier(), "foo"));
    }

    @Test
    public void engine_authenticated_multiple_token_sources_06() {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createEngine(
                List.of(new HeaderSource(CUSTOM_AUTH_HEADER, JwtHttpConstants.AUTH_SCHEME_BEARER),
                        new HeaderSource(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER)),
                null, null);
        // Order of headers in request is irrelevant as engine tries the headers in its configured order of preference
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(CUSTOM_AUTH_HEADER, "Bearer bar");
        headers.put(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer foo");
        Assert.assertNotNull(verifyAuthenticated(headers, engine, new FakeTokenVerifier(), "bar"));
    }

    @Test
    public void engine_authenticated_multiple_token_sources_07() {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiHeaderSourceEngine();
        // Can have multiple instances of the headers where some have invalid values and still authenticate successfully
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(CUSTOM_AUTH_HEADER, "Bearer");
        headers.put(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer foo");
        Assert.assertNotNull(verifyAuthenticated(headers, engine, new FakeTokenVerifier(), "foo"));
    }


    @Test
    public void engine_invalid_multiple_token_sources_01() throws IOException {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiHeaderSourceEngine();
        // Token not in any of the configured headers
        TRequest request = createMockRequest(Map.of("Other", "Bearer test"));
        TResponse response = createMockResponse();

        engine.authenticate(request, response, mock(JwtVerifier.class));

        verifyChallenge(request, response, 401);
    }

    @Test
    public void engine_invalid_multiple_token_sources_02() throws IOException {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiHeaderSourceEngine();
        // No headers at all
        TRequest request = createMockRequest(Collections.emptyMap());
        TResponse response = createMockResponse();

        engine.authenticate(request, response, mock(JwtVerifier.class));

        verifyChallenge(request, response, 401);
    }

    @Test
    public void engine_invalid_multiple_token_sources_03() throws IOException {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiHeaderSourceEngine();
        // Headers present but invalid token
        TRequest request = createMockRequest(
                Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer test", CUSTOM_AUTH_HEADER, "Bearer test"));
        TResponse response = createMockResponse();

        engine.authenticate(request, response, new InvalidTokenVerifier());

        verifyChallenge(request, response, 401, OAuth2Constants.ERROR_INVALID_TOKEN, "Not a valid token");
    }

    @Test
    public void engine_invalid_multiple_token_sources_04() throws IOException {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiHeaderSourceEngine();
        // Headers present but token has no subject
        TRequest request = createMockRequest(
                Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer test", CUSTOM_AUTH_HEADER, "Bearer test"));
        TResponse response = createMockResponse();

        engine.authenticate(request, response, new SubjectlessTokenVerifier());

        verifyChallenge(request, response, 401, OAuth2Constants.ERROR_INVALID_TOKEN, "Failed to find a username");
    }

    private JwtAuthenticationEngine<TRequest, TResponse> createMultiHeaderSourceEngine() {
        return createMultiHeaderSourceEngine(null, null);
    }

    private JwtAuthenticationEngine<TRequest, TResponse> createMultiHeaderSourceEngine(String realm,
                                                                                       ClaimPath usernameClaim) {
        List<HeaderSource> headerSources = new ArrayList<>(JwtHttpConstants.DEFAULT_HEADER_SOURCES);
        headerSources.add(new HeaderSource(CUSTOM_AUTH_HEADER, JwtHttpConstants.AUTH_SCHEME_BEARER));

        JwtAuthenticationEngine<TRequest, TResponse> engine =
                createEngine(headerSources, realm, usernameClaim != null ? List.of(usernameClaim) : null);
        return engine;
    }

    protected void verifyChallenge(String authHeader, JwtVerifier verifier, int expectedStatus,
                                   String... expectedChallengeContents) throws IOException {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createEngine();
        verifyChallenge(authHeader, engine, verifier, expectedStatus, expectedChallengeContents);
    }

    protected void verifyChallenge(String authHeader, JwtAuthenticationEngine<TRequest, TResponse> engine,
                                   JwtVerifier verifier, int expectedStatus, String... expectedChallengeContents) throws
            IOException {
        TRequest request = createMockRequest(Map.of(JwtHttpConstants.HEADER_AUTHORIZATION, authHeader));
        TResponse response = createMockResponse();

        engine.authenticate(request, response, verifier);

        verifyChallenge(request, response, expectedStatus, expectedChallengeContents);
    }

    @Test
    public void engine_authenticated_multiple_username_claims_01() {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiClaimEngine(CUSTOM_CLAIM);
        Assert.assertNotNull(verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer test", engine,
                                                 new AlternateClaimTokenVerifier(CUSTOM_CLAIM), "test"));
    }

    @Test
    public void engine_authenticated_multiple_username_claims_02() {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiClaimEngine("upper", CUSTOM_CLAIM);
        Assert.assertNotNull(verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer test", engine,
                                                 new MultipleClaimsTokenVerifier(CUSTOM_CLAIM), "TEST"));
    }

    @Test
    public void engine_authenticated_multiple_username_claims_03() {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiClaimEngine("start", CUSTOM_CLAIM);
        Assert.assertNotNull(verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer test", engine,
                                                 new MultipleClaimsTokenVerifier(CUSTOM_CLAIM), "t"));
    }

    @Test
    public void engine_authenticated_multiple_username_claims_04() {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiClaimEngine("end", CUSTOM_CLAIM);
        Assert.assertNotNull(verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer testing", engine,
                                                 new MultipleClaimsTokenVerifier(CUSTOM_CLAIM), "g"));
    }

    @Test
    public void engine_authenticated_multiple_username_claims_05() {
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiClaimEngine("lower", CUSTOM_CLAIM);
        Assert.assertNotNull(verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer UPPERCASE", engine,
                                                 new MultipleClaimsTokenVerifier(CUSTOM_CLAIM), "uppercase"));
    }

    @Test
    public void engine_authenticated_multiple_username_claims_06() {
        JwtAuthenticationEngine<TRequest, TResponse> engine =
                createMultiClaimEngine("upper", "lower", "start", "end", CUSTOM_CLAIM);
        Assert.assertNotNull(verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer test", engine,
                                                 new FakeTokenVerifier(), "test"));
    }

    @Test
    public void engine_authenticated_multiple_username_claims_07() {
        // Test for #17
        // Even if we are preferring a claim whose value will be blank/empty we should ignore that value and return the
        // first non-empty username claim
        JwtAuthenticationEngine<TRequest, TResponse> engine =
                createMultiClaimEngine("empty", "blank", "username", CUSTOM_CLAIM);
        Assert.assertNotNull(verifyAuthenticated(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer test", engine,
                                                 new MultipleClaimsTokenVerifier("username"), "test"));
    }

    public JwtAuthenticationEngine<TRequest, TResponse> createMultiClaimEngine(String... usernameClaims) {
        return createEngine((List<HeaderSource>) JwtHttpConstants.DEFAULT_HEADER_SOURCES, null,
                            Arrays.stream(usernameClaims).map(ClaimPath::topLevel).collect(Collectors.toList()));
    }

    @Test
    public void givenDefaultHeaderSources_whenCreatingAnEngine_thenToStringReflectsSources() {
        // Given
        List<HeaderSource> sources = new ArrayList<>(JwtHttpConstants.DEFAULT_HEADER_SOURCES);

        // When
        JwtAuthenticationEngine<TRequest, TResponse> engine = createEngine(sources, null, null);

        // Then
        String debugString = engine.toString();
        for (HeaderSource source : sources) {
            Assert.assertTrue(Strings.CS.contains(debugString, source.toString()));
        }
    }

    @Test
    public void givenCustomHeaderSources_whenCreatingAnEngine_thenToStringReflectsSources() {
        // Given
        List<HeaderSource> sources =
                List.of(new HeaderSource(CUSTOM_AUTH_HEADER, null), new HeaderSource(CUSTOM_AUTH_HEADER, "Prefix"));

        // When
        JwtAuthenticationEngine<TRequest, TResponse> engine = createEngine(sources, null, null);

        // Then
        String debugString = engine.toString();
        for (HeaderSource source : sources) {
            Assert.assertTrue(Strings.CS.contains(debugString, source.toString()));
        }
    }

    @Test
    public void givenCustomUsernameClaims_whenCreatingAnEngine_thenToStringReflectsSources() {
        // Given
        List<ClaimPath> claims = List.of(ClaimPath.topLevel(CUSTOM_CLAIM), ClaimPath.topLevel("test"));

        // When
        JwtAuthenticationEngine<TRequest, TResponse> engine =
                createEngine(new ArrayList<>(JwtHttpConstants.DEFAULT_HEADER_SOURCES), null, claims);

        // Then
        String debugString = engine.toString();
        for (ClaimPath claim : claims) {
            Assert.assertTrue(Strings.CS.contains(debugString, claim.toConfigurationString()));
        }
    }

    @Test
    public void givenMultipleHeaders_whenAuthenticating_thenRequestAttributesIncludeCorrectRawJwt() {
        // Given
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiHeaderSourceEngine();
        // Order of headers in request is irrelevant as engine tries the headers in its configured order of preference
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(CUSTOM_AUTH_HEADER, "Bearer bar");
        headers.put(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer foo");

        // When and Then
        TRequest request = verifyAuthenticated(headers, engine, new FakeTokenVerifier(), "foo");
        Object source = verifyRequestAttribute(request, JwtServletConstants.REQUEST_ATTRIBUTE_SOURCE);
        Assert.assertTrue(source instanceof HeaderSource);
        verifyRequestAttribute(request, JwtServletConstants.REQUEST_ATTRIBUTE_RAW_JWT, "foo");
        Object jws = verifyRequestAttribute(request, JwtServletConstants.REQUEST_ATTRIBUTE_VERIFIED_JWT);
        Assert.assertTrue(jws instanceof Jws<?>);
    }

    @Test
    public void givenMultipleHeadersOfWhichSomeAreInvalid_whenAuthenticating_thenRequestAttributesIncludeCorrectRawJwt() {
        // Given
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiHeaderSourceEngine();
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(CUSTOM_AUTH_HEADER, "bar");
        headers.put(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer foo");

        // When and Then
        TRequest request = verifyAuthenticated(headers, engine, new FakeTokenVerifier(), "foo");
        Object source = verifyRequestAttribute(request, JwtServletConstants.REQUEST_ATTRIBUTE_SOURCE);
        Assert.assertTrue(source instanceof HeaderSource);
        verifyRequestAttribute(request, JwtServletConstants.REQUEST_ATTRIBUTE_RAW_JWT, "foo");
        Object jws = verifyRequestAttribute(request, JwtServletConstants.REQUEST_ATTRIBUTE_VERIFIED_JWT);
        Assert.assertTrue(jws instanceof Jws<?>);
    }

    @Test
    public void givenMultipleHeadersOfWhichSomeAreNull_whenAuthenticating_thenRequestAttributesIncludeCorrectRawJwt() {
        // Given
        JwtAuthenticationEngine<TRequest, TResponse> engine = createMultiHeaderSourceEngine();
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(CUSTOM_AUTH_HEADER, "Bearer bar");
        headers.put(JwtHttpConstants.HEADER_AUTHORIZATION, null);

        // When and Then
        TRequest request = verifyAuthenticated(headers, engine, new FakeTokenVerifier(), "bar");
        Object source = verifyRequestAttribute(request, JwtServletConstants.REQUEST_ATTRIBUTE_SOURCE);
        Assert.assertTrue(source instanceof HeaderSource);
        verifyRequestAttribute(request, JwtServletConstants.REQUEST_ATTRIBUTE_RAW_JWT, "bar");
        Object jws = verifyRequestAttribute(request, JwtServletConstants.REQUEST_ATTRIBUTE_VERIFIED_JWT);
        Assert.assertTrue(jws instanceof Jws<?>);
    }

    private static String createJwtWithRoles(SecretKey key, String[] rolesClaim, Object roles) {
        Map<String, Object> claims = new LinkedHashMap<>();
        if (rolesClaim.length == 1) {
            claims.put(rolesClaim[0], roles);
        } else {
            Map<String, Object> parent = claims;
            for (int i = 0; i < rolesClaim.length - 1; i++) {
                Map<String, Object> child = new LinkedHashMap<>();
                parent.put(rolesClaim[i], child);
                parent = child;
            }
            parent.put(rolesClaim[rolesClaim.length - 1], roles);
        }

        return Jwts.builder().subject("test").claims().add(claims).and().signWith(key).compact();
    }

    @DataProvider(name = "rolesTestData")
    public static Object[][] rolesTestData() {
        return new Object[][] {
                { new String[] { "roles" }, USER_AND_ADMIN, USER_AND_ADMIN },
                { new String[] { "lightly", "nested" }, "USER,ADMIN", USER_AND_ADMIN },
                {
                        new String[] { "really", "really", "deeply", "nested" },
                        new String[] { "USER", "ADMIN" },
                        USER_AND_ADMIN
                },
                { new String[] { "roles" }, List.of("AUDITOR"), List.of("AUDITOR") },
                { new String[] { "roles" }, "USER,, ADMIN  ", USER_AND_ADMIN },
                { new String[] { "roles" }, "USER, USER , ADMIN  ", USER_AND_ADMIN },
                };
    }

    @Test(dataProvider = "rolesTestData", dataProviderClass = AbstractHeaderBasedEngineTests.class)
    public void givenEngineWithRolesClaim_whenAuthenticating_thenRolesAreAvailable(String[] rolesClaim, Object roles,
                                                                                   List<String> expectedRoles) throws
            IOException, KeyLoadException {
        // Given
        JwtAuthenticationEngine<TRequest, TResponse> engine =
                createEngine(new ArrayList<>(JwtHttpConstants.DEFAULT_HEADER_SOURCES), null, null,
                             ClaimPath.of(rolesClaim));
        File keyFile = AbstractConfigurableFilterTests.createSecretKey(Jwts.SIG.HS256);
        SecretKey key = KeyUtils.loadSecretKey(keyFile);
        SignedJwtVerifier verifier = new SignedJwtVerifier(key);
        String jwt = createJwtWithRoles(key, rolesClaim, roles);
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer " + jwt);

        // When
        TRequest request = verifyAuthenticated(headers, engine, verifier, "test");

        // Then
        for (String role : expectedRoles) {
            verifyHasRole(request, role);
        }
        verifyMissingRole(request, "NO-SUCH-ROLE");
    }

    @Test(dataProvider = "rolesTestData", dataProviderClass = AbstractHeaderBasedEngineTests.class)
    public void givenJwtWithRolesAndEngineWithoutRolesClaim_whenAuthenticating_thenRolesAreUnavailable(
            String[] rolesClaim, Object roles,
            List<String> expectedRoles) throws
            IOException, KeyLoadException {
        // Given
        JwtAuthenticationEngine<TRequest, TResponse> engine =
                createEngine(new ArrayList<>(JwtHttpConstants.DEFAULT_HEADER_SOURCES), null, null, null);
        File keyFile = AbstractConfigurableFilterTests.createSecretKey(Jwts.SIG.HS256);
        SecretKey key = KeyUtils.loadSecretKey(keyFile);
        SignedJwtVerifier verifier = new SignedJwtVerifier(key);
        String jwt = createJwtWithRoles(key, rolesClaim, roles);
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(JwtHttpConstants.HEADER_AUTHORIZATION, "Bearer " + jwt);

        // When
        TRequest request = verifyAuthenticated(headers, engine, verifier, "test");

        // Then
        for (String role : expectedRoles) {
            // As role extraction wasn't configured on the engine the roles won't be extracted even though the JWT
            // contained them
            verifyMissingRole(request, role);
        }
        verifyMissingRole(request, "NO-SUCH-ROLE");
    }

}
