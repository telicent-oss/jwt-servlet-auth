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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.PublicJwk;
import io.jsonwebtoken.security.SignatureException;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.Reader;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCachingJwtVerifier {

    private final SecretKey key = Jwts.SIG.HS256.key().build();
    private final SecretKey anotherKey = Jwts.SIG.HS256.key().build();

    /**
     * A clock that only moves when told to
     */
    private static final class MutableClock extends Clock {
        private Instant now = Instant.now();

        void advance(Duration duration) {
            this.now = this.now.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return this.now;
        }
    }

    /**
     * Counts how often tokens actually get verified
     */
    private static final class CountingVerifier implements JwtVerifier {
        private final JwtVerifier delegate;
        private final AtomicInteger calls = new AtomicInteger();

        CountingVerifier(JwtVerifier delegate) {
            this.delegate = delegate;
        }

        @Override
        public Jws<Claims> verify(String rawJwt) {
            this.calls.incrementAndGet();
            return this.delegate.verify(rawJwt);
        }

        @Override
        public String toString() {
            return "CountingVerifier";
        }
    }

    private String token(SecretKey signingKey, String subject, Instant expiry) {
        var builder = Jwts.builder().subject(subject).signWith(signingKey);
        if (expiry != null) {
            builder.expiration(Date.from(expiry));
        }
        return builder.compact();
    }

    private CountingVerifier counting() {
        return new CountingVerifier(new SignedJwtVerifier(this.key));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void givenNullDelegate_whenCreating_thenNullPointerException() {
        new CachingJwtVerifier(null, 10, Duration.ofMinutes(1));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*size.*")
    public void givenZeroSize_whenCreating_thenIllegalArgument() {
        new CachingJwtVerifier(counting(), 0, Duration.ofMinutes(1));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*age.*")
    public void givenZeroMaxAge_whenCreating_thenIllegalArgument() {
        new CachingJwtVerifier(counting(), 10, Duration.ZERO);
    }

    @Test
    public void givenValidToken_whenVerifiedRepeatedly_thenVerifiedOnceAndEqualResultsReturned() {
        // Given
        CountingVerifier delegate = counting();
        CachingJwtVerifier verifier = new CachingJwtVerifier(delegate, 10, Duration.ofMinutes(1));
        String jwt = token(this.key, "alice", Instant.now().plusSeconds(3600));

        // When
        Jws<Claims> first = verifier.verify(jwt);
        Jws<Claims> second = verifier.verify(jwt);
        Jws<Claims> third = verifier.verify(jwt);

        // Then
        Assert.assertEquals(delegate.calls.get(), 1);
        Assert.assertEquals(first.getPayload().getSubject(), "alice");
        // Each caller gets its own copy, with the same content
        Assert.assertNotSame(second, first);
        Assert.assertNotSame(third, second);
        Assert.assertEquals(second.getPayload(), first.getPayload());
        Assert.assertEquals(third.getPayload(), first.getPayload());
        Assert.assertEquals(third.getHeader(), first.getHeader());
        Assert.assertEquals(third.getSignature(), first.getSignature());
    }

    @Test
    public void givenTokenWithBadSignature_whenVerifiedRepeatedly_thenRejectedEveryTimeAndNeverCached() {
        // Given
        CountingVerifier delegate = counting();
        CachingJwtVerifier verifier = new CachingJwtVerifier(delegate, 10, Duration.ofMinutes(1));
        String forged = token(this.anotherKey, "mallory", Instant.now().plusSeconds(3600));

        // When and Then
        for (int i = 0; i < 3; i++) {
            Assert.assertThrows(SignatureException.class, () -> verifier.verify(forged));
        }
        Assert.assertEquals(delegate.calls.get(), 3);
        Assert.assertEquals(verifier.cachedTokens(), 0);
    }

    @Test
    public void givenCachedToken_whenTamperedCopyPresented_thenTamperedCopyIsVerifiedAndRejected() {
        // Given
        CountingVerifier delegate = counting();
        CachingJwtVerifier verifier = new CachingJwtVerifier(delegate, 10, Duration.ofMinutes(1));
        String jwt = token(this.key, "alice", Instant.now().plusSeconds(3600));
        verifier.verify(jwt);
        String[] parts = jwt.split("\\.");
        String tampered = parts[0] + "." + token(this.key, "admin", Instant.now().plusSeconds(3600)).split("\\.")[1] + "."
                          + parts[2];

        // When and Then
        Assert.assertThrows(SignatureException.class, () -> verifier.verify(tampered));
        Assert.assertEquals(delegate.calls.get(), 2);
    }

    @Test
    public void givenTokenExpiringBeforeMaxAge_whenPastItsExpiry_thenVerifiedAgainAndRejected() {
        // Given: the cache and the real verifier share a clock
        MutableClock clock = new MutableClock();
        CountingVerifier delegate = new CountingVerifier(new SignedJwtVerifier(
                JwtParsers.builder().verifyWith(this.key).clock(() -> Date.from(clock.instant())).build()));
        CachingJwtVerifier verifier = new CachingJwtVerifier(delegate, 10, Duration.ofMinutes(10), clock);
        String jwt = token(this.key, "alice", clock.instant().plusSeconds(2));
        verifier.verify(jwt);
        verifier.verify(jwt);
        Assert.assertEquals(delegate.calls.get(), 1);

        // When
        clock.advance(Duration.ofSeconds(3));

        // Then
        Assert.assertThrows(ExpiredJwtException.class, () -> verifier.verify(jwt));
        Assert.assertEquals(delegate.calls.get(), 2);
    }

    @Test
    public void givenLongLivedToken_whenMaxAgeElapses_thenVerifiedAgain() {
        // Given
        MutableClock clock = new MutableClock();
        CountingVerifier delegate = counting();
        CachingJwtVerifier verifier = new CachingJwtVerifier(delegate, 10, Duration.ofSeconds(30), clock);
        String jwt = token(this.key, "alice", Instant.now().plusSeconds(3600));
        verifier.verify(jwt);

        // When
        clock.advance(Duration.ofSeconds(29));
        verifier.verify(jwt);
        Assert.assertEquals(delegate.calls.get(), 1);
        clock.advance(Duration.ofSeconds(2));
        verifier.verify(jwt);

        // Then
        Assert.assertEquals(delegate.calls.get(), 2);
    }

    @Test
    public void givenTokenWithoutExpiry_whenVerifiedRepeatedly_thenCachedForMaxAge() {
        // Given
        MutableClock clock = new MutableClock();
        CountingVerifier delegate = counting();
        CachingJwtVerifier verifier = new CachingJwtVerifier(delegate, 10, Duration.ofSeconds(30), clock);
        String jwt = token(this.key, "alice", null);

        // When
        verifier.verify(jwt);
        verifier.verify(jwt);
        clock.advance(Duration.ofSeconds(31));
        verifier.verify(jwt);

        // Then
        Assert.assertEquals(delegate.calls.get(), 2);
    }

    @Test
    public void givenTokenAcceptedOnlyThroughClockSkew_whenVerifiedRepeatedly_thenNeverCached() {
        // Given: a verifier allowing a minute of clock skew, and a token that expired 10 seconds ago
        CountingVerifier delegate = new CountingVerifier(
                new SignedJwtVerifier(JwtParsers.builder().verifyWith(this.key).clockSkewSeconds(60).build()));
        CachingJwtVerifier verifier = new CachingJwtVerifier(delegate, 10, Duration.ofMinutes(1));
        String jwt = token(this.key, "alice", Instant.now().minusSeconds(10));

        // When
        Assert.assertNotNull(verifier.verify(jwt));
        Assert.assertNotNull(verifier.verify(jwt));

        // Then: still accepted, as by the delegate, but always verified for real
        Assert.assertEquals(delegate.calls.get(), 2);
        Assert.assertEquals(verifier.cachedTokens(), 0);
    }

    @Test
    public void givenDifferentTokens_whenVerified_thenEachCachedSeparately() {
        // Given
        CountingVerifier delegate = counting();
        CachingJwtVerifier verifier = new CachingJwtVerifier(delegate, 10, Duration.ofMinutes(1));
        String alice = token(this.key, "alice", Instant.now().plusSeconds(3600));
        String bob = token(this.key, "bob", Instant.now().plusSeconds(3600));

        // When
        Assert.assertEquals(verifier.verify(alice).getPayload().getSubject(), "alice");
        Assert.assertEquals(verifier.verify(bob).getPayload().getSubject(), "bob");
        Assert.assertEquals(verifier.verify(alice).getPayload().getSubject(), "alice");
        Assert.assertEquals(verifier.verify(bob).getPayload().getSubject(), "bob");

        // Then
        Assert.assertEquals(delegate.calls.get(), 2);
    }

    @Test
    public void givenMoreTokensThanMaxSize_whenVerified_thenCacheStaysBounded() {
        // Given
        CachingJwtVerifier verifier = new CachingJwtVerifier(counting(), 5, Duration.ofMinutes(1));

        // When
        for (int i = 0; i < 50; i++) {
            verifier.verify(token(this.key, "user-" + i, Instant.now().plusSeconds(3600)));
        }

        // Then
        Assert.assertTrue(verifier.cachedTokens() <= 5, "cached " + verifier.cachedTokens());
    }

    @Test
    public void givenDelegateReturningNull_whenVerified_thenNullReturnedAndNotCached() {
        // Given
        AtomicInteger calls = new AtomicInteger();
        CachingJwtVerifier verifier = new CachingJwtVerifier(raw -> {
            calls.incrementAndGet();
            return null;
        }, 10, Duration.ofMinutes(1));

        // When
        Assert.assertNull(verifier.verify("anything"));
        Assert.assertNull(verifier.verify("anything"));

        // Then
        Assert.assertEquals(calls.get(), 2);
    }

    @Test
    public void givenNullToken_whenVerified_thenPassedToDelegate() {
        // Given
        CountingVerifier delegate = counting();
        CachingJwtVerifier verifier = new CachingJwtVerifier(delegate, 10, Duration.ofMinutes(1));

        // When and Then: the same exception as the delegate alone would give
        Assert.assertThrows(IllegalArgumentException.class, () -> verifier.verify(null));
        Assert.assertEquals(delegate.calls.get(), 1);
    }

    @Test
    public void givenCachingVerifier_whenDescribed_thenIncludesSettingsAndDelegate() {
        // Given
        CachingJwtVerifier verifier = new CachingJwtVerifier(counting(), 7, Duration.ofSeconds(45));

        // When
        String description = verifier.toString();

        // Then
        Assert.assertTrue(description.contains("maxSize=7"), description);
        Assert.assertTrue(description.contains("PT45S"), description);
        Assert.assertTrue(description.contains("CountingVerifier"), description);
        Assert.assertEquals(verifier.getDelegate().toString(), "CountingVerifier");
    }

    private String tokenWithNestedClaims() {
        return Jwts.builder()
                   .header().add("x-nested", Map.of("tags", List.of("signed"))).and()
                   .subject("alice")
                   .expiration(Date.from(Instant.now().plusSeconds(3600)))
                   .claim("roles", List.of("reader"))
                   .claim("details", Map.of("groups", List.of("analysts"), "level", Map.of("clearance", "O")))
                   .signWith(this.key)
                   .compact();
    }

    @SuppressWarnings("unchecked")
    private static List<Object> roles(Jws<Claims> jws) {
        return (List<Object>) jws.getPayload().get("roles");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> details(Jws<Claims> jws) {
        return (Map<String, Object>) jws.getPayload().get("details");
    }

    @SuppressWarnings("unchecked")
    private static void tryToMutate(Runnable mutation) {
        try {
            mutation.run();
        } catch (UnsupportedOperationException e) {
            // Also fine: the value isn't modifiable at all
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenCachedToken_whenCallersModifyNestedClaims_thenLaterResultsStillMatchTheSignedToken() {
        // Given
        CountingVerifier delegate = counting();
        CachingJwtVerifier verifier = new CachingJwtVerifier(delegate, 10, Duration.ofMinutes(1));
        String jwt = tokenWithNestedClaims();
        Claims signed = new SignedJwtVerifier(this.key).verify(jwt).getPayload();

        // When: the caller of the first (uncached) verification, and of a cached one, modify nested claims
        Jws<Claims> first = verifier.verify(jwt);
        tryToMutate(() -> roles(first).add("admin"));
        tryToMutate(() -> ((List<Object>) details(first).get("groups")).add("admins"));
        Jws<Claims> second = verifier.verify(jwt);
        tryToMutate(() -> roles(second).add("admin"));
        tryToMutate(() -> ((List<Object>) details(second).get("groups")).add("admins"));
        tryToMutate(() -> ((Map<String, Object>) details(second).get("level")).put("clearance", "TS"));
        Jws<Claims> third = verifier.verify(jwt);

        // Then: later results are unaffected and still match the signed claims
        Assert.assertEquals(delegate.calls.get(), 1);
        Assert.assertEquals(roles(third), List.of("reader"));
        Assert.assertEquals(details(third).get("groups"), List.of("analysts"));
        Assert.assertEquals(details(third).get("level"), Map.of("clearance", "O"));
        Assert.assertEquals(third.getPayload(), signed);
        Assert.assertNotSame(third, second);
        Assert.assertNotSame(third.getPayload(), second.getPayload());
    }

    @Test
    public void givenCachedToken_whenCallerModifiesTheMap_thenRejectedAsForAFreshVerification() {
        // Given
        CachingJwtVerifier verifier = new CachingJwtVerifier(counting(), 10, Duration.ofMinutes(1));
        String jwt = tokenWithNestedClaims();
        verifier.verify(jwt);

        // When
        Jws<Claims> cached = verifier.verify(jwt);

        // Then: like the claims of a fresh verification, the claims map itself can't be modified
        Assert.assertThrows(UnsupportedOperationException.class, () -> cached.getPayload().put("roles", List.of("x")));
        Assert.assertThrows(UnsupportedOperationException.class,
                            () -> new SignedJwtVerifier(this.key).verify(jwt).getPayload().put("roles", List.of("x")));
    }

    @Test
    public void givenCachedToken_whenCallerModifiesExpiryDate_thenLaterResultsUnaffected() {
        // Given
        CachingJwtVerifier verifier = new CachingJwtVerifier(counting(), 10, Duration.ofMinutes(1));
        String jwt = tokenWithNestedClaims();
        Date expiry = verifier.verify(jwt).getPayload().getExpiration();

        // When
        verifier.verify(jwt).getPayload().getExpiration().setTime(0);

        // Then
        Assert.assertEquals(verifier.verify(jwt).getPayload().getExpiration(), expiry);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenCachedToken_whenCallerModifiesHeaderOrDigest_thenLaterResultsUnaffected() {
        // Given
        CachingJwtVerifier verifier = new CachingJwtVerifier(counting(), 10, Duration.ofMinutes(1));
        String jwt = tokenWithNestedClaims();
        Jws<Claims> fresh = new SignedJwtVerifier(this.key).verify(jwt);
        verifier.verify(jwt);

        // When
        Jws<Claims> cached = verifier.verify(jwt);
        tryToMutate(() -> ((List<Object>) ((Map<String, Object>) cached.getHeader().get("x-nested")).get("tags")).add(
                "tampered"));
        tryToMutate(() -> cached.getHeader().put("kid", "other"));
        byte[] digest = cached.getDigest();
        Arrays.fill(digest, (byte) 0);
        Jws<Claims> later = verifier.verify(jwt);

        // Then
        Assert.assertEquals(later.getHeader(), fresh.getHeader());
        Assert.assertEquals(later.getDigest(), fresh.getDigest());
        Assert.assertEquals(later.getSignature(), fresh.getSignature());
    }

    @Test
    public void givenCachedToken_whenVisited_thenBehavesAsAVerifiedJws() {
        // Given
        CachingJwtVerifier verifier = new CachingJwtVerifier(counting(), 10, Duration.ofMinutes(1));
        String jwt = tokenWithNestedClaims();
        verifier.verify(jwt);

        // When
        Jws<Claims> cached = verifier.verify(jwt);

        // Then
        Assert.assertEquals(cached.accept(Jws.CLAIMS).getPayload().getSubject(), "alice");
        Assert.assertTrue(cached.toString().contains("alice"), cached.toString());
    }

    @Test
    public void givenTokenWithEmbeddedJwkHeader_whenServedFromCache_thenHeaderIsPreserved() {
        // Given: an ES256 token whose header carries its public key as a JWK, and a key ID
        KeyPair pair = Jwts.SIG.ES256.keyPair().build();
        PublicJwk<PublicKey> jwk = Jwks.builder().key(pair.getPublic()).idFromThumbprint().build();
        String jwt = Jwts.builder()
                         .header().keyId(jwk.getId()).jwk(jwk).and()
                         .subject("alice")
                         .expiration(Date.from(Instant.now().plusSeconds(3600)))
                         .signWith(pair.getPrivate())
                         .compact();
        CountingVerifier delegate = new CountingVerifier(new SignedJwtVerifier(pair.getPublic()));
        CachingJwtVerifier verifier = new CachingJwtVerifier(delegate, 10, Duration.ofMinutes(1));
        Jws<Claims> fresh = verifier.verify(jwt);

        // When
        Jws<Claims> cached = verifier.verify(jwt);

        // Then
        Assert.assertEquals(delegate.calls.get(), 1);
        Assert.assertEquals(cached.getHeader(), fresh.getHeader());
        Assert.assertEquals(cached.getHeader().getKeyId(), jwk.getId());
        Assert.assertEquals(cached.getHeader().getJwk(), jwk);
        Assert.assertEquals(cached.getHeader().getAlgorithm(), "ES256");
    }

    /**
     * A JSON deserializer, as an application might configure, that produces primitive arrays for some claims: {@code bytes}
     * as a {@code byte[]} and {@code ints} as an {@code int[]}
     */
    private static final class PrimitiveArrayDeserializer implements Deserializer<Map<String, ?>> {
        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public Map<String, ?> deserialize(byte[] bytes) {
            try {
                return convert(this.mapper.readValue(bytes, Map.class));
            } catch (IOException e) {
                throw new DeserializationException("Unable to deserialize", e);
            }
        }

        @Override
        public Map<String, ?> deserialize(Reader reader) {
            try {
                return convert(this.mapper.readValue(reader, Map.class));
            } catch (IOException e) {
                throw new DeserializationException("Unable to deserialize", e);
            }
        }

        private static Map<String, ?> convert(Map<?, ?> raw) {
            Map<String, Object> map = new LinkedHashMap<>();
            raw.forEach((k, v) -> map.put(String.valueOf(k), v));
            if (map.get("bytes") instanceof List<?> list) {
                byte[] bytes = new byte[list.size()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = ((Number) list.get(i)).byteValue();
                }
                map.put("bytes", bytes);
            }
            if (map.get("ints") instanceof List<?> list) {
                map.put("ints", list.stream().mapToInt(n -> ((Number) n).intValue()).toArray());
            }
            return map;
        }
    }

    @Test
    public void givenPrimitiveArrayClaims_whenInitialResultAndCacheHitsAreModified_thenLaterResultsUnaffected() {
        // Given
        CountingVerifier delegate = new CountingVerifier(new SignedJwtVerifier(
                JwtParsers.builder().verifyWith(this.key).json(new PrimitiveArrayDeserializer()).build()));
        CachingJwtVerifier verifier = new CachingJwtVerifier(delegate, 10, Duration.ofMinutes(1));
        String jwt = Jwts.builder()
                         .subject("alice")
                         .expiration(Date.from(Instant.now().plusSeconds(3600)))
                         .claim("bytes", List.of(1, 2, 3))
                         .claim("ints", List.of(10, 20, 30))
                         .signWith(this.key)
                         .compact();

        // When: the first (uncached) result is modified
        Jws<Claims> first = verifier.verify(jwt);
        Assert.assertTrue(first.getPayload().get("bytes") instanceof byte[]);
        ((byte[]) first.getPayload().get("bytes"))[0] = 99;
        ((int[]) first.getPayload().get("ints"))[0] = 99;

        // Then: a cache hit still has the signed values
        Jws<Claims> second = verifier.verify(jwt);
        Assert.assertEquals((byte[]) second.getPayload().get("bytes"), new byte[] { 1, 2, 3 });
        Assert.assertEquals((int[]) second.getPayload().get("ints"), new int[] { 10, 20, 30 });

        // When: a cache hit is modified
        ((byte[]) second.getPayload().get("bytes"))[1] = 99;
        ((int[]) second.getPayload().get("ints"))[1] = 99;

        // Then: later cache hits still have the signed values
        Jws<Claims> third = verifier.verify(jwt);
        Assert.assertEquals((byte[]) third.getPayload().get("bytes"), new byte[] { 1, 2, 3 });
        Assert.assertEquals((int[]) third.getPayload().get("ints"), new int[] { 10, 20, 30 });
        Assert.assertEquals(delegate.calls.get(), 1);
    }

    @Test
    public void givenIndependentCaches_whenReturningTheSameToken_thenResultsHaveEqualContentAndHashCodes() {
        CachingJwtVerifier firstVerifier = new CachingJwtVerifier(counting(), 10, Duration.ofMinutes(1));
        CachingJwtVerifier secondVerifier = new CachingJwtVerifier(counting(), 10, Duration.ofMinutes(1));
        String jwt = tokenWithNestedClaims();
        firstVerifier.verify(jwt);
        secondVerifier.verify(jwt);

        Jws<Claims> first = firstVerifier.verify(jwt);
        Jws<Claims> second = secondVerifier.verify(jwt);

        Assert.assertNotSame(first, second);
        Assert.assertEquals(first, second);
        Assert.assertEquals(second, first);
        Assert.assertEquals(first.hashCode(), second.hashCode());
        Assert.assertNotEquals(first, null);
        Assert.assertNotEquals(first, "not a JWT");
    }

}
