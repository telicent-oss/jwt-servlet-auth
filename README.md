# JSON Web Token (JWT) Servlet Authentication

This repository provides libraries that allow adding [JSON Web Token (JWT)][Rfc7519] based [Bearer authentication][Rfc6750] into
Java Servlet applications. Support is provided for both Servlet 3 (`javax.servlet`), Servlet 5 (`jakarta. servlet`) and
JAX-RS 3 (`jakarta.ws.rs`) based applications.

While some servlet containers provide integrated support for this, others don't, and even where support is provided the
flexibility of OAuth 2 (the specification that defined Bearer auth) can still leave implementations lacking.

JWT based Bearer Authentication, while very simple at a high level, actually proves to be quite difficult in practise
because tokens can be issued by a huge variety of issuers using different signature algorithms, key management
techniques etc. To try and address this the library provides two main abstractions:

- `JwtAuthenticationEngine` - This is an abstract base class that implements the authentication flow with abstract
  methods provided that perform configurable portions of the flow e.g. selecting the HTTP Header(s) that convey the
  JWTs, finding the username from the verified token and transforming the request with the authenticated user identity.
- `JwtVerifier` - This is an interface for verifying tokens.

The aim being to decouple the flow of obtaining a token from verifying it, allowing the core engine to be easily
repurposed for multiple servlet container runtime versions.

Concrete implementations of both are provided, see [Usage](#usage) for example usage.

# Build

These libraries are built with [Apache Maven][maven]:

```bash
$ mvn clean install
```

Building requires JDK 17+ and Apache Maven 3.8.1+

## Dependencies

This library depends on the excellent [`jjwt`][jjwt] libraries. 

The individual implementation modules have Servlet 3, Servlet 5 and JAX-RS 3 declared as `provided` dependencies,
meaning you can safely put this in a Java Servlet application based on whichever servlet container runtime version you
want as long as the correct dependencies are provided at runtime.  If you are using some other runtime version then you
should be able drop in the most appropriate implementation module.  For example a Servlet 6 application would use the
Servlet 5 implementation module.

Logging is done via SLF4J, no concrete provider is used so a suitable SLF4J binding should be provided at runtime.

We use the `jjwt-jackson` module meaning that [Jackson][Jackson] is used as the JSON processing library.  As `jjwt`
provides backwards compatibility for older JDKs it is pinned to old Jackson versions which can lead to runtime
dependency conflicts if mixed with newer Jackson versions.  To avoid this from `0.8.0` we explicitly declare our own
dependencies on the latest Jackson release which overrides the `jjwt` declarations as in Maven the closest dependency
wins.

# Depending on this Library

To depend on this library add the following to your Maven dependencies:

```xml
<dependency>
    <groupId>io.telicent.public</groupId>
    <artifactId>jwt-servlet-auth-IMPLEMENTATION</artifactId>
    <version>X.Y.Z</version>
</dependency>
```

Where `IMPLEMENTATION` is the desired implementation module for your target servlet container runtime and `X.Y.Z` is the
desired version. The current stable version is `2.0.2`, development snapshots are `3.0.0-SNAPSHOT` (breaking changes;
see `CHANGELOG.md` before upgrading).

The following implementation modules are currently provided:

- `jwt-servlet-auth-servlet3` - Implementations for Servlet 3/4 i.e. `javax.servlet` applications.
- `jwt-servlet-auth-servlet5` - Implementations for Servlet 5/6 i.e. `jakarta.servlet` applications.
- `jwt-servlet-auth-jaxrs3` - Implementations for JAX-RS 3 i.e. `jakarta.ws.rs` applications.

# Usage

To use this in a servlet application you need to add an appropriate filter implementation to your application. The
filter is called `JwtAuthFilter` in all cases and should be taken from the appropriate implementation module package:

- `io.telicent.servlet.auth.jwt.jaxrs3.JwtAuthFilter` for JAX-RS 3 i.e. `jakarta.ws.rs` applications.
- `io.telicent.servlet.auth.jwt.servlet3.JwtAuthFilter` for Servlet 3 i.e. `javax.servlet` applications.
- `io.telicent.servlet.auth.jwt.servlet5.JwtAuthFilter` for Servlet 5 i.e. `jakarta.servlet` applications.

The filters do not require any direct configuration, they dynamically read the necessary `JwtAuthenticationEngine` and
`JwtVerifier` on each request. These are configured via Servlet Context attributes which you can inject into your
application however you see fit, e.g. via a `ServletContextListener`, and the injected configuration will be discovered
the first time your filter is invoked.  Note that as of `0.9.0` once a filter has been invoked for the first time its
configuration is fixed and you cannot modify it without restarting the server.

The relevant attributes are as follows:

- `io.telicent.servlet.auth.jwt.engine` - Specifies a `JwtAuthenticationEngine` implementation to use. If not specified
  then an appropriate default implementation for your runtime environment is used.
- `io.telicent.servlet.auth.jwt.verifier` - Specifies a `JwtVerifier` implementation to use, **MUST** be provided or
  **all** requests will be rejected as unauthenticated.

If the filter is not properly configured then it will throw an `AuthenticationConfigurationError`, you may wish to
configure your servlet containers custom error handling to handle these errors.

## Filter Auto-Configuration

From `0.8.0` we introduced the ability to automatically configure some aspects of behaviour via filter init parameters.
For example consider the following `web.xml` snippet for a web application intended for deployment in a Servlet 3.x
compatible runtime:

```xml
<filter>
    <filter-name>JWTAuth</filter-name>
    <filter-class>io.telicent.servlet.auth.jwt.servlet3.JwtAuthFilter</filter-class>
    <init-param>
        <param-name>jwt.secret.key</param-name>
        <param-value>test.key</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>JWTAuth</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

The automatic configuration of filters is driven by the `VerificationFactory` which is `ServiceLoader` based and looks
for `VerificationProvider` instances defined in the appropriate `META-INF/services` file(s) within your applications
classpath.  Out of the box there are two implementations provided:

- The `DefaultVerificationProvider` which can configure a `SignedJwtVerifier` from a public/secret key, or a JWKS URL.
  This requires one of the `jwt.secret.key`, `jwt.public.key` or `jwt.jwks.url` parameters to be present.  If using a
  public key then `jwt.key.algorithm` must also be present specifying the key algorithm used e.g. `RSA` or `EC`.
- The `AwsVerificationProvider` which can configure a `AwsElbJwtVerifier` from an AWS region, only available when the
  extra [AWS](#aws-integration) module is on your Classpath.  This requires the `jwt.aws.region` parameter to be
  present to specify the AWS region your application is deployed in.

Note that if using the `jaxrs3` implementation module then you cannot configure via filter init params because no such
concept exists for JAX-RS filters.  Instead you can configure via a `ServletContextListener` like so:

```xml
<context-param>
    <param-name>jwt.public.key</param-name>
    <param-value>public.key</param-value>
</context-param>
<context-param>
    <param-name>jwt.key.algorithm</param-name>
    <param-value>RSA</param-value>
</context-param>
<listener>
    <listener-class>io.telicent.servlet.auth.jwt.jaxrs3.JaxRs3AutomatedAuthConfigurationListener</listener-class>
</listener>
```

In the above example the filter will be configured to verify keys using the given `RSA` public key.

Internally when using the automated configuration it goes via the `AutomatedConfiguration.configure()` method so if you
want to inject configuration some other way, but still utilise the automated configuration mechanism this library
provides, then you can do so.

**NB** One limitation of the JAX-RS filter is that because we can only configure it indirectly it's not possible to
configure multiple filters with different configurations for different parts of your application as we can do with the
Servlet 3.x/5.x filters via their filter init parameters.  If you need different filter configurations for a JAX-RS
application consider using the Servlet filters instead of the JAX-RS filter.

### Using Multiple Configurations

**Warning** As of `0.11.0` we limited support for multiple filter configurations to avoid users accidentally configuring
less security than they intended for some parts of their application.  If you want to allow for multiple configurations
in your application you must now add `jwt.configs.allow-multiple` with a value of `true` to your configuration
parameters.

## Path Exclusions

From `0.3.0` onwards you can optionally configure Path exclusions, these allow some paths to bypass the filters, which
can be useful for special paths such as z-pages e.g. `/healthz` where you want users to be able to make requests without
authentication.  No path exclusions are configured by default so all paths to which your filter applies require
authentication out of the box.

You can configure exclusions by setting the `io.telicent.servlet.auth.jwt.path-exclusions` Servlet Context attribute,
this attribute should have a value that is a `List<PathExclusion>`.  A `PathExclusion` can be either a fixed path, e.g.
`/healthz`, that matches a single path or can be a path expression with wildcards, e.g. `/status/*`, that matches many
paths.  You can use the static `PathExclusion.parsePathPatterns()` convenience method to generate this list from a comma
separated string e.g. `/healthz,/status/*`.

A path expression uses the `*` character as a wildcard to match zero or more characters, so in the above example
`/status/*`, would match `/status/`, `/status/health`, `/status/uptime` etc.  To prevent users unintentionally disabling
authentication via overly broad exclusions any path expression that consists of only `/`, `*` and whitespace will be
rejected.  So you cannot have an exclusion of `/*` as that effectively renders applying the filter pointless.

When the wildcard character - `*` - is used the path is interpreted as a regular expression with the `*` replaced with
`.*`.  When wildcards are used be careful that any other characters in the path expression which have special
interpretation in Java regular expressions are appropriately escaped in your input expression.  For example
`/$/status/*` would not work as an exclusion since `$` has special meaning as end of line anchor.  Instead the path
expression would need to be `/\$/status/*` so that the `$` character is matched literally.  Since wildcard characters
are interpreted as `.*` in the regular expressions this means they are greedy, so the example given here would exclude
requests to both `/$/status/health` and `/$/status/components/1`.  If you don't want this greedy behaviour then you
**MUST** instead enumerate each path you want to exclude.

Please also note that if you're setting the exclusions programmatically in code you will need to escape the backslash
escape character in order for it to be a valid Java string constant e.g.

```java
new PathExclusion("/\\$/status/*");
```

Every time an excluded path is requested the filter will log a warning indicating that this is happened, this helps
developers and administrators spot cases where the exclusions may have been overly broad.  See [Path Exclusion
Warnings](#path-exclusion-warnings) for more details.

### Limiting use of Path Exclusions

**IMPORTANT:** Depending on your servlet runtime it may be better to use the filter mapping capabilities of the runtime
to only apply the filter to the paths you want to protect rather than excluding paths you don't want protected.  Whether
this is a viable option depends on your runtime and the complexity of paths involved in your application.

For example if you are excluding something like `/status/*` then you **MAY** be better to simply not apply the
authentication filter to `/status/*` and apply it to the actual paths you want to protect.  Again, depending on how
complex the paths are in your application this may/may not be viable.

### Automatic path exclusion configuration

From `0.8.0` onwards we offer [automatic configuration](#filter-auto-configuration) of filters.  If the
`jwt.path-exclusions` parameter is supplied as an init parameter then the path exclusions will be automatically
configured.  This parameter expects a comma separated list of exclusion patterns e.g. `/healthz,/status/*` e.g.

```xml
<filter>
    <filter-name>JWTAuth</filter-name>
    <filter-class>io.telicent.servlet.auth.jwt.servlet3.JwtAuthFilter</filter-class>
    <init-param>
        <param-name>jwt.secret.key</param-name>
        <param-value>test.key</param-value>
    </init-param>
    <init-param>
        <param-name>jwt.path-exclusions</param-name>
        <param-value>/healthz,/status/*</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>JWTAuth</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

### Path Exclusion Warnings

Whenever a request path matches an exclusion the filter will issue a warning like the following:

> 13:28:18 WARN  AbstractJwtAuthFilter :: Request to path /$/ping is excluded from JWT Authentication filtering by filter configuration

This is done by design to make it easy to spot misconfigured filters where the exclusions are overly broad.

However, if exclusions are used for paths that are being hit regularly, e.g. a `/healthz` endpoint associated with
automated health checking of your service, these warnings can become very chatty and drown out more useful logging from
your service.  As of `0.17.0` we have added a warnings cache that is used to rate limit how often these warnings are
seen, provided you have not excluded too many paths from authentication you should now only see one warning per unique
excluded path per 15 minutes.

Note that if you have too many excluded paths, or are using a wildcard exclusion like `/status/*` that may match many
unique paths, then you will see warnings more frequently as the cache entires are evicted by new exclusion warnings
being issued.  If you are seeing this happen then please consider the earlier advice on [Limiting Path
Exclusions](#limiting-use-of-path-exclusions).


## Engines

The `JwtAuthenticationEngine` has a single public method `authenticate(request, response, verifier)` that takes in the
request, response and [Verifier](#verifiers) objects. It returns an authenticated request on success, and `null` on
failure. In the event of failure it calls its protected `sendChallenge()` or `sendError()` methods that derived
implementations use to convey an authentication challenge or other HTTP error to the client.

The implementation modules provides several concrete implementations of `JwtAuthenticationEngine`:

- `JaxRs3JwtAuthenticationEngine`
- `Servlet3JwtAuthenticationEngine`
- `Servlet5JwtAuthenticationEngine`

All of these derive from the same base class in the core module and share the following default behaviours:

1. They assume that the standard HTTP `Authorization` header is used to convey the bearer token in the format
   `Authorization: Bearer <token>`. If multiple of these headers are present then all the presented tokens are tried in
   turn.
2. That the challenge `realm` conveyed in `WWW-Authenticate` headers on rejected requests will be taken from the Request
   URI.
3. That the username for a user is contained in the standard `sub` claim of the JWT body.

Upon successful authentication the returned `HttpServletRequest` or `ContainerRequestContext` (depending on which server
runtime you're using) is a wrapper around the original request with the appropriate methods wrapped to return the
authenticated user identity.  Additionally the SLF4J logging `MDC` will also have a `JwtUser`
(`LoggingConstants.JWT_USER`) attribute set indicating the username of the authenticated user, this allows for logging
configurations that include the username in the output so for multi-user applications you can attribute log lines to the
requests of specific users.

On authentication failure challenges and errors are conveyed by setting the relevant HTTP Status Codes and Headers on
the response object. For example you might get a 401 Response with a `WWW-Authenticate: Bearer realm="your-realm",
error="invalid_token", error_description="Your token has expired"` header. If you want to do a true OAuth 2 flow where
you instead redirect to the authentication server then you would need to sub-class the appropriate implementation and
override the `sendChallenge()` implementation.

A constructor is provided for all implementations that allows overriding parts of the default behaviours, for example:

```java
JwtAuthenticationEngine<?,?> engine = 
        new Servlet3JwtAuthenticationEngine(
            List.of(new HeaderSource("X-Custom-Header", null)),
            "my-secure-domain.com",
            List.of(ClaimPath.topLevel("preferred_username")), 
            ClaimPath.of("details", "roles"));
```

In the above example the engine is configured to obtain JWTs from `X-Custom-Header` headers (with no header prefix
expected), challenge with a `realm` of `my-secure-domain.com` and to read the username from the top level
`preferred_username` claim of the JWT, and extract roles from the `roles` claim which is nested under the `details`
claim of the JWT.

As of `0.4.0` all the concrete engine implementations allow for configuring multiple HTTP Headers in which the JWT may
be provided.  These are used in the preference order provided, and the first valid token that contains a valid username
will be considered as the authenticated user identity, even if multiple valid tokens are provided.

As of `0.5.0` all the concrete engine implementations allow for configuring multiple claims within the JWT from which
the username may be read.  These are used in the preference order provided, and always falls back to reading the
standard `sub` (subject) claim if none of those contain a non-empty string value.

From `2.0.0` we introduced the ability to extract user roles from the JWT, this version also introduced the use of the
`ClaimPath` record class to represent a path to a claim for username claims as well.  Prior to `2.0.0` only top level
claims could be used to provide the username, from `2.0.0` onwards they are configured via `ClaimPath` instances
allowing for nested username claims to be used.

If you want to customise the authentication flow more then you can do so by deriving from the base
`JwtAuthenticationEngine`, or one of its derived classes, yourself. Note that the basic flow logic is intentionally
fixed in order to keep things as secure as possible and can only be modified in limited ways.

### Engine Automatic Configuration

From `0.8.0` we support automated configuration of engines, this is done by supplying appropriate `init-param` values to
the filter when configuring it e.g.

```xml
<filter>
    <filter-name>JWTAuth</filter-name>
    <filter-class>io.telicent.servlet.auth.jwt.servlet3.JwtAuthFilter</filter-class>
    <init-param>
        <param-name>jwt.secret.key</param-name>
        <param-value>test.key</param-value>
    </init-param>
    <init-param>
        <param-name>jwt.headers.names</param-name>
        <param-value>X-API-Key,Authorization</param-value>
    </init-param>
    <init-param>
        <param-name>jwt.headers.prefixes</param-name>
        <param-value>,Bearer</param-value>
    </init-param>
    <init-param>
        <param-name>jwt.username.claims</param-name>
        <param-value>email</param-value>
    </init-param>
    <init-param>
        <param-name>jwt.roles.claim</param-name>
        <param-value>details.roles</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>JWTAuth</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

Here we configure the engine to expect the JWT to be supplied in either the `X-API-Key` or `Authorization` headers. When
supplied via `X-API-Key` we expect the JWT to be provided as-is in the header value, and when supplied via
`Authorization` we expected it to be provided as `Bearer JWT`.  We also configure the engine to extract the username
from the `email` claim of the JWT, and the roles from the `roles` claim nested under the `details` claim of the JWT.

### Roles Extraction

From `2.0.0` onwards all provided engines support the new roles extraction feature.  When suitably configured via the
`jwt.roles.claim` parameter then the authenticated request will use the information from that claim within the JWT to
answer `isUserInRole(String)` calls.  Roles information in the JWT is supported in a number of formats:

- A simple string e.g. `user`
- A string containing a comma separate list of roles e.g. `user,admin`
- A Java collection listing the roles, this can be any collection where the values can be converted to a Java `String`
- A string array containing the list of roles

In all cases blank/empty roles are ignored, and any duplicate roles are ignored since roles information is stored as a
`Set<String>` internally.

Note that this logic is all encapsulated in the `RolesHelper` class.  Therefore, if your JWT provider sends roles
information in some other format then you can extend this class and override the `loadRoles()` method as needed.  If you
do this you will also need to extend your engine implementation to change how it prepares the authenticated request in
order to inject your customised `RolesHelper`, please file an issue if you need help with this.

## Verifiers

A `SignedJwtVerifier` is provided as the default `JwtVerifier` implementation, this requires that you construct an
appropriate `JwtParser` instance (from the [JJWT][jjwt] library) for your verification setup e.g.

```java
JwtVerifier verifier =
        new SignedJwtVerifier(Jwts.parserBuilder()
        .verifyWith(somePublicKey));
```

Using the [JJWT][jjwt] library allows you to configure a `JwtParser` in a wide variety of ways including requiring the JWT
contain certain claims, allowable clock skew, signing key etc.

### JWKS Verification

If you want to use [JSON Web Key Sets (JWKS)][Rfc7517] for verifying your JWTs then you can use the `UrlJwksKeyLocator` for
your `Locator<Key>` when constructing the `JwtParser`.  We also provide a `CachedJwksKeyLocator`, which we recommend for
all production usage, as this only loads the underlying JWKS URL periodically when the cache entries expire.

```java
// Create a JWKS based key locator that caches keys for 15 minutes
Locator<Key> jwks = 
    new CachedJwksKeyLocator(yourJwksUrl, Duration.ofMinutes(15));
JwtVerifier verifier = 
  new SignedJwtVerifier(Jwts.parserBuilder().keyLocator(jwks));
```

The JWKS URL **MUST** be either a `http`/`https` URL to identify a URL where the JWKS can be downloaded from, or a
`file` URL to identify a JWKS file on the local filesystem.

### Customising Verification

You can of course provide a completely custom `JwtVerifier` implementation if you so wish.  However, if providing a
custom implementation care **MUST** be taken that the verifier does not relax verification to the point where invalid
tokens are acceptable.

You will see some example custom implementations within our test suite **BUT** these are intended purely for testing and
**SHOULD** not be used as the basis for a secure verifier implementation.

## AWS Integration

As noted earlier part of the difficulty with Bearer auth is that it gets implemented in a variety of ways, often not
entirely following the OAuth 2 standard.  For example AWS ELB supports an OAuth2 flow via Cognito for user
authentication but then attaches the Bearer tokens to traffic flowing through your ELB using [custom Amazon headers][AwsElbAuth].
It also exposes the public keys needed to verify these tokens in a non-standard way, i.e. it doesn't use [JWKS][Rfc7517],
rather it simply serves the public keys in PEM format from a well known URL.

To help with AWS integration an additional `jwt-servlet-auth-aws` module is provided with AWS specific implementations
of our abstractions.  The `AwsElbJwtVerifier` is a `JwtVerifier` that is able to verify tokens signed by the ELB, it
needs only to know the AWS region in which your application is deployed:

```java
JwtVerifier verifier = new AwsElbJwtVerifier("eu-west-1");
```

There is also an associated `AwsElbKeyResolver` which implements the [JJWT][jjwt] `Locator<Key>` interface meaning you
can use it to directly construct a custom `JwtParser` if you need to.

Finally `AwsConstants` provides useful constants such as the custom AWS Header Names that AWS ELB uses.  In order to
successfully authenticate users you will also need to configure the [engine](#engines) appropriately with the custom
header sources.

### Automatic AWS Configuration

As noted in the earlier [Automated Configuration](#filter-auto-configuration) section from `0.8.0` we now support
automatic verifier configuration.  If the `jwt.aws.region` parameter is supplied with an AWS region then the
`AwsElbJwtVerifier` will be automatically configured e.g.

```xml
<filter>
    <filter-name>JWTAuth</filter-name>
    <filter-class>io.telicent.servlet.auth.jwt.servlet5.JwtAuthFilter</filter-class>
    <init-param>
        <param-name>jwt.aws.region</param-name>
        <param-value>eu-west-2</param-value>
    </init-param>
    <init-param>
        <param-name>jwt.headers.names</param-name>
        <param-value>X-Amzn-Oidc-Data</param-value>
    </init-param>
    <init-param>
        <param-name>jwt.username.claims</param-name>
        <param-value>email</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>JWTAuth</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

Here we configure the AWS verifier to use keys from the `eu-west-2` region, find the AWS ELB injected JWT in the
`X-Amzn-Oidc-Data` header and extract the username from the `email` claim.

## Configuration Reference

The following table lists all the configuration parameters currently supported by this library, please refer to earlier
sections on how/where to specify these.  Bear in mind that if this library is being used indirectly, e.g. as part of a
higher level framework, then the configuration mechanisms and/or parameter names may differ from those outlined here,
and frameworks may choose to intentionally disable some parameters!

| Parameter                    | Default | Description                                            | Supported Versions |
|------------------------------|---------|--------------------------------------------------------|--------------------|
| `jwt.configs.allow-multiple` | `false` | Sets whether multiple filter configurations are permitted within a single application, see [multiple configurations](#using-multiple-configurations). | `0.11.0` onwards |
| `jwt.headers.use-defaults`   | `false` | Sets whether you want to use the default header sources to find presented JWTs.  When `true` this looks for the `Authorization: Bearer <jwt>` header.  Additional header sources may also be configured via `jwt.headers.names` and `jwt.headers.prefixes`. | `0.8.0` onwards |
| `jwt.headers.names`          | N/A     | A comma separated list of HTTP Headers to search for JWTs e.g. `Authorization,X-API-Key`. | `0.8.0` onwards |
| `jwt.headers.prefixes`       | N/A     | A comma separated list of the prefixes to remove from the configured HTTP headers in order to find the JWT - e.g. `Bearer,` - where an empty entry signifies that the corresponding header is expected to contain only the JWT. | `0.8.0` onwards |
| `jwt.username.claims`        | N/A     | A comma separated list of the claims within the JWT to use to find the username, e.g. `preferred_username,email`.  These are treated as an order of preference.  Will always fall back to the JWT standard `sub` (subject) claim if none of the configured claims is present.  <br />From `2.0.0` onwards claims in the list may contain `.` characters to represent a nested path to a claim, e.g. `details.name,email`. | `0.8.0` onwards |
| `jwt.roles.claim`            | N/A     | A path to the roles claim, e.g. `roles`.  You may use `.` characters to represent a nested path to the claim, e.g. `details.roles`. | `2.0.0` onwards |
| `jwt.realm`                  | N/A     | The realm to use in HTTP 401 Challenges. |
| `jwt.path-exclusions`        | N/A     | A comma separated list of [path exclusions](#path-exclusions) to which authentication doesn't apply. | `0.8.0` onwards |
| `jwt.secret.key`             | N/A     | A file path to a shared secret key used for JWT verification. | `0.8.0` onwards |
| `jwt.public.key`             | N/A     | A file path to a public key used for JWT verification. | `0.8.0` onwards |
| `jwt.key.algorithm`          | N/A     | The algorithm for the secret/public key, generally one of `RSA` or `EC`. |
| `jwt.jwks.url`               | N/A     | A file path or URL from which a [JSON Web Key Set (JWKS)](#jwks-verification) can be obtained for JWT verification. | `0.8.0` onwards |
| `jwt.aws.region`             | N/A     | An AWS region, e.g. `eu-west-1`, that matches the AWS region your application is deployed in and uses [AWS ELB Verification](#aws-integration) | `0.8.0` onwards |
| `jwt.jwks.cache.minutes`     | `60`    | How long in minutes to cache retrieved [JWKS](#jwks-verification) for.  Note that if an unknown Key ID is encountered then the cache is always bypassed and the JWKS retrieved again. | `0.8.0` onwards |
| `jwt.allowed.clock.skew`     | N/A     | How long in seconds of clock skew to permit when evaluating validity period for JWT. | `0.8.0` onwards |

# License

This code is Copyright Telicent Ltd and licensed under the [Apache License 2.0][ApacheLicense]

[Rfc7519]: https://datatracker.ietf.org/doc/html/rfc7519
[Rfc6750]: https://datatracker.ietf.org/doc/html/rfc6750
[jjwt]: https://github.com/jwtk/jjwt
[Rfc7517]: https://datatracker.ietf.org/doc/html/rfc7517
[maven]: https://maven.apache.org
[AwsElbAuth]: https://docs.aws.amazon.com/elasticloadbalancing/latest/application/listener-authenticate-users.html#user-claims-encoding
[ApacheLicense]: https://www.apache.org/licenses/LICENSE-2.0
[Jackson]: https://github.com/FasterXML/jackson
