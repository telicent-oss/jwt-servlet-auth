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
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Represents a path to a claim in a JWT
 *
 * @param path Sequence of path elements
 */
public record ClaimPath(String[] path) {

    /**
     * The null claim path
     */
    public static final ClaimPath NONE = null;

    /**
     * The empty claim path
     */
    public static final ClaimPath EMPTY = new ClaimPath(new String[0]);

    /**
     * Creates a claim path for a top level claim i.e. the claim occurs at the top level of the JWT
     *
     * @param claim Claim name
     * @return Claim path
     */
    public static ClaimPath topLevel(String claim) {
        return new ClaimPath(new String[] { claim });
    }

    /**
     * Creates a claim path for a sequence of path elements where each represents one level of nesting
     *
     * @param pathElements Path elements
     * @return Claim path
     */
    public static ClaimPath of(String... pathElements) {
        return new ClaimPath(pathElements);
    }

    /**
     * Creates a claim path from a list of path elements where each represents one level of nesting
     *
     * @param pathElements Path elements
     * @return Claim path
     */
    public static ClaimPath of(List<String> pathElements) {
        if (pathElements == null || pathElements.isEmpty()) {
            return ClaimPath.EMPTY;
        }
        return new ClaimPath(pathElements.toArray(new String[0]));
    }

    /**
     * Gets whether this is an empty claim path
     *
     * @return True if empty, false otherwise
     */
    public boolean isEmpty() {
        return this.path == null || this.path.length == 0;
    }

    /**
     * Gets whether this path represents a top level claim
     *
     * @return True if a top level claim, false otherwise
     */
    public boolean isTopLevel() {
        return this.path.length == 1;
    }

    /**
     * Finds the value of a claim at this claim path if it exists in the given verified JWT
     *
     * @param jws Verified JWT
     * @param <T> Target return type, if the value of the claim cannot be cast to this type then a
     *            {@link ClassCastException} is thrown
     * @return Claim value, or {@code null} if no such claim
     * @throws ClassCastException May occur if the target return type is not compatible with the claim value type i.e.
     *                            the claim value cannot be cast to the target return type.  Note that due to the quirks
     *                            of generics in JVM this error is technically not thrown in this method, but rather at
     *                            the location where this method is called.
     */
    @SuppressWarnings("unchecked")
    public <T> T find(Jws<Claims> jws) {
        if (jws == null || this.isEmpty()) return null;

        Map<String, Object> claims = jws.getPayload();
        for (int i = 0; ; i++) {
            if (claims == null) {
                return null;
            }

            Object rawValue = claims.get(this.path[i]);
            if (rawValue == null || i == this.path.length - 1) {
                return (T) rawValue;
            } else if (rawValue instanceof Map<?, ?> mapClaim) {
                claims = (Map<String, Object>) mapClaim;
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the claim path as it would be serialized for Configuration
     *
     * @return Configuration string form of the claim path
     */
    public String toConfigurationString() {
        return StringUtils.join(this.path, ".");
    }
}
