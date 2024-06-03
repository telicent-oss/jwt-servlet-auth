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
package io.telicent.servlet.auth.jwt.configuration;

import io.telicent.servlet.auth.jwt.JwtHttpConstants;

/**
 * Configuration parameters used for {@link AutomatedConfiguration} of filters
 */
public class ConfigurationParameters {

    private ConfigurationParameters() {}

    /**
     * Parameter for indicating that your application wants to permit multiple different filter configurations to be
     * present
     */
    public static final String PARAM_ALLOW_MULTIPLE_CONFIGS = "jwt.configs.allow-multiple";
    /**
     * Parameter for configuring the use of the default JWT Headers as defined by
     * {@link JwtHttpConstants#DEFAULT_HEADER_SOURCES}
     */
    public static final String PARAM_USE_DEFAULT_HEADERS = "jwt.headers.use-defaults";
    /**
     * Parameter for configuring a list of HTTP Header names that should be inspected to find a JWT, expressed in order
     * of preference
     */
    public static final String PARAM_HEADER_NAMES = "jwt.headers.names";
    /**
     * Parameter for configuring the list of HTTP Header value prefixes associated with the {@link #PARAM_HEADER_NAMES}
     * list.  For example {@code Bearer} to indicate that you expect the configured header to contain the value
     * {@code Bearer JWT}, if an entry in this list is blank then the associated header is expected to contain the JWT
     * as-is.
     */
    public static final String PARAM_HEADER_PREFIXES = "jwt.headers.prefixes";
    /**
     * Parameter for configuring the list of claims that are used to find the username for a user from a verified JWT,
     * should be given in order of preference
     */
    public static final String PARAM_USERNAME_CLAIMS = "jwt.username.claims";
    /**
     * Parameter for configuring the realm that will be presented to users who are not authenticated as part of the HTTP
     * challenge
     */
    public static final String PARAM_REALM = "jwt.realm";
    /**
     * Parameter for configuring a list of {@link io.telicent.servlet.auth.jwt.PathExclusion} that control paths that
     * are not subject to JWT authentication
     */
    public static final String PARAM_PATH_EXCLUSIONS = "jwt.path-exclusions";
    /**
     * Parameter that configures a secret key for verification
     */
    public static final String PARAM_SECRET_KEY = "jwt.secret.key";
    /**
     * Parameter that configures a public key for verification
     */
    public static final String PARAM_PUBLIC_KEY = "jwt.public.key";
    /**
     * Parameter that configures the key algorithm used for the public/secret key
     */
    public static final String PARAM_KEY_ALGORITHM = "jwt.key.algorithm";
    /**
     * Parameter that configures a JWKS URL from which keys can be obtained for verification
     */
    public static final String PARAM_JWKS_URL = "jwt.jwks.url";
    /**
     * Parameter that configures how long keys retrieved from a JWKS URL will be cached for
     */
    public static final String PARAM_JWKS_CACHE_KEYS_FOR = "jwt.jwks.cache.minutes";
    /**
     * Parameter that configures the allowed clock skew used for token verification
     */
    public static final String PARAM_ALLOWED_CLOCK_SKEW = "jwt.allowed.clock.skew";
    /**
     * The default amount of time for which JWKS loaded keys will be cached
     */
    public static final int DEFAULT_JWKS_CACHE_KEYS_FOR = 60;
}
