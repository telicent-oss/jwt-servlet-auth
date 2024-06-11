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
 */
public class JwtServletConstants {

    private JwtServletConstants() { }

    /**
     * Context attribute used to hold a JWT Authentication engine
     */
    public static final String ATTRIBUTE_JWT_ENGINE = "io.telicent.servlet.auth.jwt.engine";
    /**
     * Context attribute used to hold a JWT Verifier
     */
    public static final String ATTRIBUTE_JWT_VERIFIER = "io.telicent.servlet.auth.jwt.verifier";
    /**
     * Context attribute used to hold Path Exclusions
     */
    public static final String ATTRIBUTE_PATH_EXCLUSIONS = "io.telicent.servlet.auth.jwt.path-exclusions";

    /**
     * Request attribute used to hold the raw JWT that authenticated the user
     */
    public static final String REQUEST_ATTRIBUTE_RAW_JWT = "io.telicent.servlet.auth.jwt.raw";

    /**
     * Request attribute used to hold the verified JWT that authenticated the user
     */
    public static final String REQUEST_ATTRIBUTE_VERIFIED_JWT = "io.telicent.servlet.auth.jwt.verified";
}
