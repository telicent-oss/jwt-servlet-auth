window.BENCHMARK_DATA = {
  "lastUpdate": 1782122353472,
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
        "date": 1771834387977,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7180.099418295341,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7333.060729073802,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 397809.64901150455,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 360520.62972177577,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 6979.401207536091,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9325.437619842796,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 112900.2012663614,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 153181.62110708718,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10840818.847515395,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 114923932.24552372,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 119463.91287268046,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 125923.96968015647,
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
          "id": "0f3e10efcb77d067b6dbe6a44ff373922089ab1f",
          "message": "Merge pull request #114 from telicent-oss/release/4.0.0\n\nComplete Release 4.0.0",
          "timestamp": "2026-02-25T10:14:59Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/0f3e10efcb77d067b6dbe6a44ff373922089ab1f"
        },
        "date": 1772439094584,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7166.081028372054,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7404.326358050213,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 399427.25715373585,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 360145.61417446955,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7238.436110010533,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9396.640040192924,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 85446.93695360291,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 153545.46073595557,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10898155.94803745,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 114653087.76491317,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 122117.02450496254,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 125729.7193754799,
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
          "id": "fc4723edd60bc52ee90239ebf42fd457675a4db2",
          "message": "Merge pull request #115 from telicent-oss/dependabot/maven/patches-b744363a4f\n\nBump org.mockito:mockito-core from 5.21.0 to 5.22.0 in the patches group",
          "timestamp": "2026-03-02T12:42:05Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/fc4723edd60bc52ee90239ebf42fd457675a4db2"
        },
        "date": 1773043885875,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7118.409654375925,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7327.746242013329,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 397642.5436381779,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 360227.6252274347,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7290.527668414649,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9275.305359048394,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 115928.72455376483,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 153686.5812261384,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10903572.699962143,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 113810567.6688973,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 119596.36168468403,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 127602.52094632485,
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
          "id": "b2774c9be8e01d75ce20a84c1e3ffa1cf0f703c1",
          "message": "Merge pull request #116 from telicent-oss/dependabot/maven/patches-31d1f003b6\n\nBump the patches group with 5 updates",
          "timestamp": "2026-03-09T11:24:14Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/b2774c9be8e01d75ce20a84c1e3ffa1cf0f703c1"
        },
        "date": 1773649274334,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7218.565089249515,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7333.430128055734,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 398044.03262343915,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 358630.67961774126,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7118.70137425361,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 8454.82821092816,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 116724.76288565548,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 153571.3147648011,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10892312.89303204,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 117675353.90014283,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 120956.2416045889,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 128818.47281104785,
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
          "id": "87a838e63235f547cb30c6315dfa8cf41711c2bb",
          "message": "Merge pull request #118 from telicent-oss/release/4.0.1\n\nComplete Release 4.0.1",
          "timestamp": "2026-03-20T11:59:37Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/87a838e63235f547cb30c6315dfa8cf41711c2bb"
        },
        "date": 1774253877137,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7254.508335237659,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7383.316785113899,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 399620.8456074751,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 359489.98588751926,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7259.724630676816,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9344.600102029915,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 113203.42135511737,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 153626.92715311874,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10747287.551945116,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 114555174.56317835,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 118962.60772035868,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 129826.30705119806,
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
          "id": "86c169d228de5e1166297b21c11076a24451e6d2",
          "message": "Merge pull request #119 from telicent-oss/dependabot/maven/patches-554c1b51cf\n\nBump the patches group with 2 updates",
          "timestamp": "2026-03-25T14:34:39Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/86c169d228de5e1166297b21c11076a24451e6d2"
        },
        "date": 1774859417633,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7158.031846436274,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7341.979288603361,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 399053.17715449526,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 359028.2243845932,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7321.14814401247,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 8687.42449821616,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 112993.82391820996,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 153053.43169610173,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10726438.541335225,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 65127736.82723136,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 119217.6176564785,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 126423.35217205851,
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
          "id": "887e3372988b0d741379888fa07ebecf7460f384",
          "message": "Merge pull request #96 from telicent-oss/openid-discovery\n\nAdds OpenID Connect configuration discovery based JWT verification",
          "timestamp": "2026-04-02T13:43:28Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/887e3372988b0d741379888fa07ebecf7460f384"
        },
        "date": 1775464241576,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7122.400416307099,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7379.373041704845,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 399274.32784547674,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 359311.18859840365,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7299.1937368948975,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9309.995134715513,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 115697.25223775984,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 146803.54243293367,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10760106.39592062,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 110998635.41269846,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 118771.20984146213,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 126941.00138150828,
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
          "id": "9f55847a827a12c136e860ae7c2984657a7dacad",
          "message": "Merge pull request #120 from telicent-oss/dependabot/maven/patches-82c4c9743c\n\nBump the patches group with 7 updates",
          "timestamp": "2026-04-07T07:21:50Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/9f55847a827a12c136e860ae7c2984657a7dacad"
        },
        "date": 1776069628208,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7172.864861658993,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7418.299488309298,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 398828.55669374,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 360276.90705771145,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7287.1198751169695,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9384.615301854554,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 116951.79330122366,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 146511.20281190332,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10945287.497302666,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 114584329.99689758,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 120960.11077211639,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 126014.972055806,
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
          "id": "9f55847a827a12c136e860ae7c2984657a7dacad",
          "message": "Merge pull request #120 from telicent-oss/dependabot/maven/patches-82c4c9743c\n\nBump the patches group with 7 updates",
          "timestamp": "2026-04-07T07:21:50Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/9f55847a827a12c136e860ae7c2984657a7dacad"
        },
        "date": 1776674623118,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 6312.153913574903,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 6576.671475115423,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 464243.8048118564,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 360110.30290224444,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 6193.88851057999,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 10266.477501566213,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 129974.08516107911,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 155351.24727067462,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 11315238.006929826,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 121873959.27598095,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 95723.90678565262,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 101233.1434325292,
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
          "id": "9f55847a827a12c136e860ae7c2984657a7dacad",
          "message": "Merge pull request #120 from telicent-oss/dependabot/maven/patches-82c4c9743c\n\nBump the patches group with 7 updates",
          "timestamp": "2026-04-07T07:21:50Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/9f55847a827a12c136e860ae7c2984657a7dacad"
        },
        "date": 1777279763486,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 6708.036902902108,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 6305.071186124853,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 465449.32758928137,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 362452.1824563004,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 6941.366761325965,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9995.977784138873,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 127700.4429145611,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 171536.1744763168,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 11828275.66942623,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 109693564.54808328,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 95717.75237698239,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 102624.67594417682,
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
          "id": "f3e719d6c472da57c64597b16b6b918fc9d78ca1",
          "message": "Merge pull request #121 from telicent-oss/dependabot/maven/patches-1929ee3a10\n\nBump commons-io:commons-io from 2.21.0 to 2.22.0 in the patches group",
          "timestamp": "2026-04-27T12:28:51Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/f3e719d6c472da57c64597b16b6b918fc9d78ca1"
        },
        "date": 1777884555490,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 9553.42087140939,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 9779.066144299948,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 474997.3454507332,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 417760.1366245084,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 8852.25594814515,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 11480.881036662642,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 134970.9650372354,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 188290.28291615372,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 13012732.758224392,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 131331190.05748701,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 148461.3852879735,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 157681.80014067373,
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
          "id": "0803d403b0c50c4f4e1fb8d0cddc8b71ec611c2a",
          "message": "Merge pull request #122 from telicent-oss/dependabot/maven/patches-7a5547909c\n\nBump the patches group with 3 updates",
          "timestamp": "2026-05-05T08:37:06Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/0803d403b0c50c4f4e1fb8d0cddc8b71ec611c2a"
        },
        "date": 1778490332154,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7215.799646851706,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 6783.059519782175,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 399069.335596332,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 359867.68679413095,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7249.922085703127,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9198.152404980949,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 117000.12245090058,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 154176.46507186245,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10905605.455943802,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 117945120.30188139,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 119946.89297029268,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 128055.61094085916,
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
          "id": "73c025c479e94f88ef13b69981f1a9cf16be5d41",
          "message": "Merge pull request #126 from telicent-oss/dependabot/maven/patches-2e8718fa48\n\nBump the patches group across 1 directory with 7 updates",
          "timestamp": "2026-05-18T08:34:23Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/73c025c479e94f88ef13b69981f1a9cf16be5d41"
        },
        "date": 1779095208402,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 9504.911027721972,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 9558.6876910576,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 476113.92219262396,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 416105.3845430386,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 8509.799780685778,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 11330.763679142758,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 134083.1205433026,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 189589.7173174119,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 12957680.110455038,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 130818251.36767355,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 148866.5524185039,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 158310.56624457243,
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
          "id": "6e8d9441019e37bb37b5fdb5cfd3f65e8afcb7af",
          "message": "Merge pull request #127 from telicent-oss/dependabot/maven/patches-92c47fc95f\n\nBump org.apache.maven.plugins:maven-enforcer-plugin from 3.6.2 to 3.6.3 in the patches group",
          "timestamp": "2026-05-19T07:08:43Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/6e8d9441019e37bb37b5fdb5cfd3f65e8afcb7af"
        },
        "date": 1779700650259,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7271.708453285741,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7478.369383757745,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 368653.651173356,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 324630.49959791277,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 6855.083249061796,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 8790.824428858472,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 104366.72582244346,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 131871.3813593436,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10098069.595870534,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 102058396.76712492,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 118185.58111315612,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 125851.71494184058,
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
          "id": "6e8d9441019e37bb37b5fdb5cfd3f65e8afcb7af",
          "message": "Merge pull request #127 from telicent-oss/dependabot/maven/patches-92c47fc95f\n\nBump org.apache.maven.plugins:maven-enforcer-plugin from 3.6.2 to 3.6.3 in the patches group",
          "timestamp": "2026-05-19T07:08:43Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/6e8d9441019e37bb37b5fdb5cfd3f65e8afcb7af"
        },
        "date": 1780307254897,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7179.866473728903,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7361.648633891613,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 397349.1408010978,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 358762.4943724772,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7330.863693136251,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9338.937722658471,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 113659.18299000799,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 154452.45808304482,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10290328.99792641,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 114476809.3218433,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 119565.06877758901,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 126904.69934002915,
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
          "id": "a0edc23be60168e4cd59380bcae90a145ba8c8ad",
          "message": "Merge pull request #128 from telicent-oss/CORE-1306\n\nFix not clearing Logging MDC of username in all cases (CORE-1306)",
          "timestamp": "2026-06-02T09:38:09Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/a0edc23be60168e4cd59380bcae90a145ba8c8ad"
        },
        "date": 1780911160884,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7251.002237864047,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7422.961160442954,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 398809.82065034006,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 359095.15095163125,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7284.605858726364,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9342.775684155102,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 117535.29576747127,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 153675.0491803947,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10844322.788323065,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 115022515.84111643,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 118356.06035814958,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 125724.69647474641,
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
          "id": "5f4df39afb9887f10e74725878328914a64305b1",
          "message": "Merge pull request #134 from telicent-oss/release/4.1.2\n\nComplete Release 4.1.2",
          "timestamp": "2026-06-10T08:19:40Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/5f4df39afb9887f10e74725878328914a64305b1"
        },
        "date": 1781517906159,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 7235.351458707684,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 7362.605083193033,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 398797.65549789055,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 358754.90198798955,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 7316.603017301959,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 9231.298197445698,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 117491.6195090218,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 145981.185492708,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 10557508.075917054,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 114655208.45898278,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 119290.74001021835,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 126087.21011967953,
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
          "id": "be4bc3c45036d259795e5fa28b713be0b50bbf6f",
          "message": "Merge pull request #136 from telicent-oss/dependabot/maven/patches-15ac8da0f3\n\nBump the patches group across 1 directory with 2 updates",
          "timestamp": "2026-06-15T12:58:24Z",
          "url": "https://github.com/telicent-oss/jwt-servlet-auth/commit/be4bc3c45036d259795e5fa28b713be0b50bbf6f"
        },
        "date": 1782122351835,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserDefault",
            "value": 9283.150239586328,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.JwtParserBuilderBenchmark.buildParserSharedDeserializer",
            "value": 9759.923221168847,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMatch",
            "value": 475399.29271961935,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.exactMiss",
            "value": 418629.73056696064,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMatch",
            "value": 8881.004413658651,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.multiWildcardMiss",
            "value": 11558.283821786961,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMatch",
            "value": 134991.20852805077,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.PathExclusionBenchmark.wildcardMiss",
            "value": 170054.49058866157,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.coldIsUserInRole",
            "value": 12966579.476247072,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.RolesHelperBenchmark.warmIsUserInRole",
            "value": 131631039.39261135,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.createVerifierAndVerify",
            "value": 147631.09246056707,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.telicent.servlet.auth.jwt.benchmarks.SignedJwtVerifierBenchmark.verifyJwt",
            "value": 158774.06250714936,
            "unit": "ops/s",
            "extra": "iterations: 5\nforks: 1\nthreads: 1"
          }
        ]
      }
    ]
  }
}