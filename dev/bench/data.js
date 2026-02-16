window.BENCHMARK_DATA = {
  "lastUpdate": 1771229553797,
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
        "date": 1766390682686,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 399342.0973417772,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 358880.6748089668,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 19412.771187231443,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 42217.82883054168,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 5408230.939746219,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 118087603.63750264,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 120573.93372875774,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 124632.32768457406,
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
        "date": 1766995502285,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 439548.50498897117,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 353512.99650911696,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 21448.790983906893,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 47872.44886166038,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 5504503.332229239,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 121838545.89089993,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 96551.5764861063,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 99896.61817843851,
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
        "date": 1767600296160,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 398998.2022070257,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 359811.47499382315,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 19376.83194984157,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 42207.007108196936,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 5356246.269521133,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 114740447.58152243,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 118160.8870244736,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 124562.61393679823,
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
        "date": 1768205089444,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 399017.66848992585,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 357983.75170379586,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 17831.151648252668,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 39740.73059311925,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 5268859.265402451,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 117524611.8160491,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 119642.40265055982,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 125024.36840678586,
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
          "id": "e1bb692150878ebd2e55a94644c08d0cc69d6d45",
          "message": "Merge pull request #106 from telicent-oss/minor/add_test_for_unsecured_jwt\n\n[Minor] Extending test coverage",
          "timestamp": "2026-01-12T17:13:21Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/e1bb692150878ebd2e55a94644c08d0cc69d6d45"
        },
        "date": 1768809905994,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 398600.7931447826,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 358515.86254757526,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 19359.62833062468,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 41823.33045982163,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 5347473.459493923,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 114928549.53666893,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 116760.18202318223,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 122622.43931899325,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "name": "Rob Vesse",
            "username": "rvesse",
            "email": "rob.vesse@telicent.io"
          },
          "committer": {
            "name": "GitHub",
            "username": "web-flow",
            "email": "noreply@github.com"
          },
          "id": "94d23e74169ac111085af81d0aa72608279bb4d2",
          "message": "Merge pull request #108 from telicent-oss/release/3.0.0\n\nComplete Release 3.0.0",
          "timestamp": "2026-01-20T13:29:58Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/94d23e74169ac111085af81d0aa72608279bb4d2"
        },
        "date": 1769414706839,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7105.533466632613,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7420.7180641911455,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 399255.48116754985,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 360307.48762937484,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7276.679107088383,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9376.428680769948,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 117546.46255710821,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 153709.51042788656,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10723360.634971488,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 114026807.51251133,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 119728.51681084337,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 125101.19350660667,
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
          "id": "5638636b28743ae291333867862d5543b0b1e9ce",
          "message": "Merge pull request #109 from telicent-oss/dependabot/maven/patches-77c0824362\n\nBump org.testng:testng from 7.11.0 to 7.12.0 in the patches group",
          "timestamp": "2026-01-26T11:57:35Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/5638636b28743ae291333867862d5543b0b1e9ce"
        },
        "date": 1770019912144,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7276.304419289278,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7364.141608777238,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 397890.46391421196,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 360661.80702649505,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7300.463049567222,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9246.090418729349,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 117395.75622637112,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 153785.63290159387,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10922653.072549468,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 114585066.87170401,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 120914.29385542788,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 128408.47459477345,
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
          "id": "45f59eb386aaec1c44e95c9a76c3a81c8091f3b4",
          "message": "Merge pull request #110 from telicent-oss/dependabot/maven/patches-c7c33a07b1\n\nBump the patches group with 6 updates",
          "timestamp": "2026-02-02T12:05:28Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/45f59eb386aaec1c44e95c9a76c3a81c8091f3b4"
        },
        "date": 1770624843567,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7244.524187673817,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7414.714291102445,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 399044.56739479676,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 360536.033962166,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7356.466304403482,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9401.972531981804,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 116948.1005472731,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 153399.75882012426,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10935949.116688143,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 117447759.55192623,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 121881.04955759834,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 127469.34408326098,
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
          "id": "4d1ce3e9fcfec3d2fc40a4ef893a9c7aaa84b81a",
          "message": "Merge pull request #111 from telicent-oss/dependabot/maven/patches-10755dabf8\n\nBump org.apache.maven.plugins:maven-dependency-plugin from 3.9.0 to 3.10.0 in the patches group",
          "timestamp": "2026-02-09T12:15:45Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/4d1ce3e9fcfec3d2fc40a4ef893a9c7aaa84b81a"
        },
        "date": 1771229553467,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7320.283882753461,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7470.035996579842,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 368108.99368829606,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 325142.0613231479,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 6859.568707064408,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 8850.881972644795,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 104722.8898330064,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 145942.71450224094,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10055276.623287212,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 101766720.87855712,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 119096.42448562747,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 125806.1225981119,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          }
        ]
      }
    ]
  }
}