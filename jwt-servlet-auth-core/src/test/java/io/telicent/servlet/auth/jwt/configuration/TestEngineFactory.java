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
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TestEngineFactory extends AbstractFactoryTests {

    @Test
    public void givenNoConfig_whenConfiguringEngine_thenNothingIsConfigured() {
        // Given
        AtomicReference<JwtAuthenticationEngine<?, ?>> configured = new AtomicReference<>();

        // When
        EngineFactory.configure(NULL_PARAM_SUPPLIER, x -> configured.set(x));

        // Then
        Assert.assertNull(configured.get());
    }

    @Test
    public void givenCustomHeaderConfig_whenConfiguringEngine_thenEngineIsConfigured() {
        // Given
        AtomicReference<JwtAuthenticationEngine<?, ?>> configured = new AtomicReference<>();
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_USE_DEFAULT_HEADERS, "true",
                                            ConfigurationParameters.PARAM_HEADER_NAMES,
                                            "X-Auth-Token,X-Token,X-ApiKey",
                                            ConfigurationParameters.PARAM_HEADER_PREFIXES, "Bearer,Bearer");

        // When
        EngineFactory.configure(supplierForMap(config), x -> configured.set(x));

        // Then
        Assert.assertNotNull(configured);
    }

    @Test
    public void givenCustomHeaderConfigWithoutPrefixes_whenConfiguringEngine_thenEngineIsConfigured() {
        // Given
        AtomicReference<JwtAuthenticationEngine<?, ?>> configured = new AtomicReference<>();
        Map<String, String> config = Map.of(ConfigurationParameters.PARAM_USE_DEFAULT_HEADERS, "true",
                                            ConfigurationParameters.PARAM_HEADER_NAMES,
                                            "X-Auth-Token,X-Token,X-ApiKey",
                                            ConfigurationParameters.PARAM_REALM, "Secret Squirrel HQ");

        // When
        EngineFactory.configure(supplierForMap(config), x -> configured.set(x));

        // Then
        Assert.assertNotNull(configured);
    }
}
