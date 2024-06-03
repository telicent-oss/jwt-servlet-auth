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

/**
 * A configuration adaptor to allow sharing configuration code across different servlet runtimes
 */
public interface RuntimeConfigurationAdaptor {

    /**
     * Gets a configuration parameter
     *
     * @param param Parameter
     * @return Parameter value, or {@code null} if no such parameter exists
     */
    String getParameter(String param);

    /**
     * Sets an attribute i.e. accepts the results of some configuration operation and stores it for later retrieval
     *
     * @param attribute Attribute
     * @param value     Value
     */
    void setAttribute(String attribute, Object value);

    /**
     * Gets the value of an attribute
     * @param attribute Attribute
     * @return Value
     */
    Object getAttribute(String attribute);
}
