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
package io.telicent.servlet.auth.jwt.verification.jwks;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.security.JwkSet;
import io.telicent.servlet.auth.jwt.configuration.oidc.OidcVerificationProvider;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.ee9.servlet.ServletContextHandler;
import org.eclipse.jetty.ee9.servlet.ServletHandler;
import org.eclipse.jetty.ee9.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A minimalist server application that provides an {@value OidcVerificationProvider#WELL_KNOWN_OPENID_CONFIGURATION}
 * endpoint pointing back to its own JWKS endpoint allowing us to test Open ID Connect configuration discovery in a test
 * environment
 */
public class OidcServer extends JwksServer {
    private final AtomicInteger counter = new AtomicInteger();

    /**
     * Creates a new OpenID Connect discovery enabled test key server
     *
     * @param port Port
     * @param jwks JWKS
     */
    public OidcServer(int port, JwkSet jwks) {
        super(port, jwks);
    }

    /**
     * Gets the OpenID Connect well known URL for configuration discovery
     *
     * @return Well known configuration URL
     */
    public String getConfigurationUrl() {
        if (this.server != null) {
            return String.format("http://localhost:%d%s", this.port,
                                 OidcVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION);
        } else {
            return null;
        }
    }

    /**
     * Gets an alternative non-standard URL at which OpenID Connect configuration can also be discovered
     *
     * @return Non-standard configuration URL
     */
    public String getNonStandardConfigurationUrl() {
        if (this.server != null) {
            return String.format("http://localhost:%d/non-standard-configuration", this.port);
        } else {
            return null;
        }
    }

    /**
     * Gets an OpenID Connect configuration discovery endpoint that always returns empty configuration
     *
     * @return Empty configuration URL
     */
    public String getEmptyConfigurationUrl() {
        if (this.server != null) {
            return String.format("http://localhost:%d/empty-configuration", this.port);
        } else {
            return null;
        }
    }

    /**
     * Gets an OpenID Connect configuration discovery URL that always returns a 404 Not Found response
     *
     * @return Not found configuration URL
     */
    public String getNotFoundConfigurationUrl() {
        if (this.server != null) {
            return String.format("http://localhost:%d/not-found-configuration", this.port);
        } else {
            return null;
        }
    }

    /**
     * Gets how many times the OpenID configuration has been requested
     *
     * @return Number of configuration discovery requests
     */
    public int getDiscoveryRequestsCount() {
        return this.counter.get();
    }

    /**
     * Resets the counter of OpenID configuration discovery requests
     */
    public void resetDiscoveryRequestsCount() {
        this.counter.set(0);
    }

    @Override
    public void start() throws Exception {
        if (this.server == null) {
            this.server = new Server(this.port);

            ServletContextHandler handler = new ServletContextHandler();
            handler.setContextPath("/");
            this.server.setHandler(handler);

            ServletHandler servletHandler = new ServletHandler();
            handler.setHandler(servletHandler);
            addJwksServlet(servletHandler);

            addConfigurationDiscoveryServlets(servletHandler);

            ServletHolder holder = new ServletHolder();
            holder.setServlet(new EmptyConfigurationServlet(this.counter));
            servletHandler.addServletWithMapping(holder, "/empty-configuration");

            this.server.start();
        } else if (!this.server.isStarted()) {
            this.server.start();
        }
    }

    private void addConfigurationDiscoveryServlets(ServletHandler servletHandler) {
        ServletHolder holder = new ServletHolder();
        holder.setServlet(new ConfigurationServlet(this.getUrl(), this.counter));
        servletHandler.addServletWithMapping(holder, OidcVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION);
        servletHandler.addServletWithMapping(holder, "/non-standard-configuration");
    }

    @Override
    public void stop() throws Exception {
        if (this.server != null) {
            this.server.stop();
        }
    }

    protected static class ConfigurationServlet extends HttpServlet {
        private final String jwksUri;
        private final AtomicInteger counter;
        private static final ObjectMapper JSON = new ObjectMapper();

        public ConfigurationServlet(String jwksUri, AtomicInteger attemptCounter) {
            this.jwksUri = jwksUri;
            this.counter = attemptCounter;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            this.counter.incrementAndGet();

            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            Map<String, Object> configuration =
                    Map.of("jwks_uri", jwksUri, "issuer", "https://example.org", "userinfo_endpoint",
                           "https://example.org/userinfo", "foo", "bar");
            JSON.writeValue(resp.getWriter(), configuration);
        }
    }

    protected static class EmptyConfigurationServlet extends HttpServlet {

        private final AtomicInteger counter;

        public EmptyConfigurationServlet(AtomicInteger attemptCounter) {
            this.counter = attemptCounter;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            this.counter.incrementAndGet();

            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{}");
        }
    }
}
