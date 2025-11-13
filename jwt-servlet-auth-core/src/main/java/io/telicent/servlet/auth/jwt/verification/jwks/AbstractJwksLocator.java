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
import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Jwk;
import io.jsonwebtoken.security.JwkSet;
import io.telicent.servlet.auth.jwt.errors.KeyLoadException;
import io.telicent.servlet.auth.jwt.verification.KeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Objects;

/**
 * Abstract base classes for JWKS locators that use the JDK {@link HttpClient} for HTTP requests
 */
public abstract class AbstractJwksLocator extends LocatorAdapter<Key> {
    private static HttpClient DEFAULT_CLIENT;

    /**
     * The configured HTTP client to use for any HTTP requests
     */
    protected final HttpClient client;

    /**
     * Creates a new abstract JWKS locator
     *
     * @param client HTTP Client to use
     */
    public AbstractJwksLocator(HttpClient client) {
        this.client = Objects.requireNonNull(client, "HTTP Client cannot be null");
    }

    /**
     * Creates the default HTTP Client
     *
     * @return HTTP Client
     */
    protected static synchronized HttpClient createDefaultClient() {
        if (DEFAULT_CLIENT == null) {
            DEFAULT_CLIENT = HttpClient.newBuilder().build();
        }
        return DEFAULT_CLIENT;
    }

    /**
     * Gets the JWKS URI in use
     * <p>
     * Depending on the locator this may be dynamically resolved the first time this is called.
     * </p>
     *
     * @return JWKS URI
     */
    protected abstract URI getJwksURI();

    @Override
    protected Key locate(JwsHeader header) {
        String keyId = ensureValidKeyId(header);
        JwkSet jwks = loadJwks(this.getJwksURI());
        Jwk<?> jwk = locateKey(jwks, keyId);
        ensureKeyPresent(header, jwk);
        return jwk.toKey();
    }

    /**
     * Ensures that the key is not {@code null}, otherwise throws an error
     *
     * @param header JWS Header
     * @param jwk    JWK
     * @throws InvalidKeyException Thrown if no key was found
     */
    protected void ensureKeyPresent(JwsHeader header, Jwk<?> jwk) {
        if (jwk == null) {
            throw new InvalidKeyException(
                    "Key ID '" + header.getKeyId() + "' not present in JWKS at URI " + this.getJwksURI().toString());
        }
    }

    /**
     * Loads the JWKS resource
     *
     * @param jwksSourceUri Source URI from which the JWKS should be read
     * @return JWKS resource
     */
    protected JwkSet loadJwks(URI jwksSourceUri) {
        JwkSet jwks;
        try {
            if (Strings.CS.equals(jwksSourceUri.getScheme(), "file")) {
                // Read in File
                File f = Paths.get(jwksSourceUri).toFile();
                jwks = KeyUtils.loadJwks(f);
            } else {
                // Read in URL
                jwks = KeyUtils.loadJwks(jwksSourceUri, this.client);
            }
        } catch (KeyLoadException e) {
            throw new InvalidKeyException(e.getMessage(), e.getCause());
        }
        return jwks;
    }

    /**
     * Ensures that a valid Key ID is provided in the JWS header via the {@code kid} header
     *
     * @param header JWS Header
     * @return Key ID
     * @throws InvalidKeyException Thrown if there is no {@code kid} header present in the JWS header
     */
    protected String ensureValidKeyId(JwsHeader header) {
        String keyId = header.getKeyId();
        if (StringUtils.isBlank(keyId)) {
            throw new InvalidKeyException("JWS fails to declare a valid kid header");
        }
        return keyId;
    }

    /**
     * Locates a Key by ID in the given JWKS
     *
     * @param jwks  JWKS
     * @param keyId Key ID
     * @return Key, or {@code null} if no such key is present
     */
    protected Jwk<?> locateKey(JwkSet jwks, String keyId) {
        // This is a bit hacky, would be nice if there was a simpler way to just call get() on the JwkSet and get the
        // associated key back directly.  But this isn't happening per https://github.com/jwtk/jjwt/issues/919 so have
        // to live with this
        return jwks.getKeys()
                   .stream()
                   .filter(k -> Strings.CS.equals(k.getId(), keyId))
                   .findFirst()
                   .orElse(null);
    }
}
