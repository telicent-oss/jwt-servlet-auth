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
package io.telicent.servlet.auth.jwt.servlet5.examples;

import io.telicent.servlet.auth.jwt.configuration.ConfigurationParameters;
import io.telicent.servlet.auth.jwt.servlet5.JwtAuthFilter;
import io.telicent.servlet.auth.jwt.testing.AbstractIntegrationTests;
import io.telicent.servlet.auth.jwt.testing.AbstractServer;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.ee9.servlet.FilterHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.ee9.servlet.ServletContextHandler;
import org.eclipse.jetty.ee9.servlet.ServletHandler;
import org.eclipse.jetty.ee9.servlet.ServletHolder;
import org.eclipse.jetty.ee9.webapp.WebAppContext;
import org.eclipse.jetty.util.resource.ResourceFactory;

import java.io.File;
import java.util.EnumSet;

public class TestServlet5Application extends AbstractIntegrationTests {

    protected AbstractServer buildProgrammaticApplication(File keyFile, int port) {
        Server server = new Server(port);
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        ServletHandler servletHandler = new ServletHandler();
        handler.setHandler(servletHandler);
        ServletHolder helloWorld = servletHandler.addServletWithMapping(HelloWorldServlet.class, "/hello");
        helloWorld.setInitParameter(ConfigurationParameters.PARAM_SECRET_KEY, keyFile.getAbsolutePath());
        FilterHolder auth =
                servletHandler.addFilterWithMapping(JwtAuthFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        auth.setInitParameter(ConfigurationParameters.PARAM_SECRET_KEY, keyFile.getAbsolutePath());
        server.setHandler(handler);
        return new JettyServlet5Server(server, port);
    }

    protected AbstractServer buildWebXmlApplication(int port, String appName) {
        ensureWebAppExists(appName);

        Server server = new Server(port);
        WebAppContext webApp = new WebAppContext(ResourceFactory.root().newResource(new File("src/test/apps/" + appName).toURI()), "/");
        server.setHandler(webApp);
        return new JettyServlet5Server(server, port);
    }

}
