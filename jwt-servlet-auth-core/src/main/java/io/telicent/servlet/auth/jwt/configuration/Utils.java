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

import java.util.Map;
import java.util.function.Function;

/**
 * Utilities relating to configuration parsing
 */
public class Utils {

    /**
     * Private constructor prevents direct instantiation
     */
    private Utils() {}

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
}
