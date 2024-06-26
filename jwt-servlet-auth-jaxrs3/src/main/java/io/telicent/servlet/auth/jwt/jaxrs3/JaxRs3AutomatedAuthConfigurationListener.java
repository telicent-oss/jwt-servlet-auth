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
package io.telicent.servlet.auth.jwt.jaxrs3;

import io.telicent.servlet.auth.jwt.configuration.AutomatedConfiguration;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * A servlet context listener that provides automated JWT Auth configuration when used
 */
public class JaxRs3AutomatedAuthConfigurationListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AutomatedConfiguration.configure(new JaxRs3ConfigAdaptor(sce.getServletContext()));
    }
}
