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
 * A key locator that reads in a JWKS from a file/URI and locates keys based upon the IDs declared in their {@code kid}
 * header
 * <p>
 * This uses the new {@link JwkSet} support available in {@code jjwt} since {@code 0.8.0} and so supports the full range
 * of key types and algorithms mandated by the JWKS specification.
 * </p>
 * <p>
 * Note that this implementation is naive in that it loads the JWKS on every location attempt, see the
 * {@link CachedJwksKeyLocator} for a more intelligent implementation that includes caching of the retrieved keys and
 * only reloads the JWKS periodically.
 * </p>
 *
 * @since 0.8.0
 */
public class UrlJwksKeyLocator extends LocatorAdapter<Key> {

    private static HttpClient DEFAULT_CLIENT;

    /**
     * Supported URI schemes for retrieving a JWKS resource
     */
    public static final String[] SUPPORTED_SCHEMES = { "http", "https", "file" };

    private final HttpClient client;
    /**
     * The configured JWKS URI
     */
    protected final URI jwksURI;

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
     * Creates a new locator that will use a default HTTP Client
     *
     * @param jwksURI JWKS URI
     */
    public UrlJwksKeyLocator(URI jwksURI) {
        this(jwksURI, createDefaultClient());
    }

    /**
     * Creates a new locator
     *
     * @param jwksURI JWKS URI
     * @param client  HTTP Client
     */
    public UrlJwksKeyLocator(URI jwksURI, HttpClient client) {
        this.jwksURI = Objects.requireNonNull(jwksURI, "JWKS URI cannot be null");
        if (!isSupportedScheme(jwksURI)) {
            throw new IllegalArgumentException(
                    "JWKS URI does not use any of the supported schemes: " + StringUtils.join(SUPPORTED_SCHEMES, ", "));
        }
        this.client = Objects.requireNonNull(client, "HTTP Client cannot be null");
    }

    /**
     * Determines whether the URI uses one of the schemes supporting for loading by this implementation
     *
     * @param jwksURI JWKS URI
     * @return True if a supported scheme, false otherwise
     */
    protected static boolean isSupportedScheme(URI jwksURI) {
        return Strings.CS.equalsAny(jwksURI.getScheme(), SUPPORTED_SCHEMES);
    }

    @Override
    protected Key locate(JwsHeader header) {
        String keyId = ensureValidKeyId(header);
        JwkSet jwks = loadJwks();
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
                    "Key ID '" + header.getKeyId() + "' not present in JWKS at URI " + this.jwksURI.toString());
        }
    }

    /**
     * Loads the JWKS resource
     *
     * @return JWKS resource
     */
    protected JwkSet loadJwks() {
        JwkSet jwks;
        try {
            if (Strings.CS.equals(this.jwksURI.getScheme(), "file")) {
                // Read in File
                File f = Paths.get(this.jwksURI).toFile();
                jwks = KeyUtils.loadJwks(f);
            } else {
                // Read in URL
                jwks = KeyUtils.loadJwks(this.jwksURI, this.client);
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

    @Override
    public String toString() {
        return "UrlJwksKeyLocator{jwksUrl=" + this.jwksURI.toString() + "}";
    }
}
