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
package io.telicent.servlet.auth.jwt.challenges;

import io.telicent.servlet.auth.jwt.sources.TokenSource;

import java.util.Objects;

/**
 * A candidate authentication token
 */
public record TokenCandidate(TokenSource source, String value) {

    /**
     * Creates a new token candidate
     *
     * @param source Token source
     * @param value  Raw value
     */
    public TokenCandidate {
        Objects.requireNonNull(source, "Token Source cannot be null");
    }

    /**
     * Gets the token source
     *
     * @return Token source
     */
    @Override
    public TokenSource source() {
        return source;
    }

    /**
     * Gets the candidate token value
     *
     * @return Value
     */
    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TokenCandidate{source=").append(source).append(", value=").append(value).append("}");
        return builder.toString();
    }
}
