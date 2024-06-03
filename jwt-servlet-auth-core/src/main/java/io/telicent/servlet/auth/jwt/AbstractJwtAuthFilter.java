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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * An abstract base class for JWT Authentication filters, this avoids any dependency on a version of the Java Servlet
 * API so that common code can be reused
 *
 * @param <TRequest>  Request type
 * @param <TResponse> Response type
 */
public class AbstractJwtAuthFilter<TRequest, TResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJwtAuthFilter.class);

    /**
     * Gets whether the given path is an excluded path to which the filter should not apply
     *
     * @param path       Path
     * @param exclusions Exclusions
     * @return True if the path is excluded from filtering i.e. no JWT authentication is required, false otherwise
     */
    protected boolean isExcludedPath(String path, List<PathExclusion> exclusions) {
        if (exclusions == null || exclusions.isEmpty()) {
            return false;
        }
        if (StringUtils.isBlank(path)) {
            return false;
        }

        boolean excluded = exclusions.stream().anyMatch(e -> e.matches(path));
        if (excluded) {
            LOGGER.warn("Request to path {} is excluded from JWT Authentication filtering by filter configuration",
                        path);
        }
        return excluded;
    }
}
