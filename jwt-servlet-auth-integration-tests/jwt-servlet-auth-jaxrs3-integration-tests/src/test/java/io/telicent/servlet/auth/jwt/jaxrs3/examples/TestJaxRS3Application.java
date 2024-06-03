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

import io.telicent.servlet.auth.jwt.configuration.ConfigurationParameters;
import io.telicent.servlet.auth.jwt.jaxrs3.JaxRs3AutomatedAuthConfigurationListener;
import io.telicent.servlet.auth.jwt.testing.AbstractIntegrationTests;
import io.telicent.servlet.auth.jwt.testing.AbstractServer;
import jakarta.servlet.ServletContextListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TestJaxRS3Application extends AbstractIntegrationTests {
    @Override
    protected boolean areNonExistentUrlsFiltered() {
        return false;
    }

    @Override
    protected AbstractServer buildProgrammaticApplication(File keyFile, int port) {
        WebappContext context = new WebappContext("JWT Auth Integration Tests", "/");

        // Add the JAX-RS application servlet
        ServletRegistration registration =
                context.addServlet(ServletContainer.class.getCanonicalName(), ServletContainer.class);
        registration.addMapping("/*");
        registration.setInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS,
                                      HelloWorldApplication.class.getCanonicalName());

        ServletContextListener listener = new JaxRs3AutomatedAuthConfigurationListener();
        context.addListener(listener);
        context.addContextInitParameter(ConfigurationParameters.PARAM_SECRET_KEY, keyFile.getAbsolutePath());

        URI baseUri = null;
        try {
            baseUri = new URI(String.format("http://localhost:%d", port));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, false);
        return new GrizzlyJaxRS3Server(server, context, port);
    }

    @Override
    protected AbstractServer buildWebXmlApplication(int port, String appName) throws IOException {
        ensureWebAppExists(appName);

        Server server = new Server(port);
        WebAppContext webApp = new WebAppContext(new PathResource(new File("src/test/apps/" + appName)), "/");
        server.setHandler(webApp);

        return new Jetty11JaxRS3Server(server, port);
    }
}
