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
package io.telicent.servlet.auth.jwt.sources;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

/**
 * Represents an HTTP header based token source
 */
public class HeaderSource implements TokenSource {

    private final String header, prefix;

    /**
     * Creates a new header source
     *
     * @param header HTTP Header
     * @param prefix Header prefix, i.e. a string that this header value <strong>MUST</strong> start with and will be
     *               removed to yield the actual.  This <strong>MAY</strong> be {@code null} if this header will just
     *               contain the JWT directly without any prefix present.
     */
    public HeaderSource(String header, String prefix) {
        if (StringUtils.isBlank(header)) {
            throw new IllegalArgumentException("header cannot be null/blank");
        }
        this.header = header;
        this.prefix = prefix;
    }

    /**
     * Gets the HTTP header from which the token should be sourced
     *
     * @return Header
     */
    public String getHeader() {
        return header;
    }

    /**
     * Gets the HTTP Header prefix that is required on the source header
     *
     * @return Header prefix
     */
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getRawToken(String rawValue) {
        if (StringUtils.isNotBlank(this.prefix)) {
            if (!Strings.CI.startsWith(rawValue, prefix)) {
                return null;
            }
            String rawToken = rawValue.substring(prefix.length());
            if (StringUtils.isNotBlank(rawToken)) {
                return StringUtils.trim(rawToken);
            }
        } else if (StringUtils.isNotBlank(rawValue)) {
            return StringUtils.trim(rawValue);
        }

        return null;
    }

    @Override
    public String toString() {
        if (StringUtils.isNotBlank(this.prefix)) {
            return String.format("%s: %s <jwt>", this.header, this.prefix);
        } else {
            return String.format("%s: <jwt>", this.header);
        }
    }
}
