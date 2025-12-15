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
import io.telicent.servlet.auth.jwt.verification.SignedJwtVerifier;
import org.openjdk.jmh.annotations.*;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks JWT verification throughput using SignedJwtVerifier.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class SignedJwtVerifierBenchmark {

    @State(Scope.Benchmark)
    public static class JwtState {

        SecretKey key;
        SignedJwtVerifier verifier;
        String token;

        @Setup(Level.Trial)
        public void setup() {
            // Generate a symmetric key for HS256
            this.key = Jwts.SIG.HS256.key().build();

            Instant now = Instant.now();
            JwtBuilder builder = Jwts.builder()
                    .subject("benchmark-user")
                    .issuer("jwt-servlet-auth-benchmarks")
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(now.plus(Duration.ofHours(1))))
                    .claim("scope", List.of("read", "write", "admin"))
                    .claim("tenant", "example-tenant");

            this.token = builder
                    .signWith(this.key)
                    .compact();

            this.verifier = new SignedJwtVerifier(this.key);
        }
    }

    /**
     * Measures pure verification cost (parsing + signature verification).
     */
    @Benchmark
    public Jws<Claims> verifyJwt(JwtState state) {
        return state.verifier.verify(state.token);
    }

    /**
     * Measures the cost of creating a new parser/verifier + verifying.
     * Useful to understand "cold" per-request setup overhead.
     */
    @Benchmark
    public Jws<Claims> createVerifierAndVerify(JwtState state) {
        JwtParser parser = Jwts.parser()
                .verifyWith(state.key)
                .build();
        SignedJwtVerifier localVerifier = new SignedJwtVerifier(parser);
        return localVerifier.verify(state.token);
    }
}
