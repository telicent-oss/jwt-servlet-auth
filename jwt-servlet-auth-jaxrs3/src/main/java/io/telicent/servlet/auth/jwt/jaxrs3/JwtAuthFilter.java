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

import io.telicent.servlet.auth.jwt.*;
import io.telicent.servlet.auth.jwt.configuration.FrozenFilterConfiguration;
import jakarta.annotation.Priority;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.function.Function;

/**
 * A server side JAX-RS request filter that requires requests include a valid JSON Web Token (JWT) in order to proceed
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter extends AbstractJwtAuthFilter<ContainerRequestContext, ContainerResponseContext>
        implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthFilter.class);

    private FrozenFilterConfiguration<ContainerRequestContext, ContainerResponseContext> config =
            new FrozenFilterConfiguration<>();

    @Context
    private ServletContext servletContext;

    /**
     * Used by unit tests to set the context for testing purposes, usually the context is injected by the JAX-RS server
     * runtime
     *
     * @param context Servlet Context
     */
    final void setContext(ServletContext context) {
        this.servletContext = context;
    }

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        MDC.put(LoggingConstants.MDC_JWT_USER, null);

        if (this.config.getExclusions() == null) {
            this.config.tryFreezeExclusionsConfiguration(
                    this.servletContext.getAttribute(ServletConstants.ATTRIBUTE_PATH_EXCLUSIONS));
        }
        Function<String, Object> attributeGetter = x -> this.servletContext.getAttribute(x);
        this.config.warnIfModificationAttempted(ServletConstants.ATTRIBUTE_PATH_EXCLUSIONS, attributeGetter,
                                                this.config.getExclusions());
        if (this.isExcludedPath("/" + request.getUriInfo().getPath(), this.config.getExclusions())) {
            return;
        }

        if (this.config.getEngine() == null) {
            this.config.tryFreezeEngineConfiguration(
                    this.servletContext.getAttribute(ServletConstants.ATTRIBUTE_JWT_ENGINE),
                    new JaxRs3JwtAuthenticationEngine());
        }
        this.config.warnIfModificationAttempted(ServletConstants.ATTRIBUTE_JWT_ENGINE, attributeGetter,
                                                this.config.getEngine());
        if (this.config.getVerifier() == null) {
            this.config.tryFreezeVerifierConfiguration(
                    this.servletContext.getAttribute(ServletConstants.ATTRIBUTE_JWT_VERIFIER));
        }
        this.config.warnIfModificationAttempted(ServletConstants.ATTRIBUTE_JWT_VERIFIER, attributeGetter,
                                                this.config.getVerifier());

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Using JWT Authentication engine {} with JWT verifier {}", this.config.getEngine(),
                         this.config.getVerifier());
        }

        if (this.config.getEngine().authenticate(request, null, this.config.getVerifier()) == null) {
            LOGGER.warn("Request to {} rejected as unauthenticated", request.getUriInfo().getRequestUri().toString());
        }
    }
}
