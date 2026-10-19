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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtVisitor;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Jwk;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * A {@link JwtVerifier} that remembers successfully verified tokens for a short while, so that a client presenting the
 * same token on many requests doesn't pay for verifying its signature (and parsing it) every time.
 * <p>
 * Signature verification is often the single most expensive step of authenticating a request, e.g. for ES256 tokens,
 * and the same token is typically presented on every request a client makes until it expires.
 * </p>
 * <h2>Behaviour</h2>
 * <ul>
 * <li>Only <strong>successful</strong> verifications are cached.  A token that fails verification is passed to the
 * delegate verifier every time, and any exception it throws propagates unchanged.</li>
 * <li>A cached token is returned for at most the configured maximum age, and never beyond its own expiry
 * ({@code exp}) claim.  Once past either, the token is passed to the delegate again, so any configured clock skew
 * allowance is still applied exactly as by the delegate.</li>
 * <li>Tokens are keyed by the SHA-256 digest of the raw token, so raw tokens are not retained as cache keys, and the
 * whole token (header, claims and signature) must match for a cached result to be used.</li>
 * <li>Every caller gets its own copy of the header and claims.  The cache keeps a private deep copy of them, taken when
 * the token is first verified, and each cached result is a fresh deep copy of that, so a caller modifying its result
 * (including nested values such as a roles list) can't affect what later requests see.  As with a fresh verification,
 * the header and claims maps themselves are unmodifiable and nested values are ordinary mutable collections.</li>
 * <li>The cache holds at most the configured number of tokens; beyond that, Caffeine's size eviction policy decides
 * which to forget.</li>
 * </ul>
 * <h2>Security considerations</h2>
 * <p>
 * A token that has been verified continues to be accepted for up to the maximum age even if the key that signed it is
 * withdrawn in the meantime (e.g. removed from a JWKS).  Keys retrieved from a JWKS are themselves cached (see
 * {@link io.telicent.servlet.auth.jwt.verification.jwks.CachedJwksKeyLocator}) so keep the maximum age short relative to
 * that.  JWTs cannot be revoked individually, so no other validity information is lost by caching.
 * </p>
 */
public class CachingJwtVerifier implements JwtVerifier {

    /**
     * A verified token and the time until which it may be returned from the cache.  Nothing here is ever handed out.
     *
     * @param header    Private deep copy of the verified header
     * @param claims    Private deep copy of the verified claims
     * @param signature Signature
     * @param digest    Private copy of the digest
     * @param expiresAt Time after which the token must be verified again
     */
    private record CachedJws(Map<String, Object> header, Map<String, Object> claims, String signature, byte[] digest,
                             Instant expiresAt) {
        @Override
        public boolean equals(Object other) {
            return other instanceof CachedJws(var otherHeader, var otherClaims, var otherSignature, var otherDigest,
                                                var otherExpiresAt)
                   && Objects.equals(this.header, otherHeader)
                   && Objects.equals(this.claims, otherClaims)
                   && Objects.equals(this.signature, otherSignature)
                   && Arrays.equals(this.digest, otherDigest)
                   && Objects.equals(this.expiresAt, otherExpiresAt);
        }

        @Override
        public int hashCode() {
            return 31 * Objects.hash(this.header, this.claims, this.signature, this.expiresAt)
                   + Arrays.hashCode(this.digest);
        }

        @Override
        public String toString() {
            return "CachedJws[header=" + this.header + ", claims=" + this.claims + ", signature=" + this.signature
                   + ", digest=" + Arrays.toString(this.digest) + ", expiresAt=" + this.expiresAt + "]";
        }
    }

    /**
     * A cached verification result as returned to a caller, with the caller's own copies of the header, claims and
     * digest
     *
     * @param header    Caller's copy of the header
     * @param claims    Caller's copy of the claims
     * @param signature Signature
     * @param digest    Caller's copy of the digest
     */
    private record CopiedJws(JwsHeader header, Claims claims, String signature, byte[] digest) implements Jws<Claims> {
        @Override
        public boolean equals(Object other) {
            return other instanceof CopiedJws(var otherHeader, var otherClaims, var otherSignature, var otherDigest)
                   && Objects.equals(this.header, otherHeader)
                   && Objects.equals(this.claims, otherClaims)
                   && Objects.equals(this.signature, otherSignature)
                   && Arrays.equals(this.digest, otherDigest);
        }

        @Override
        public int hashCode() {
            return 31 * Objects.hash(this.header, this.claims, this.signature) + Arrays.hashCode(this.digest);
        }

        @Override
        public JwsHeader getHeader() {
            return this.header;
        }

        @Override
        @SuppressWarnings("deprecation")
        public Claims getBody() {
            return this.claims;
        }

        @Override
        public Claims getPayload() {
            return this.claims;
        }

        @Override
        public String getSignature() {
            return this.signature;
        }

        @Override
        public byte[] getDigest() {
            return this.digest != null ? this.digest.clone() : null;
        }

        @Override
        public <T> T accept(JwtVisitor<T> visitor) {
            return visitor.visit(this);
        }

        @Override
        public String toString() {
            return "header=" + getHeader() + ",payload=" + this.claims + ",signature=" + getSignature();
        }
    }

    private final JwtVerifier delegate;
    private final Duration maxAge;
    private final long maxSize;
    private final Clock clock;
    private final Cache<String, CachedJws> cache;

    /**
     * Creates a new caching verifier
     *
     * @param delegate Verifier that actually verifies tokens
     * @param maxSize  Maximum number of verified tokens to remember, must be greater than zero
     * @param maxAge   Maximum time to remember a verified token for, must be positive
     */
    public CachingJwtVerifier(JwtVerifier delegate, long maxSize, Duration maxAge) {
        this(delegate, maxSize, maxAge, Clock.systemUTC());
    }

    /**
     * Creates a new caching verifier using the given clock, intended for testing
     *
     * @param delegate Verifier that actually verifies tokens
     * @param maxSize  Maximum number of verified tokens to remember, must be greater than zero
     * @param maxAge   Maximum time to remember a verified token for, must be positive
     * @param clock    Clock
     */
    CachingJwtVerifier(JwtVerifier delegate, long maxSize, Duration maxAge, Clock clock) {
        this.delegate = Objects.requireNonNull(delegate, "Delegate verifier cannot be null");
        this.maxAge = Objects.requireNonNull(maxAge, "Maximum age cannot be null");
        this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Maximum size must be greater than zero");
        }
        if (maxAge.isZero() || maxAge.isNegative()) {
            throw new IllegalArgumentException("Maximum age must be positive");
        }
        this.maxSize = maxSize;
        // NB - Correctness relies on the expiry check in verify(), using the clock; the cache's own expiry is only
        //      there so that tokens nobody presents again don't linger until evicted by size
        this.cache = Caffeine.newBuilder().maximumSize(maxSize).expireAfterWrite(maxAge).build();
    }

    @Override
    public Jws<Claims> verify(String rawJwt) {
        if (rawJwt == null) {
            return this.delegate.verify(null);
        }
        String key = digest(rawJwt);
        Instant now = this.clock.instant();
        CachedJws cached = this.cache.getIfPresent(key);
        if (cached != null) {
            if (now.isBefore(cached.expiresAt())) {
                return new CopiedJws(headerCopyOf(cached.header()), claimsCopyOf(cached.claims()), cached.signature(),
                                     cached.digest());
            }
            this.cache.invalidate(key);
        }

        // Not cached, or no longer usable, so verify it for real.  Any exception propagates without caching anything.
        Jws<Claims> jws = this.delegate.verify(rawJwt);
        cacheVerified(key, jws, now);
        return jws;
    }

    private void cacheVerified(String key, Jws<Claims> jws, Instant now) {
        if (jws == null || jws.getPayload() == null || jws.getHeader() == null) {
            return;
        }
        Instant expiresAt = now.plus(this.maxAge);
        Date expiration = jws.getPayload().getExpiration();
        if (expiration != null && expiration.toInstant().isBefore(expiresAt)) {
            expiresAt = expiration.toInstant();
        }
        if (now.isBefore(expiresAt)) {
            // Cache private deep copies so the caller modifying its result can't affect later requests.
            byte[] digest = jws.getDigest();
            this.cache.put(key, new CachedJws(deepCopyMap(jws.getHeader()), deepCopyMap(jws.getPayload()),
                                              jws.getSignature(), digest != null ? digest.clone() : null, expiresAt));
        }
    }

    /**
     * Gets the verifier that actually verifies tokens
     *
     * @return Delegate verifier
     */
    public JwtVerifier getDelegate() {
        return this.delegate;
    }

    /**
     * Gets the approximate number of tokens currently cached, intended for testing and diagnostics
     *
     * @return Approximate number of cached tokens
     */
    long cachedTokens() {
        this.cache.cleanUp();
        return this.cache.estimatedSize();
    }

    /**
     * Creates a caller's copy of cached claims, with the same shape as a freshly verified token's claims
     *
     * @param claims Cached claims
     * @return Claims
     */
    private static Claims claimsCopyOf(Map<String, Object> claims) {
        return Jwts.claims().add(deepCopyMap(claims)).build();
    }

    /**
     * Creates a caller's copy of a cached header, with the same shape as a freshly verified token's header
     *
     * @param header Cached header
     * @return Header
     */
    private static JwsHeader headerCopyOf(Map<String, Object> header) {
        Header copy = Jwts.header().add(deepCopyMap(header)).build();
        if (copy instanceof JwsHeader jwsHeader) {
            return jwsHeader;
        }
        // Can't happen for the header of a verified signed token, which always has an algorithm
        throw new IllegalStateException("Cached header is not a JWS header");
    }

    private static Map<String, Object> deepCopyMap(Map<?, ?> map) {
        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            copy.put(String.valueOf(entry.getKey()), deepCopy(entry.getValue()));
        }
        return copy;
    }

    /**
     * Deep copies a header or claim value: maps, lists, sets, arrays (object and primitive) and dates are copied, other
     * values (strings, numbers, booleans, and JJWT's immutable JWKs) are shared
     */
    private static Object deepCopy(Object value) {
        return switch (value) {
            case null -> null;
            // NB - A JWK is a Map but immutable, and must stay a JWK
            case Jwk<?> jwk -> jwk;
            case Map<?, ?> map -> deepCopyMap(map);
            case Set<?> set -> {
                Set<Object> copy = new LinkedHashSet<>();
                set.forEach(item -> copy.add(deepCopy(item)));
                yield copy;
            }
            case Collection<?> collection -> {
                List<Object> copy = new ArrayList<>();
                collection.forEach(item -> copy.add(deepCopy(item)));
                yield copy;
            }
            case Object[] array -> {
                Object[] copy = array.clone();
                for (int i = 0; i < copy.length; i++) {
                    copy[i] = deepCopy(copy[i]);
                }
                yield copy;
            }
            // NB - Primitive arrays (e.g. byte[] from a custom JSON deserializer) don't match Object[] above
            case Object array when array.getClass().isArray() -> {
                int length = Array.getLength(array);
                Object copy = Array.newInstance(array.getClass().getComponentType(), length);
                System.arraycopy(array, 0, copy, 0, length);
                yield copy;
            }
            case Date date -> new Date(date.getTime());
            default -> value;
        };
    }

    private static String digest(String rawJwt) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(sha256.digest(rawJwt.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            // Every Java platform is required to support SHA-256
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s{maxSize=%d, maxAge=%s, delegate=%s}", this.getClass().getSimpleName(), this.maxSize,
                             this.maxAge, this.delegate);
    }
}
