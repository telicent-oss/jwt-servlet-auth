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
package io.telicent.servlet.auth.jwt;

/**
 * Provides useful constants related to Servlets
 *
 * @deprecated Renamed to {@link JwtServletConstants}
 */
@Deprecated(forRemoval = true, since = "0.13.0")
public class ServletConstants {

    private ServletConstants() {
    }

    /**
     * Context attribute used to hold a JWT Authentication engine
     *
     * @deprecated Renamed to {@link JwtServletConstants#ATTRIBUTE_JWT_ENGINE}
     */
    @Deprecated(forRemoval = true, since = "0.13.0")
    public static final String ATTRIBUTE_JWT_ENGINE = "io.telicent.servlet.auth.jwt.engine";

    /**
     * Context attribute used to hold a JWT Verifier
     *
     * @deprecated Renamed to {@link JwtServletConstants#ATTRIBUTE_JWT_VERIFIER}
     */
    @Deprecated(forRemoval = true, since = "0.13.0")
    public static final String ATTRIBUTE_JWT_VERIFIER = "io.telicent.servlet.auth.jwt.verifier";

    /**
     * Context attribute used to hold Path Exclusions
     *
     * @deprecated Renamed to {@link JwtServletConstants#ATTRIBUTE_PATH_EXCLUSIONS}
     */
    @Deprecated(forRemoval = true, since = "0.13.0")
    public static final String ATTRIBUTE_PATH_EXCLUSIONS = "io.telicent.servlet.auth.jwt.path-exclusions";
}
