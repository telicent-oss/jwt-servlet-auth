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

import java.util.Comparator;
import java.util.List;

/**
 * Base interface for configuration provider
 */
public interface ConfigurationProvider {

    /**
     * Indicates the priority of the provider, defaults to {@code 0} if not overridden in a provider implementation.
     * <p>
     * Priorities are used to determine which provider is tried first in the event that multiple providers are present
     * in an environment.
     * </p>
     *
     * @return Priority
     */
    default int priority() {
        return 0;
    }

    /**
     * Sorts a list of providers based upon their priorities with highest priority values appearing first in the
     * resulting list
     *
     * @param providers Providers
     */
    static void sort(List<? extends ConfigurationProvider> providers) {
        providers.sort(Comparator.nullsLast(Comparator.<ConfigurationProvider>comparingInt(
                                                              ConfigurationProvider::priority)
                                                      .reversed()
                                                      .thenComparing(Object::hashCode)));
    }
}
