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
package io.telicent.servlet.auth.jwt.jaxrs3.examples;

import java.util.Map;

/**
 * Jersey properties shared by the example applications.
 * <p>
 * These applications don't use Jersey's optional WADL generation or the {@code jakarta.activation.DataSource} message
 * body writer, and the dependencies those need (JAX-B and Jakarta Activation) aren't on the classpath. Explicitly
 * disabling them stops Jersey warning about the missing dependencies every time an application starts.
 * </p>
 */
final class ExampleApplicationProperties {

    /**
     * Jersey's {@code ServerProperties.WADL_FEATURE_DISABLE}
     */
    static final String JERSEY_DISABLE_WADL = "jersey.config.server.wadl.disableWadl";
    /**
     * Jersey's {@code CommonProperties.PROVIDER_DEFAULT_DISABLE}
     */
    static final String JERSEY_DISABLE_DEFAULT_PROVIDERS = "jersey.config.disableDefaultProvider";

    private static final Map<String, Object> PROPERTIES =
            Map.of(JERSEY_DISABLE_WADL, true, JERSEY_DISABLE_DEFAULT_PROVIDERS, "DATASOURCE");

    private ExampleApplicationProperties() {
    }

    /**
     * Gets the properties to return from {@link jakarta.ws.rs.core.Application#getProperties()}
     *
     * @return Properties
     */
    static Map<String, Object> get() {
        return PROPERTIES;
    }
}
