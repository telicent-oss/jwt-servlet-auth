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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
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
     * Default size of the exclusion warnings cache
     */
    protected static final int EXCLUSIONS_CACHE_SIZE = 10;
    /**
     * We create a basic cache to control the flow of path exclusion warnings because without this these warnings can
     * dominate the logs of relatively quiet services if automated monitoring tools are regularly pinging a health
     * status endpoint (or other equivalent) that's been configured for exclusion and detracts from actual useful
     * logging from the service.
     * <p>
     * Note that we set the cache size intentionally quite small (see {@link #EXCLUSIONS_CACHE_SIZE}) as applications
     * should generally have very few exclusions, if they have too many paths being excluded then that's most likely a
     * sign that they are misconfigured.  In that case we want them to be spammed by the warnings so they realise their
     * mistake!
     * </p>
     */
    protected static final Cache<String, Boolean> EXCLUSION_WARNINGS_CACHE =
            Caffeine.newBuilder()
                    .expireAfterWrite(Duration.ofMinutes(15))
                    .initialCapacity(EXCLUSIONS_CACHE_SIZE)
                    .maximumSize(
                            EXCLUSIONS_CACHE_SIZE)
                    .build();

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
            // Use a cache to prevent these warnings being spammed endlessly, this is especially true when something
            // like a health status endpoint is excluded from authentication and being regularly hit by automated
            // monitoring tools
            if (EXCLUSION_WARNINGS_CACHE.getIfPresent(path) == null) {
                LOGGER.warn("Request to path {} is excluded from JWT Authentication filtering by filter configuration",
                            path);
                EXCLUSION_WARNINGS_CACHE.put(path, Boolean.TRUE);
            }
        }
        return excluded;
    }
}
