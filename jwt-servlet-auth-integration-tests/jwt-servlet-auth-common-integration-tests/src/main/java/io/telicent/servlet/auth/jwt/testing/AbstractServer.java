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
package io.telicent.servlet.auth.jwt.testing;

import io.telicent.servlet.auth.jwt.configuration.RuntimeConfigurationAdaptor;

/**
 * An abstract server runtime for dropping different concrete runtimes into integration tests
 */
public abstract class AbstractServer {

    /**
     * Starts the server
     */
    public abstract void start();

    /**
     * Stops the server
     */
    public abstract void stop();

    /**
     * Gets the Base URL of the server
     *
     * @return Base URL
     */
    public abstract String getBaseUrl();

    /**
     * Gets the runtime configuration adaptor for the server
     * @return Runtime configuration adaptor
     */
    public abstract RuntimeConfigurationAdaptor getRuntimeConfiguration();

}
