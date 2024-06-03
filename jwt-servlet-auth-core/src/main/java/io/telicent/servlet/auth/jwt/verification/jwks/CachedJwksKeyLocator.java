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

import java.net.URI;
import java.net.http.HttpClient;
import java.security.Key;
import java.time.Duration;

/**
 * A variant of {@link UrlJwksKeyLocator} that adds a caching layer so that the underlying JWKS file/URL is only loaded
 * upon encountering a key that is not currently cached
 */
public class CachedJwksKeyLocator extends UrlJwksKeyLocator {

    private final Cache<String, Jwk<?>> cache;
    private final Duration cacheKeysFor;

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
        super(jwksURI, client);
        this.cacheKeysFor = cacheKeysFor;
        // Generally speaking there are relatively few keys are used in a JWKS, so we set a relatively compact cache
        // size to minimise memory footprint
        this.cache = Caffeine.newBuilder()
                             .initialCapacity(10)
                             .maximumSize(25)
                             .expireAfterAccess(cacheKeysFor)
                             .build();
    }

    @Override
    protected Key locate(JwsHeader header) {
        String keyId = this.ensureValidKeyId(header);

        // Use the previously cached key if present
        Jwk<?> jwk = this.cache.getIfPresent(keyId);
        if (jwk != null) {
            return jwk.toKey();
        }

        // Otherwise load the JWKS and cache the contained keys
        JwkSet jwks = this.loadJwks();
        jwks.getKeys().forEach(k -> this.cache.put(k.getId(), k));

        // Then lookup the key again
        jwk = this.cache.getIfPresent(keyId);
        ensureKeyPresent(header, jwk);
        return jwk.toKey();
    }

    @Override
    public String toString() {
        return "CachedJwksKeyLocator{jwksUrl=" + this.jwksURI.toString() + ", cacheKeysFor=" + this.cacheKeysFor.toString() + "}";
    }
}
