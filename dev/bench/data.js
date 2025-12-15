window.BENCHMARK_DATA = {
  "lastUpdate": 1765807816067,
  "repoUrl": "https://github.com/telicent-oss/jwt-servlet-auth",
  "entries": {
    "JWTAuthBenchmarks": [
      {
        "commit": {
          "author": {
            "name": "Paul Gallagher",
            "username": "TelicentPaul",
            "email": "132362215+TelicentPaul@users.noreply.github.com"
          },
          "committer": {
            "name": "GitHub",
            "username": "web-flow",
            "email": "noreply@github.com"
          },
          "id": "b61bed974ab07f78165b997d3f374a37c3fe4906",
          "message": "Merge pull request #103 from telicent-oss/core_1096_add_benchmarking\n\n[CORE-1096] Adding initial pass at benchmarking. Merging due to minimal impact  - also might require tweaks",
          "timestamp": "2025-12-15T12:46:45Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/b61bed974ab07f78165b997d3f374a37c3fe4906"
        },
        "date": 1765803873459,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 397299.07915540226,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 359181.6349291172,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 19343.241532460415,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 41868.14972672249,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 4954273.992812,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 112964039.00886294,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 120229.76644484163,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 124445.56107562981,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "name": "Paul Gallagher",
            "username": "TelicentPaul",
            "email": "132362215+TelicentPaul@users.noreply.github.com"
          },
          "committer": {
            "name": "GitHub",
            "username": "web-flow",
            "email": "noreply@github.com"
          },
          "id": "b61bed974ab07f78165b997d3f374a37c3fe4906",
          "message": "Merge pull request #103 from telicent-oss/core_1096_add_benchmarking\n\n[CORE-1096] Adding initial pass at benchmarking. Merging due to minimal impact  - also might require tweaks",
          "timestamp": "2025-12-15T12:46:45Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/b61bed974ab07f78165b997d3f374a37c3fe4906"
        },
        "date": 1765807815583,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 399003.87038147234,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 360030.41941280896,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 19403.197972484304,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 42156.13809717253,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 5305799.19774452,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 114121308.22718772,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 117418.38558224608,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 122886.2885638644,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          }
        ]
      }
    ]
  }
}