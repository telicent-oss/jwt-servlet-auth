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

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A provider of {@link JwtAuthenticationEngine} automatic configuration that will be {@link java.util.ServiceLoader}
 * discovered by {@link EngineFactory}
 */
public interface EngineProvider extends ConfigurationProvider {

    /**
     * Attempts to configure a {@link JwtAuthenticationEngine}
     *
     * @param paramSupplier  Parameter supplier
     * @param engineConsumer Engine consumer that will be called if configuration occurs
     * @param <TRequest>     Request type
     * @param <TResponse>    Response type
     * @return True if an engine was configured, false otherwise
     */
    <TRequest, TResponse> boolean configure(Function<String, String> paramSupplier,
                                            Consumer<JwtAuthenticationEngine<TRequest, TResponse>> engineConsumer);
}
