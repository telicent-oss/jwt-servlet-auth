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
package io.telicent.servlet.auth.jwt.fake;

import io.telicent.servlet.auth.jwt.JwtAuthenticationEngine;
import io.telicent.servlet.auth.jwt.configuration.AbstractHeaderBasedEngineProvider;
import io.telicent.servlet.auth.jwt.sources.HeaderSource;

import java.util.List;

public class FakeEngineProvider extends AbstractHeaderBasedEngineProvider {
    @Override
    @SuppressWarnings("unchecked")
    protected <TRequest, TResponse> JwtAuthenticationEngine<TRequest, TResponse> createEngine(
            List<HeaderSource> headerSources, String realm, List<String> usernameClaims, String[] rolesClaim) {
        return (JwtAuthenticationEngine<TRequest, TResponse>) new FakeEngine(headerSources, realm, usernameClaims,
                                                                             rolesClaim);
    }

    @Override
    public int priority() {
        // As this provider is only for tests if running integration tests set its priority to the lowest possible
        return Integer.MIN_VALUE;
    }
}
