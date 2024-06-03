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

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Jwks;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.PublicKey;
import java.util.Objects;

/**
 * A JSON Web Token (JWT) verifier that verifies that the JWT is cryptographically signed and thus has not been tampered
 * with
 */
public class SignedJwtVerifier implements JwtVerifier {

    /**
     * The default debug string used when a secret key is configured
     */
    public static final String SECRET_KEY_DEBUG_STRING = "verificationMethod=SecretKey";

    /**
     * Gets the default debug string for a public key
     *
     * @param publicKey Public Key
     * @return Debug string
     */
    public static String debugStringForPublicKey(PublicKey publicKey) {
        Objects.requireNonNull(publicKey);
        return String.format("verificationMethod=PublicKey, fingerprint=%s",
                             Jwks.builder().key(publicKey).build().thumbprint().toString());
    }

    /**
     * Gets the default debug string for a key locator
     *
     * @param locator Key Locator
     * @return Debug string
     */
    public static String debugStringForLocator(Locator<Key> locator) {
        Objects.requireNonNull(locator);
        return String.format("verificationMethod=Locator, locator=%s", locator);
    }


    private final JwtParser parser;
    private final String debugString;

    /**
     * Creates a new verifier
     *
     * @param parser A JWT parser
     */
    public SignedJwtVerifier(JwtParser parser) {
        this(parser, null);
    }

    /**
     * Creates a new verifier
     *
     * @param parser      A JWT parser
     * @param debugString Optional debug string describing the parser configuration
     */
    public SignedJwtVerifier(JwtParser parser, String debugString) {
        this.parser = Objects.requireNonNull(parser, "Parser cannot be null");
        this.debugString = String.format("%s{%s}", this.getClass().getSimpleName(), debugString);
    }

    /**
     * Creates a new verifier that creates a basic JWT parser which verifies signatures using the given public key
     *
     * @param signingKey Singing key
     */
    public SignedJwtVerifier(PublicKey signingKey) {
        this(Jwts.parser().verifyWith(signingKey).build(),
             debugStringForPublicKey(signingKey));
    }

    /**
     * Creates a new verifier that creates a basic JWT parser which verifies signatures using the given secret key
     *
     * @param signingKey Singing key
     */
    public SignedJwtVerifier(SecretKey signingKey) {
        this(Jwts.parser().verifyWith(signingKey).build(), SECRET_KEY_DEBUG_STRING);
    }

    /**
     * Creates a new verifier that creates a basic JWT parser using the given key locator
     *
     * @param locator Key locator
     */
    public SignedJwtVerifier(Locator<Key> locator) {
        this(Jwts.parser().keyLocator(locator).build(),
             debugStringForLocator(locator));
    }


    @Override
    public Jws<Claims> verify(String rawJwt) {
        return this.parser.parseSignedClaims(rawJwt);
    }

    @Override
    public String toString() {
        if (this.debugString != null) {
            return this.debugString;
        } else {
            return this.getClass().getSimpleName();
        }
    }
}
