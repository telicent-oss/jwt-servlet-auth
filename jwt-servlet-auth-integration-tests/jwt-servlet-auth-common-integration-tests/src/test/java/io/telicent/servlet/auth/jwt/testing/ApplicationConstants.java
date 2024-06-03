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
package io.telicent.servlet.auth.jwt.testing;

public class ApplicationConstants {
    /**
     * A web application that offers a single resource at {@code /hello} that returns {@code Hello World!} as a
     * {@code text/plain} response.  The application is protected by JWT authentication with keys verified by a
     * {@code test.key} secret key.
     */
    public static final String BASIC = "basic";

    /**
     * A web application that offers a single resource at {@code /hello} that returns {@code Hello World!} as a
     * {@code text/plain} response.  The application is protected by JWT authentication with keys verified by a
     * {@code test.key} secret key.  However, the application requires that JWT be presented in a custom
     * {@code X-API-Key} header and the username presented in the {@code email} claim.
     */
    public static final String CUSTOM_CLAIMS = "custom-claims";
    /**
     * A web application that offers a {@code /public/hello} and a {@code /private/hello} resource that follows the same
     * behaviour as described in {@link #BASIC}.
     * <p>
     * Only the {@code /private/hello} path should be protected by JWT authentication.   Exposing {@code /public/hello}
     * to unauthenticated requests should be done by using our path exclusions functionality.
     * </p>
     */
    public static final String EXCLUSIONS = "exclusions";
    /**
     * A web application offering the same resources as {@link #EXCLUSIONS}
     * <p>
     * Only the {@code /private/hello} path should be protected by JWT authentication and this <strong>MUST</strong> be
     * achieved by only applying our filter to the {@link /private/*} paths.
     * </p>
     */
    public static final String MAPPING = "mapping";
    /**
     * A web application offering the same resources as {@link #BASIC} but protected by public key verification using a
     * RSA algorithm {@code public.key} file
     */
    public static final String PUBLIC_KEY = "public-key";
    /**
     * A web application offering the same resources as {@link #BASIC} but protected by JWKS verification using a
     * {@code jwks.json} file
     */
    public static final String JWKS = "jwks";
    /**
     * A web application offering resources at {@code /secret-key/hello} and {@code /public-key/hello} where they are
     * respectively protected by a secret key and a public key.  Thus requests need to sign the JWT in different ways
     * depending upon the resource they are accessing.  This application demonstrates the ability (from 0.9.0 onwards)
     * to have multiple filters configured with different configurations.
     */
    public static final String MULTIPLE_FILTERS = "multiple-filters";
}
