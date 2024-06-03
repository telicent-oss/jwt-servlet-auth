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
package io.telicent.servlet.auth.jwt.servlet3;

import io.telicent.servlet.auth.jwt.configuration.RuntimeConfigurationAdaptor;

import javax.servlet.FilterConfig;
import java.util.Objects;

/**
 * A runtime configuration adaptor for Servlet 3.x environments
 */
public class Servlet3FilterConfigAdaptor implements RuntimeConfigurationAdaptor {
    private final FilterConfig config;

    /**
     * Creates a new adaptor
     *
     * @param config Filter configuration
     */
    public Servlet3FilterConfigAdaptor(FilterConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public String getParameter(String param) {
        return this.config.getInitParameter(param);
    }

    @Override
    public void setAttribute(String attribute, Object value) {
        this.config.getServletContext().setAttribute(attribute, value);
    }

    @Override
    public Object getAttribute(String attribute) {
        return this.config.getServletContext().getAttribute(attribute);
    }
}
