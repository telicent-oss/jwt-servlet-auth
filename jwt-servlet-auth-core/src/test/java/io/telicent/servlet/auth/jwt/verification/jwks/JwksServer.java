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

import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.JwkSet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.ee9.servlet.ServletContextHandler;
import org.eclipse.jetty.ee9.servlet.ServletHandler;
import org.eclipse.jetty.ee9.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

import java.io.IOException;

/**
 * A minimalist server application that can serve a JWKS
 */
public class JwksServer {

    protected final JwkSet jwks;
    protected final int port;

    protected Server server;

    public JwksServer(int port, JwkSet jwks) {
        this.jwks = jwks;
        this.port = port;
    }

    public String getUrl() {
        if (this.server != null) {
            return String.format("http://localhost:%d/jwks.json", this.port);
        } else {
            return null;
        }
    }

    public void start() throws Exception {
        if (this.server == null) {
            this.server = new Server(this.port);

            ServletContextHandler handler = new ServletContextHandler();
            handler.setContextPath("/");
            this.server.setHandler(handler);

            ServletHandler servletHandler = new ServletHandler();
            ServletHolder holder = new ServletHolder();
            holder.setServlet(new JwksServlet(this.jwks));
            servletHandler.addServletWithMapping(holder, "/jwks.json");
            handler.setHandler(servletHandler);

            this.server.start();
        }
    }

    public void stop() throws Exception {
        if (this.server != null) {
            this.server.stop();
            this.server.destroy();
        }
    }

    protected static class JwksServlet extends HttpServlet {
        protected final JwkSet jwks;

        public JwksServlet(JwkSet jwks) {
            this.jwks = jwks;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            if (this.jwks != null) {
                new JacksonSerializer<>().serialize(this.jwks, resp.getOutputStream());
            } else {
                resp.getOutputStream().println("{}");
            }
        }
    }
}
