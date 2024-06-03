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

import io.telicent.servlet.auth.jwt.JwtAuthenticationEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A factory for creating {@link JwtAuthenticationEngine} from configuration
 */
public class EngineFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineFactory.class);

    private static final List<EngineProvider> PROVIDERS = new ArrayList<>();

    static {
        try {
            ServiceLoader<EngineProvider> loader = ServiceLoader.load(EngineProvider.class);
            Iterator<EngineProvider> iter = loader.iterator();
            while (iter.hasNext()) {
                PROVIDERS.add(iter.next());
            }
        } catch (ServiceConfigurationError e) {
            LOGGER.warn("Failed to load a EngineProvider: ", e);
        } finally {
            ConfigurationProvider.sort(PROVIDERS);
        }
    }

    private EngineFactory() {
    }

    /**
     * Configures a JWT Authentication Engine in a runtime independent way
     *
     * @param paramSupplier  Supplier function where configuration parameters can be obtained from
     * @param engineConsumer Consumer function that takes the configured engine
     * @param <TRequest> Request type
     * @param <TResponse> Response type
     */
    public static <TRequest, TResponse> void configure(Function<String, String> paramSupplier,
                                                       Consumer<JwtAuthenticationEngine<TRequest, TResponse>> engineConsumer) {
        for (EngineProvider provider : PROVIDERS) {
            if (provider.configure(paramSupplier, engineConsumer)) {
                return;
            }
        }
        LOGGER.info(
                "Failed to configure any JWT engine from the available providers.  The default engine for your runtime will be used as a result.");
    }

    /**
     * Gets how many engine providers are available
     * @return Total engine providers
     */
    public static int available() {
        return PROVIDERS.size();
    }
}
