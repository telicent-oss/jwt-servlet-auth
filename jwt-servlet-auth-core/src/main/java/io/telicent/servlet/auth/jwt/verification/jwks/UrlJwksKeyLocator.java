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

import io.jsonwebtoken.security.JwkSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.net.URI;
import java.net.http.HttpClient;
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
public class UrlJwksKeyLocator extends AbstractJwksLocator {

    /**
     * Supported URI schemes for retrieving a JWKS resource
     */
    public static final String[] SUPPORTED_SCHEMES = { "http", "https", "file" };

    /**
     * The configured JWKS URI
     */
    protected final URI jwksURI;

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
        super(client);
        this.jwksURI = Objects.requireNonNull(jwksURI, "JWKS URI cannot be null");
        if (!isSupportedScheme(jwksURI)) {
            throw new IllegalArgumentException(
                    "JWKS URI does not use any of the supported schemes: " + StringUtils.join(SUPPORTED_SCHEMES, ", "));
        }
    }

    @Override
    protected URI getJwksURI() {
        return this.jwksURI;
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
    public String toString() {
        return "UrlJwksKeyLocator{jwksUrl=" + this.jwksURI.toString() + "}";
    }
}
