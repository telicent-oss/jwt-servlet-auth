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
package io.telicent.servlet.auth.jwt.verifier.aws;

import io.jsonwebtoken.security.Jwk;
import io.jsonwebtoken.security.JwkSet;
import io.telicent.servlet.auth.jwt.verification.jwks.JwksServer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.ee9.servlet.ServletContextHandler;
import org.eclipse.jetty.ee9.servlet.ServletHandler;
import org.eclipse.jetty.ee9.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

import java.io.IOException;
import java.util.Base64;

/**
 * A mock ELB Key Server for testing
 */
public class AwsElbServer extends JwksServer {
    public AwsElbServer(int port, JwkSet jwks) {
        super(port, jwks);
    }

    @Override
    public void start() throws Exception {
        if (this.server == null) {
            this.server = new Server(this.port);

            ServletContextHandler handler = new ServletContextHandler();
            handler.setContextPath("/");
            this.server.setHandler(handler);

            ServletHandler servletHandler = new ServletHandler();
            ServletHolder holder = new ServletHolder();
            holder.setServlet(new PemKeyServlet(this.jwks));
            servletHandler.addServletWithMapping(holder, "/*");
            handler.setHandler(servletHandler);

            this.server.start();
        }
    }

    @Override
    public String getUrl() {
        if (this.server != null) {
            return String.format("http://localhost:%d", this.port);
        } else {
            return null;
        }
    }

    private static final class PemKeyServlet extends JwksServlet {

        public PemKeyServlet(JwkSet jwks) {
            super(jwks);
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String keyId = StringUtils.stripStart(req.getRequestURI(), "/");

            Jwk<?> jwk = this.jwks.getKeys()
                                  .stream()
                                  .filter(k -> StringUtils.equals(k.getId(), keyId))
                                  .findFirst()
                                  .orElse(null);

            if (jwk == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/plain");
            resp.getOutputStream().println("-----BEGIN PUBLIC KEY-----");
            resp.getOutputStream().println(Base64.getEncoder().encodeToString(jwk.toKey().getEncoded()));
            resp.getOutputStream().println("-----END PUBLIC KEY-----");
        }
    }
}
