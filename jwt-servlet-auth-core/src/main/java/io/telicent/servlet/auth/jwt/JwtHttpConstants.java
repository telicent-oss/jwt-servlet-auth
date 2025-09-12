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
import org.apache.commons.lang3.RegExUtils;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Constants related to HTTP usage in conjunction with JWTs
 */
public class JwtHttpConstants {

    /**
     * A regular expression that matches characters that are considered invalid for use in a sanitised HTTP Header
     * parameter value, this is utilised by the {@link #sanitiseHeaderParameterValue(String)} method.
     * <p>
     * This pattern considers anything that is not one of the following as invalid:
     * </p>
     * <ul>
     *     <li>Alphanumeric characters (letters and digits)</li>
     *     <li>Hyphens ({@code -})</li>
     *     <li>Underscores {@code _}</li>
     *     <li>Periods ({@code .}), Commas ({@code ,}) and Semicolons ({@code ;})</li>
     *     <li>Forward slashes ({@code /})</li>
     *     <li>Single quotes ({@code '})</li>
     *     <li>Equals ({@code =}) and Plus ({@code +})</li>
     *     <li>The basic space character</li>
     * </ul>
     */
    public static final Pattern INVALID_PARAM_CHARACTERS = Pattern.compile("[^\\p{L}\\d\\-_.,;/'=+ ]");

    /**
     * A regular expression that matches characters that are considered invalid for use in a sanitised HTTP Header
     * value, this is utilised by the {@link #sanitiseHeader(String)} method.
     * <p>
     * This considers anything that is not an acceptable character for {@link #INVALID_PARAM_CHARACTERS}, or a double
     * quote {@code "}, as invalid.
     * </p>
     */
    public static final Pattern INVALID_HEADER_CHARACTERS = Pattern.compile("[^\\p{L}\\d\\-_.,;/'\"=+ ]");

    private JwtHttpConstants() {
    }

    /**
     * The standard HTTP {@code Authorization} header
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * The standard HTTP {@code WWW-Authenticate} header
     */
    public static final String HEADER_WWW_AUTHENTICATE = "WWW-Authenticate";

    /**
     * The HTTP {@code Bearer} authentication scheme
     */
    public static final String AUTH_SCHEME_BEARER = "Bearer";

    /**
     * The realm challenge parameter used in HTTP Authorization challenges
     */
    public static final String CHALLENGE_PARAMETER_REALM = "realm";

    /**
     * Default HTTP Headers from which to pull the raw JSON Web Token (JWT)
     */
    public static final Collection<HeaderSource>
            DEFAULT_HEADER_SOURCES =
            List.of(new HeaderSource(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER));

    /**
     * Sanitises a value that is intended for inclusion in a parameter within an HTTP Header value to avoid
     * request/response splitting attacks
     * <p>
     * A sanitised value consists only of characters not matching the {@link #INVALID_PARAM_CHARACTERS} regular
     * expression, any other characters are removed from the provided value.
     * </p>
     *
     * @param value Value to sanitise
     * @return Sanitised values
     */
    public static String sanitiseHeaderParameterValue(String value) {
        return RegExUtils.removeAll((CharSequence) value, INVALID_PARAM_CHARACTERS);
    }

    /**
     * Sanitises a value that is intended to be a value for an HTTP Header to avoid request/response splitting attacks
     * <p>
     * A sanitised value consists only of characters not matching the {@link #INVALID_HEADER_CHARACTERS} regular
     * expression, any other characters are removed from the provided value.
     * </p>
     *
     * @param header Header value to sanitise
     * @return Sanitised values
     */
    public static String sanitiseHeader(String header) {
        return RegExUtils.removeAll((CharSequence) header, INVALID_HEADER_CHARACTERS);
    }
}
