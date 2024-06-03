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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapRuntimeConfigAdaptor implements RuntimeConfigurationAdaptor {

    private final Map<String, String> config;
    private final Map<String, Object> attributes = new HashMap<>();

    public MapRuntimeConfigAdaptor() {
        this(Collections.emptyMap());
    }

    public MapRuntimeConfigAdaptor(Map<String, String> config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public String getParameter(String param) {
        return this.config.get(param);
    }

    @Override
    public void setAttribute(String attribute, Object value) {
        this.attributes.put(attribute, value);
    }

    @Override
    public Object getAttribute(String attribute) {
        return this.attributes.get(attribute);
    }
}
