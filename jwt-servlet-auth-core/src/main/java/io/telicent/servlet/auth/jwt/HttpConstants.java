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

import io.telicent.servlet.auth.jwt.sources.HeaderSource;

import java.util.Collection;
import java.util.List;

/**
 * Constants related to HTTP
 *
 * @deprecated Replaced by {@link JwtHttpConstants} to reflect that we're only declaring constants specific to how JWTs
 * are used with HTTP
 */
@Deprecated(forRemoval = true, since = "0.13.0")
public class HttpConstants {

    private HttpConstants() {
    }

    /**
     * The standard HTTP {@code Authorization} header
     *
     * @deprecated Renamed to {@link JwtHttpConstants#HEADER_AUTHORIZATION}
     */
    @Deprecated(forRemoval = true, since = "0.13.0")
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * The standard HTTP {@code WWW-Authenticate} header
     *
     * @deprecated Renamed to {@link JwtHttpConstants#HEADER_WWW_AUTHENTICATE}
     */
    @Deprecated(forRemoval = true, since = "0.13.0")
    public static final String HEADER_WWW_AUTHENTICATE = "WWW-Authenticate";

    /**
     * The HTTP {@code Bearer} authentication scheme
     *
     * @deprecated Renamed to {@link JwtHttpConstants#AUTH_SCHEME_BEARER}
     */
    @Deprecated(forRemoval = true, since = "0.13.0")
    public static final String AUTH_SCHEME_BEARER = "Bearer";

    /**
     * The realm challenge parameter used in HTTP Authorization challenges
     *
     * @deprecated Renamed to {@link JwtHttpConstants#CHALLENGE_PARAMETER_REALM}
     */
    @Deprecated(forRemoval = true, since = "0.13.0")
    public static final String CHALLENGE_PARAMETER_REALM = "realm";

    /**
     * Default HTTP Headers from which to pull the raw JSON Web Token (JWT)
     *
     * @deprecated Renamed to {@link JwtHttpConstants#DEFAULT_HEADER_SOURCES}
     */
    @Deprecated(forRemoval = true, since = "0.13.0")
    public static final Collection<HeaderSource> DEFAULT_HEADER_SOURCES =
            List.of(new HeaderSource(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER));
}
