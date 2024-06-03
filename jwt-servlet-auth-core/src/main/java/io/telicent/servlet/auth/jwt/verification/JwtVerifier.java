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

/**
 * Verifies a JSON Web Token (JWT)
 */
public interface JwtVerifier {

    /**
     * Verifies the provided raw JSON Web Token, throwing an exception or returning {@code null} if it is invalid
     *
     * @param rawJwt Raw JSON Web Token
     * @return Verified JSON Web Token
     */
    Jws<Claims> verify(String rawJwt);
}
