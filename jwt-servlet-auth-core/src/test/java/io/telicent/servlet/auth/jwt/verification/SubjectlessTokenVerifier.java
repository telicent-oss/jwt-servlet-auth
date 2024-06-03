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
import io.jsonwebtoken.Jwts;

import java.time.Instant;
import java.util.Date;

/**
 * A fake token verifier that treats all tokens as valid <strong>BUT</strong> returns a JWT that <strong>DOES
 * NOT</strong> contain a username
 */
public class SubjectlessTokenVerifier extends FakeTokenVerifier {

    @Override
    public Jws<Claims> verify(String rawJwt) {
        String generatedJws =
                Jwts.builder()
                    .issuer("test")
                    .expiration(Date.from(Instant.now().plus(this.expiresIn, this.unit)))
                    .signWith(this.key)
                    .compact();
        return this.parser.parseSignedClaims(generatedJws);
    }
}
