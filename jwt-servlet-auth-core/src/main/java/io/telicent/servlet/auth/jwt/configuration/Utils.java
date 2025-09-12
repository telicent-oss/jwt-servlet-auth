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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.Map;
import java.util.function.Function;

/**
 * Utilities relating to configuration parsing
 */
public class Utils {

    /**
     * Private constructor prevents direct instantiation
     */
    private Utils() {
    }

    /**
     * Parses a configuration parameter
     *
     * @param parameters   Map of parameters
     * @param param        Parameter
     * @param parser       Value parser
     * @param defaultValue Default value to use as fallback
     * @param <T>          Value type
     * @return Parsed value
     */
    public static <T> T parseParameter(Map<String, String> parameters, String param, Function<String, T> parser,
                                       T defaultValue) {
        if (parameters.containsKey(param)) {
            return parseParameter(parameters.get(param), parser, defaultValue);
        }
        return defaultValue;
    }

    /**
     * Parses a configuration parameter
     *
     * @param value        Raw parameter value
     * @param parser       Value parser
     * @param defaultValue Default value to use as fallback
     * @param <T>          Value type
     * @return Parsed value
     */
    public static <T> T parseParameter(String value, Function<String, T> parser, T defaultValue) {
        try {
            return parser.apply(value);
        } catch (Throwable e) {
            return defaultValue;
        }
    }

    /**
     * Finds the value of a claim at the given claim path if it exists in the given verified JWT
     *
     * @param jws       Verified JWT
     * @param claimPath Claim path, each item in the list represents a level of nesting
     * @param <T>       Target return type, if the value of the claim cannot be cast to this type then a
     *                  {@link ClassCastException} is thrown
     * @return Claim value, or {@code null} if no such claim
     * @throws ClassCastException May occur if the target return type is not compatible with the claim value type i.e.
     *                            the claim value cannot be cast to the target return type.  Note that due to the quirks
     *                            of generics in JVM this error is technically not thrown in this method, but rather at
     *                            the location where this method is called.
     */
    @SuppressWarnings("unchecked")
    public static <T> T findClaim(Jws<Claims> jws, String[] claimPath) {
        if (jws == null || claimPath == null || claimPath.length == 0) {
            return null;
        }

        Map<String, Object> claims = jws.getPayload();
        for (int i = 0; ; i++) {
            if (claims == null) {
                return null;
            }

            Object rawValue = claims.get(claimPath[i]);
            if (rawValue == null || i == claimPath.length - 1) {
                return (T) rawValue;
            } else if (rawValue instanceof Map<?, ?> mapClaim) {
                claims = (Map<String, Object>) mapClaim;
            } else {
                return null;
            }
        }
    }
}
