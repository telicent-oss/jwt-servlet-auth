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

import io.telicent.servlet.auth.jwt.configuration.RuntimeConfigurationAdaptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FilterConfigAdaptorWrapper implements RuntimeConfigurationAdaptor {
    private final RuntimeConfigurationAdaptor adaptor;
    private final Map<String, Object> attributes = new HashMap<>();

    public FilterConfigAdaptorWrapper(RuntimeConfigurationAdaptor adaptor) {
        this.adaptor = Objects.requireNonNull(adaptor);
    }

    @Override
    public String getParameter(String param) {
        return this.adaptor.getParameter(param);
    }

    @Override
    public void setAttribute(String attribute, Object value) {
        this.attributes.put(attribute, value);
        this.adaptor.setAttribute(attribute, value);
    }

    @Override
    public Object getAttribute(String attribute) {
        return this.attributes.get(attribute);
    }
}
