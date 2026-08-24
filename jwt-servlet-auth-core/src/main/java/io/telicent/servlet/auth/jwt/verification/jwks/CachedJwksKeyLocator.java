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

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Cache;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.security.Jwk;
import io.jsonwebtoken.security.JwkSet;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.security.Key;
import java.time.Duration;

/**
 * A decorator over another {@link AbstractJwksLocator} that adds a caching layer so that the underlying JWKS file/URL
 * is only loaded upon encountering a key that is not currently cached
 */
public class CachedJwksKeyLocator extends AbstractJwksLocator {

    private final Cache<String, Jwk<?>> cache;
    private final Duration cacheKeysFor;
    private final AbstractJwksLocator jwksLocator;

    /**
     * Creates a new JWKS key locator with caching of keys enabled
     *
     * @param jwksURI      JWKS URI
     * @param cacheKeysFor How long keys should be cached for
     */
    public CachedJwksKeyLocator(URI jwksURI, Duration cacheKeysFor) {
        this(jwksURI, createDefaultClient(), cacheKeysFor);
    }

    /**
     * Creates a new JWKS key locator with caching of keys enabled
     *
     * @param jwksURI      JWKS URI
     * @param client       HTTP Client
     * @param cacheKeysFor How long keys should be cached for
     */
    public CachedJwksKeyLocator(URI jwksURI, HttpClient client, Duration cacheKeysFor) {
        this(new UrlJwksKeyLocator(jwksURI, client), cacheKeysFor);
    }

    public CachedJwksKeyLocator(AbstractJwksLocator jwksLocator, Duration cacheKeysFor) {
        super(jwksLocator.client);
        this.jwksLocator = jwksLocator;
        this.cacheKeysFor = cacheKeysFor;
        // Generally speaking there are relatively few keys are used in a JWKS, so we set a relatively compact cache
        // size to minimise memory footprint
        this.cache = Caffeine.newBuilder().initialCapacity(10).maximumSize(25).expireAfterAccess(cacheKeysFor).build();
    }

    @Override
    protected URI getJwksURI() {
        return this.jwksLocator.getJwksURI();
    }

    @Override
    // Sonar S2259 - the reported null dereference on jwk.toKey() is unreachable: ensureKeyPresent() throws
    // InvalidKeyException when jwk is null.  Sonar cannot see that contract because the method is inherited from
    // AbstractJwksLocator in a different compilation unit, while Caffeine's getIfPresent() carries a @Nullable
    // annotation that it does trust.
    @SuppressWarnings("java:S2259")
    protected Key locate(JwsHeader header) {
        String keyId = this.ensureValidKeyId(header);

        // Use the previously cached key if present
        Jwk<?> jwk = this.cache.getIfPresent(keyId);
        if (jwk != null) {
            return jwk.toKey();
        }

        // Otherwise load the JWKS and cache the contained keys
        JwkSet jwks = this.jwksLocator.loadJwks(this.jwksLocator.getJwksURI());
        // NB - The kid header is optional per RFC 7517, so Jwk.getId() can be null, and Caffeine rejects null keys
        //      with a NullPointerException.  Without this filter a single keyless entry anywhere in the JWKS would
        //      make every verification through this locator fail with an NPE escaping locate(), which surfaces as a
        //      500 rather than a 401.  Keys without an id cannot be looked up by kid anyway.
        jwks.getKeys().stream().filter(k -> StringUtils.isNotBlank(k.getId())).forEach(k -> this.cache.put(k.getId(), k));

        // Then lookup the key again
        jwk = this.cache.getIfPresent(keyId);
        ensureKeyPresent(header, jwk);
        return jwk.toKey();
    }

    @Override
    public String toString() {
        return "CachedJwksKeyLocator{jwksLocator=" + this.jwksLocator + ", cacheKeysFor=" + this.cacheKeysFor.toString() + "}";
    }
}
