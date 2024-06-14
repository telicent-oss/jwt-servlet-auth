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
package io.telicent.servlet.auth.jwt;

import io.telicent.servlet.auth.jwt.configuration.AutomatedConfiguration;
import io.telicent.servlet.auth.jwt.configuration.FrozenFilterConfiguration;
import io.telicent.servlet.auth.jwt.configuration.RuntimeConfigurationAdaptor;
import io.telicent.servlet.auth.jwt.errors.AuthenticationConfigurationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * An abstract configurable JWT authentication filter
 *
 * @param <TRequest>  Request
 * @param <TResponse> Response
 */
public abstract class AbstractConfigurableJwtAuthFilter<TRequest, TResponse>
        extends AbstractJwtAuthFilter<TRequest, TResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigurableJwtAuthFilter.class);

    /**
     * A filter configuration holder
     */
    protected FrozenFilterConfiguration<TRequest, TResponse> config = new FrozenFilterConfiguration<>();

    /**
     * The last authenticated request.
     * <p>
     * <strong>NOT THREAD SAFE</strong>, intended for unit test usage only
     * </p>
     */
    protected TRequest lastAuthenticatedRequest;

    /**
     * Called to automatically configure the filter from the available configuration parameters
     *
     * @param adaptor Configuration adaptor
     */
    protected void configure(RuntimeConfigurationAdaptor adaptor) {
        AutomatedConfiguration.configure(adaptor);

        // Take the configuration we've just done (if any) and freeze it in our config
        // It remains in the attributes in case the filter gets disposed of and recreated at any point
        // We intentionally don't throw the errors upwards here rather we just log them because the user may be choosing
        // to configure the filter some other way e.g. ServletContextListener
        try {
            this.config.tryFreezeExclusionsConfiguration(
                    adaptor.getAttribute(JwtServletConstants.ATTRIBUTE_PATH_EXCLUSIONS));
        } catch (AuthenticationConfigurationError e) {
            LOGGER.error(e.getMessage());
        }
        try {
            this.config.tryFreezeEngineConfiguration(adaptor.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE),
                                                     this.getDefaultEngine());
        } catch (AuthenticationConfigurationError e) {
            LOGGER.error(e.getMessage());
        }
        try {
            this.config.tryFreezeVerifierConfiguration(adaptor.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        } catch (AuthenticationConfigurationError e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Gets an attribute
     *
     * @param request   Request
     * @param attribute Attribute name
     * @return Attribute value, or {@code null} if no such attribute exists
     */
    protected abstract Object getAttribute(TRequest request, String attribute);

    /**
     * Gets the request path
     *
     * @param request Request
     * @return Path
     */
    protected abstract String getPath(TRequest request);

    /**
     * Gets the default authentication engine to use if an engine has not been explicitly configured
     *
     * @return Default authentication engine
     */
    protected abstract JwtAuthenticationEngine<TRequest, TResponse> getDefaultEngine();

    /**
     * Gets the response status
     *
     * @param response Response
     * @return Status
     */
    protected abstract int getStatus(TResponse response);

    /**
     * Performs the actual authentication filtering
     *
     * @param request   Request
     * @param response  Response
     * @param onSuccess Function that is called upon successful authentication (or authentication being determined to be
     *                  unnecessary)
     */
    public final void doFilter(TRequest request, TResponse response, BiConsumer<TRequest, TResponse> onSuccess) {
        this.lastAuthenticatedRequest = null;
        MDC.put(JwtLoggingConstants.MDC_JWT_USER, null);

        if (this.config.getExclusions() == null) {
            this.config.tryFreezeExclusionsConfiguration(
                    this.getAttribute(request, JwtServletConstants.ATTRIBUTE_PATH_EXCLUSIONS));
        }
        Function<String, Object> attributeGetter = x -> this.getAttribute(request, x);
        this.config.warnIfModificationAttempted(JwtServletConstants.ATTRIBUTE_PATH_EXCLUSIONS, attributeGetter,
                                                this.config.getExclusions());
        if (this.isExcludedPath(this.getPath(request), this.config.getExclusions())) {
            // If the path is excluded this filter doesn't apply to the request, and we treat it as a success
            onSuccess.accept(request, response);
            return;
        }

        if (this.config.getEngine() == null) {
            this.config.tryFreezeEngineConfiguration(this.getAttribute(request, JwtServletConstants.ATTRIBUTE_JWT_ENGINE),
                                                     this.getDefaultEngine());
        }
        this.config.warnIfModificationAttempted(JwtServletConstants.ATTRIBUTE_JWT_ENGINE, attributeGetter,
                                                this.config.getEngine());
        if (this.config.getVerifier() == null) {
            this.config.tryFreezeVerifierConfiguration(
                    this.getAttribute(request, JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        }
        this.config.warnIfModificationAttempted(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER, attributeGetter,
                                                this.config.getVerifier());

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Using JWT Authentication engine {} with JWT verifier {}", this.config.getEngine(),
                         this.config.getVerifier());
        }

        TRequest authenticatedRequest =
                this.config.getEngine().authenticate(request, response, this.config.getVerifier());
        if (authenticatedRequest != null) {
            this.lastAuthenticatedRequest = authenticatedRequest;
            onSuccess.accept(authenticatedRequest, response);
        } else {
            LOGGER.warn("Request to {} rejected as unauthenticated with HTTP {}",
                        this.config.getEngine().getRequestUrl(request), getStatus(response));
        }
    }
}
