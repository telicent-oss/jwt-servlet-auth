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
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * A fake token verifier that treats all tokens as valid <strong>BUT</strong> returns a JWT that contains the username,
 * or variants thereof, in a number of different claims.  The default claim contains a random UUID to verify that the
 * engine really is consulting the correct claim.
 */
public class MultipleClaimsTokenVerifier extends FakeTokenVerifier {

    private final String usernameClaim;

    /**
     * Creates a new fake verifier
     *
     * @param usernameClaim Username claim into which the actual username will be inserted
     */
    public MultipleClaimsTokenVerifier(String usernameClaim) {
        this.usernameClaim = usernameClaim;
    }

    @Override
    public Jws<Claims> verify(String rawJwt) {
        String generatedJws = Jwts.builder()
                                  .subject(UUID.randomUUID().toString())
                                  .claims()
                                  .add(
                                          //@formatter:off
                                          Map.of(this.usernameClaim, rawJwt,
                                                 "lower", rawJwt.toLowerCase(Locale.ROOT),
                                                 "upper", rawJwt.toUpperCase(Locale.ROOT),
                                                 "start", rawJwt.substring(0, 1),
                                                 "end", rawJwt.substring(rawJwt.length() - 1),
                                                 "empty", "",
                                                 "blank", "     "))
                                          //@formatter:on
                                  .and()
                                  .issuer("test")
                                  .expiration(Date.from(Instant.now().plus(this.expiresIn, this.unit)))
                                  .signWith(this.key)
                                  .compact();
        return this.parser.parseSignedClaims(generatedJws);
    }
}
