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
import io.telicent.servlet.auth.jwt.sources.HeaderSource;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class TestBadEngineProvider {

    private static final Function<String, String> PARAM_SUPPLIER = x -> null;

    /**
     * A provider that always returns {@code null}
     */
    private static final class NullEngineProvider extends AbstractHeaderBasedEngineProvider {

        @Override
        protected <TRequest, TResponse> JwtAuthenticationEngine<TRequest, TResponse> createEngine(
                List<HeaderSource> headerSources, String realm, List<String> usernameClaims) {
            return null;
        }
    }

    private static final class ThrowingEngineProvider extends AbstractHeaderBasedEngineProvider {

        @Override
        protected <TRequest, TResponse> JwtAuthenticationEngine<TRequest, TResponse> createEngine(
                List<HeaderSource> headerSources, String realm, List<String> usernameClaims) {
            throw new RuntimeException("Failed");
        }
    }

    @Test
    public void givenNullEngineProvider_whenCreatingEngine_thenNotConfigured() {
        // Given
        EngineProvider provider = new NullEngineProvider();
        AtomicBoolean called = new AtomicBoolean(false);

        // When
        boolean configured = provider.configure(PARAM_SUPPLIER, x -> called.set(true));

        // Then
        Assert.assertFalse(configured);
        Assert.assertFalse(called.get());
    }

    @Test
    public void givenThrowingEngineProvider_whenCreatingEngine_thenNotConfigured() {
        // Given
        EngineProvider provider = new ThrowingEngineProvider();
        AtomicBoolean called = new AtomicBoolean(false);

        // When
        boolean configured = provider.configure(PARAM_SUPPLIER, x -> called.set(true));

        // Then
        Assert.assertFalse(configured);
        Assert.assertFalse(called.get());
    }
}
