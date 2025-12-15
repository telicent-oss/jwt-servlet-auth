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

import io.telicent.servlet.auth.jwt.PathExclusion;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks PathExclusion matching for wildcard and exact patterns.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class PathExclusionBenchmark {

    /**
     * Shared state for the PathExclusion benchmarks.
     */
    @State(Scope.Benchmark)
    public static class PathState {

        PathExclusion wildcardApi;
        PathExclusion exactHealth;

        String apiMatchingPath;
        String apiNonMatchingPath;
        String healthMatchingPath;
        String healthNonMatchingPath;

        @Setup(Level.Trial)
        public void setup() {
            this.wildcardApi = new PathExclusion("/api/*");
            this.exactHealth = new PathExclusion("/health");

            this.apiMatchingPath = "/api/v1/resources/123";
            this.apiNonMatchingPath = "/static/css/app.css";

            this.healthMatchingPath = "/health";
            this.healthNonMatchingPath = "/healthcheck";
        }
    }

    @Benchmark
    public boolean wildcardMatch(PathState state) {
        return state.wildcardApi.matches(state.apiMatchingPath);
    }

    @Benchmark
    public boolean wildcardMiss(PathState state) {
        return state.wildcardApi.matches(state.apiNonMatchingPath);
    }

    @Benchmark
    public boolean exactMatch(PathState state) {
        return state.exactHealth.matches(state.healthMatchingPath);
    }

    @Benchmark
    public boolean exactMiss(PathState state) {
        return state.exactHealth.matches(state.healthNonMatchingPath);
    }
}
