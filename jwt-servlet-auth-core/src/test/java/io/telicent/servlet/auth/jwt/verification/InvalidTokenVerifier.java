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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

import java.util.function.Function;

/**
 * A token verifier for simulating errors in tests
 */
public class InvalidTokenVerifier implements JwtVerifier {

    private final Function<String, RuntimeException> errorSupplier;

    /**
     * Creates a new verifier that generates a default error
     */
    public InvalidTokenVerifier() {
        this(token -> {throw new JwtException("Not a valid token: " + token);});
    }

    /**
     * Creates a new verifier providing a function that generates the desired error
     *
     * @param errorSupplier Error supplier function
     */
    public InvalidTokenVerifier(Function<String, RuntimeException> errorSupplier) {
        this.errorSupplier = errorSupplier;
    }

    @Override
    public Jws<Claims> verify(String rawJwt) {
        throw this.errorSupplier.apply(rawJwt);
    }
}
