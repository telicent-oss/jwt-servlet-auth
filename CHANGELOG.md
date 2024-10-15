# CHANGE LOG

# 0.17.4

- Build improvements
    - Various build and test dependencies upgraded to latest available

# 0.17.3

- Build improvements:
    - Jackson upgraded to 2.18.0
    - Various build and test dependencies upgraded to latest available

# 0.17.2

- Build improvements:
    - Automated build pipeline now ensures that Release PRs are automatically generated
    - Various build and test dependencies upgraded to latest available

# 0.17.1

- Build improvements:
    - Apache Commons IO upgraded to 2.17.0

# 0.17.0

- Filter improvements:
    - A cache is now used to rate limit the frequency of warnings issued when a request goes to a path that has been
      excluded from authentication by configuration.  
      This prevents these warnings from dominating the logs when used on
      things like health status endpoints that are being regularly pinged by automated monitoring tools.
    - Improved documentation around filter exclusions
- Build improvements:
    - Various build and test dependencies upgraded to latest available

# 0.16.0

- Security fixes:
    - Fixes a theoretical vulnerability where HTTP response/request splitting could occur if a malicious user was either
      able to control server configuration, or the filter was deployed in a server runtime that applied insufficient
      Request URI sanitisation.
    - Fixes a potential information disclosure vulnerability where raw error messages were sent to users in the event of
      unexpected authentication errors.
- Build improvements:
    - Bumped Apache Commons Lang to 3.17.0
    - Various build and test dependencies upgraded to latest available

# 0.15.3

- Build improvements:
    - Bumped Apache Commons Lang to 3.15.0
    - Various build and test dependencies upgraded to latest available

# 0.15.2

- Build improvements:
    - Bumped Jackson to 2.17.2
    - Various build and test dependencies upgraded to latest available

# 0.15.1

- Build improvements:
    - Bumped JJWT to 0.12.6
    - Various build plugins updated to latest available

# 0.15.0

- API Improvements
    - **BREAKING** Some internal focused model classes were converted to record classes to simplify implementation but
      this has changed their field accessor method names
    - **BREAKING** Added `addRequestAttribute()` method to `JwtAuthenticationEngine`
    - Authentication engine now populates additional request attributes to allow applications to better understand how a
      user was authenticated, and to use the JWT for further work, e.g. authorization, if needed.
    - New `VerifiedToken` class used to track which token was used to authenticate the request better and in populating
      the new request attributes:
        - `io.telicent.servlet.auth.jwt.source` with the `TokenSource` from which the JWT was obtained
        - `io.telicent.servlet.auth.jwt.raw` for the raw JWT
        - `io.telicent.servlet.auth.jwt.verified` for the `Jws<Claims>` that represents the verified JWT
    - **BREAKING** Removed old constant classes that was previously renamed in the `0.13.0` release
- Build improvements:
    - Further improved test coverage across the libraries
    - Reverted to JDK 17 as the target JDK for releases to improve portability

# 0.14.0

- Initial public release to Maven Central
- Build Improvements
    - Use Nexus Staging Maven Plugin for deployment

# 0.13.0

- API Improvements
    - **BREAKING** Renamed `HttpConstants`, `LoggingConstants` and `ServletConstants` to `Jwt<X>Constants` to better
      reflect intent and usage.  Old classes are marked as `@Deprecrated` and Javadoc points to new constant names.
    - Further improved `toString()` output for `HeaderBasedJwtAuthenticationEngine`
- AWS Improvements
    - Fixed a bug where passing in a JWT that is missing the `kid` (Key ID) claim in the header to the
      `AwsElbKeyResolver` would result in an erroneous attempt to resolve the `null` key from AWS.  This now fails fast
      with an appropriate error message.
- Build Improvements
    - Integration test modules have clearer Maven Artifact IDs to clearly distinguish them from the actual
      implementation modules.

# 0.12.0

- Build improvements:
    - Now builds with JDK 21 by default

# 0.11.1

- API Improvements
    - `TokenCanidate` and `Challenge` have `toString()` methods added to aid debugging
    - `JwtAuthenticationEngine` now logs the details of the challenges being issued, not just the number of challenges

# 0.11.0

- API Improvements
    - `HeaderSource.toString()` more accurately reflects the configured Header and Prefix 
- Configuration
    - `AutomatedConfiguration.configure()` skips configuring items it detects have already been configured in your 
      provided configured adaptor
        - **BREAKING** New `jwt.configs.allow-multiple` parameter **MUST** be set to `true` if you wish to provide
          multiple distinct filter configurations in your application.  This helps prevent accidentally applying
          different levels of JWT Authentication to different parts of your application unless you know you want this.
    - `DefaultVerificationProvider` and `AwsVerificationProvider` now log the details of the configured verifier

# 0.10.2

- API Improvements
   - Further `toString()` standardisation

# 0.10.1

- API Improvements
   - `toString()` for SignedJwtVerifier is appropriately populated in more cases

# 0.10.0

- API Improvements
    - Added `toString()` overrides to various classes to aid in debugging authentication setup
    - Removed deprecated `JwksKeyResolver` and `CachedJwkProvider` classes that were previously marked for removal
- Upgraded various dependencies to latest available, notable changes:
    - Jackson upgraded to 2.17.1
    - Removed deprecated `jwks-rsa` library from dependencies as no longer used

# 0.9.0

- Configuration
    - `VerificationProvider` and `EngineProvider` now provide a `priority()` method whose return value is used to
      control the order in which providers are tried by their corresponding factories.  This provides for predictable
      behaviour if multiple providers are present in a runtime environment.  For example a user implementing a custom
      provider can now guarantee it will be used in favour of any of the default providers by declaring their priority
      appropriately.
    - Filters now freeze their configuration after they first receive it.  This prevents filter configuration being
      modified for a running server and allows (in some runtime environments) for multiple instances of the filter to 
      be configured with different configurations
- Upgraded various dependencies to latest available, notable changes
    - Removed Apache Commons Codec in favour of using JDK Base 64 support
    - Bumped Apache Commons IO to 2.16.1
    - Bumped Jackson to 2.17.0
    - Bumped Jakarta Annotations to 3.0.0
    - Bumped SLF4J to 2.0.13

# 0.8.0

- API Cleanup
    - Adjusted some of our APIs to align with changes in the `jjwt` API that are being made in preparation for their 1.0
      release, notably:
          - Our `SigningKeyAdaptor`'s were changed to implement `LocatorAdaptor<Key>` instead
          - Tests were adapted to new ways of generating keys for testing
    - New `AbstractConfigurableJwtAuthFilter` makes it easier to implement new filters by abstracting common
      configuration and filtering logic into a base class
- Improved JWKS Support
    - `jjwt` offers full JWKS support from `0.12.0` onwards so we have now adopted that in favour of the `jwks-rsa`
      library which only offered limited JWKS support
        - New `UrlJwksKeyLocator` based on `jjwt`'s JWKS support
        - New `CachedJwksKeyLocator` that uses the Caffeine caching library to provide an in-memory cache of previously
          loaded keys to avoid putting unnecessary request load on the JWKS URL
        - The existing `JwksKeyResolver` and `CachedJwkProvider` implementations are marked as `@deprecated` and will be
          removed in a future release
- Automatic Configuration
    - Added support for automatically configuring the `JwtAuthFilter` for Servlet 3.x and 5.x using filter init
      parameters
    - Added a `JaxRs3AutomatedAuthConfigurationListener` for JAX-RS 3 that automatically configures the `JwtAuthFilter`
      via a `ServletContextListener` and context init params
    - `AutomaticConfiguration.configure()` entrypoint can be used to invoke automatic configuration (used by the above
      implementations internally)
    - New `VerificationProvider` interface to allow hooking in new automatic verifier configurations via `ServiceLoader`
        - `DefaultVerificationProvider` allows configuring a `SignedJwtVerifier` from a public/secret key or JWKS URL
        - `AwsVerificationProvider` allows configuring an `AwsElbJwtVerifier` from an AWS region
    - New `EngineProvider` interface to allow hooking in new automatic authentication engine configurations via
      `ServiceLoader`
         - Each implementation module provides an appropriate engine provider for their runtime environment
- Integration Tests
    - Added new integration test modules that show how to actually configure and use the filters in example toy web
      applications
- Upgraded various dependencies to latest available, notable changes
    - Bumped `jjwt` to 0.12.4
    - Bumped SLF4J to 2.0.11

# 0.7.0

- Upgraded various build plugins to latest available
    - Downgraded `maven-source-plugin` to 3.2.1 to avoid a release related bug
- Upgraded various dependencies to latest available, notable changes:
    - Bumped `jwks-rsa` to 0.22.1
    - Bumped Apache Commons Codec to 1.16.0
    - Bumped Apache Commons IO to 2.15.1
    - Bumped Apache Commons Lang to 3.14.0
    - Bumped SLF4J to 2.0.10

# 0.6.0

- Upgrade `jjwt` to 0.12.3, this entails some internal code changes to adapt to their updated APIs

# 0.5.1

- Fix bug where if one of the configured username claims contained an empty/blank string it was incorrectly returned as
  the username instead of trying further claims

# 0.5.0

- All provided authentication engines can now support configuring multiple claims which may provide the username

# 0.4.2

- Bump various dependencies to latest available
- Adds explicit NOTICE file

# 0.4.1

- Bump various build dependencies to latest available
- Bump `jwks-rsa` dependency to `0.22.0`
- Applied more consistent licensing across the code base

# 0.4.0

- Allow a request to contain multiple candidate JWTs, e.g. from multiple HTTP Headers, and consider all candidates
  before making a final success/failure decision for authentication.  This required a breaking change to some of the
  internal APIs for `JwtAuthenticationEngine` that derived code may need to be adapted for.

# 0.3.0

- Allow optionally excluding some paths from JWT authentication requirement 

# 0.2.0

- Inject authenticated username into SLF4J MDC (Mapped Diagnostic Context) so logging can potentially include
  username with each line of logging
- Refactoring to share more code between implementations wherever possible
- Split into separate modules so each concrete implementation for a given servlet container runtime is a separate
  module with only the code and dependencies it needs:
    - `jwt-servlet-auth-jaxrs3`
    - `jwt-servlet-auth-servlet3`
    - `jwt-servlet-auth-servlet5`
- Improved test coverage and removed redundant code
- Added support for AWS ELB Key resolution and AWS specific function via new module `jwt-servlet-auth-aws`

# 0.1.0

- Initial release
- `JwtAuthenticationEngine` abstract class and concrete implementations for `javax.servlet`, `jakarta.servlet` and
  `jakarta.ws`
- `JwtVerifier` interface with default `SignedJwtVerifier` implementation
- `JwksKeyResolver` for resolving JWKS
