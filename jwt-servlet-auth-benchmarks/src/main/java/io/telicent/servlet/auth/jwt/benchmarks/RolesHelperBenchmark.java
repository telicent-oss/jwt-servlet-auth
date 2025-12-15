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
package io.telicent.servlet.auth.jwt.benchmarks;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.telicent.servlet.auth.jwt.configuration.ClaimPath;
import io.telicent.servlet.auth.jwt.roles.RolesHelper;
import org.openjdk.jmh.annotations.*;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks role extraction via RolesHelper.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class RolesHelperBenchmark {

    /**
     * Shared state for the RolesHelper benchmarks.
     */
    @State(Scope.Benchmark)
    public static class RolesState {

        SecretKey key;
        Jws<Claims> jws;
        ClaimPath rolesClaimPath;
        RolesHelper warmHelper;

        @Setup(Level.Trial)
        public void setup() {
            this.key = Jwts.SIG.HS256.key().build();

            Map<String, Object> realmAccess = Map.of(
                    "roles", List.of("admin", "user", "viewer")
            );

            Instant now = Instant.now();
            JwtBuilder builder = Jwts.builder()
                    .subject("benchmark-user")
                    .issuer("jwt-servlet-auth-benchmarks")
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(now.plus(Duration.ofHours(1))))
                    .claim("realm_access", realmAccess);

            String token = builder
                    .signWith(this.key)
                    .compact();

            JwtParser parser = Jwts.parser()
                    .verifyWith(this.key)
                    .build();
            this.jws = parser.parseSignedClaims(token);

            this.rolesClaimPath = ClaimPath.of("realm_access", "roles");

            this.warmHelper = new RolesHelper(this.jws, this.rolesClaimPath);

            this.warmHelper.isUserInRole("admin");
        }
    }

    /**
     * "Cold" path: creates a fresh RolesHelper on each call.
     * Includes roles claim lookup plus roles set creation.
     *
     * @param state shared roles benchmark state
     * @return {@code true} if the "admin" role is present
     */
    @Benchmark
    public boolean coldIsUserInRole(RolesState state) {
        RolesHelper helper = new RolesHelper(state.jws, state.rolesClaimPath);
        return helper.isUserInRole("admin");
    }

    /**
     * "Warm" path: reuses a single RolesHelper with cached roles.
     * Measures pure set lookup and control flow.
     *
     * @param state shared roles benchmark state
     * @return {@code true} if the "admin" role is present
     */
    @Benchmark
    public boolean warmIsUserInRole(RolesState state) {
        return state.warmHelper.isUserInRole("admin");
    }

}
