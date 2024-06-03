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
 * Constants relating to logging integration
 *
 * @deprecated Renamed to {@link JwtLoggingConstants}
 */
@Deprecated(forRemoval = true, since = "0.13.0")
public class LoggingConstants {

    /**
     * Private constructor prevents instantiation
     */
    private LoggingConstants() {

    }

    /**
     * An attribute that will be set in the logging {@link org.slf4j.MDC} to indicate the currently authenticated user
     * @deprecated Renamed to {@link JwtLoggingConstants#MDC_JWT_USER}
     */
    @Deprecated(forRemoval = true, since = "0.13.0")
    public static final String MDC_JWT_USER = "JwtUser";
}
