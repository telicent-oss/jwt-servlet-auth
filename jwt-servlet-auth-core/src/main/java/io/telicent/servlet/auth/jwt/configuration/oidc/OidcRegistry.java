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
package io.telicent.servlet.auth.jwt.configuration.oidc;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.net.URI;
import java.time.Duration;

/**
 * A static registry of Open ID Connect discovery URIs to loaded {@link OidcConfiguration}'s, intended to allow
 * applications that need further access to the configuration beyond just the JWKS URL to access it
 * <p>
 * Discovered configurations are cached for a short period after their retrieval (15 minutes) to allow other parts of
 * the application to access it and obtain any additional configuration needed.
 * </p>
 */
public class OidcRegistry {

    private static final Cache<URI, OidcConfiguration> CONFIGURATIONS =
            Caffeine.newBuilder().maximumSize(5).expireAfterWrite(Duration.ofMinutes(15)).build();

    /**
     * Private constructor to prevent direct instantiation
     */
    private OidcRegistry() {

    }

    /**
     * Registers loaded configuration for the given URI
     * <p>
     * Automatically called by {@link OidcConfigurationLoader#load(URI)} upon successful configuration discovery.
     * </p>
     *
     * @param discoveryUri  Discovery URI
     * @param configuration Configuration
     */
    public static void register(URI discoveryUri, OidcConfiguration configuration) {
        if (configuration != null) {
            CONFIGURATIONS.put(discoveryUri, configuration);
        }
    }

    /**
     * Gets previously registered configuration (if any)
     * <p>
     * Note that configurations are only kept registered for a short period (15 minutes) as it is assumed that most
     * configuration will be discovered and utilised early in the application lifecycle after which time it can be
     * safely discarded.
     * </p>
     *
     * @param discoveryUri Discovery URI
     * @return Configuration, or {@code null} if not yet registered
     */
    public static OidcConfiguration get(URI discoveryUri) {
        return CONFIGURATIONS.getIfPresent(discoveryUri);
    }

    /**
     * Resets the configuration registry wiping any previously registered configurations
     * <p>
     * Primarily only needed for unit testing scenarios to ensure test isolation.
     * </p>
     */
    public static void reset() {
        CONFIGURATIONS.invalidateAll();
    }
}
