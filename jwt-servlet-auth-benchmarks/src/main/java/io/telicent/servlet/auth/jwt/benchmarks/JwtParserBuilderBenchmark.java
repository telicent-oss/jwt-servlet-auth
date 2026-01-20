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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.telicent.servlet.auth.jwt.verification.JwtParsers;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import javax.crypto.SecretKey;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks parser construction to highlight shared-deserializer gains.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class JwtParserBuilderBenchmark {

    @State(Scope.Thread)
    public static class ParserState {
        SecretKey secret;

        @Setup(Level.Trial)
        public void setup() {
            this.secret = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        }
    }

    @Benchmark
    @OperationsPerInvocation(100)
    public void buildParserDefault(ParserState state, Blackhole bh) {
        for (int i = 0; i < 100; i++) {
            bh.consume(Jwts.parser().verifyWith(state.secret).build());
        }
    }

    @Benchmark
    @OperationsPerInvocation(100)
    public void buildParserSharedDeserializer(ParserState state, Blackhole bh) {
        for (int i = 0; i < 100; i++) {
            bh.consume(JwtParsers.builder().verifyWith(state.secret).build());
        }
    }
}
