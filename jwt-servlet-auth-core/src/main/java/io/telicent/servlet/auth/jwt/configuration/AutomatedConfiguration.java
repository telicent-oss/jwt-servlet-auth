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
package io.telicent.servlet.auth.jwt.configuration;

import io.telicent.servlet.auth.jwt.PathExclusion;
import io.telicent.servlet.auth.jwt.ServletConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for automated configuration
 */
public class AutomatedConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutomatedConfiguration.class);

    private AutomatedConfiguration() {
    }

    /**
     * Automatically configures the JWT Verifier and Engine based on the available configuration parameters
     * <p>
     * If any of the configurable items have already been configured for the given configuration adaptor then we do not
     * try to configure them again.
     * </p>
     *
     * @param adaptor Configuration adaptor
     */
    public static void configure(RuntimeConfigurationAdaptor adaptor) {
        boolean allowMultiple =
                Utils.parseParameter(adaptor.getParameter(ConfigurationParameters.PARAM_ALLOW_MULTIPLE_CONFIGS),
                                     Boolean::parseBoolean, false);

        // Configure the JWT Verifier
        if (adaptor.getAttribute(ServletConstants.ATTRIBUTE_JWT_VERIFIER) == null || allowMultiple) {
            VerificationFactory.configure(x -> adaptor.getParameter(x),
                                          v -> adaptor.setAttribute(ServletConstants.ATTRIBUTE_JWT_VERIFIER, v));
        } else {
            LOGGER.warn("JWT Verifier already configured, skipping additional attempt to automatically configure.");
        }

        // Configure the Path Exclusions
        if (adaptor.getAttribute(ServletConstants.ATTRIBUTE_PATH_EXCLUSIONS) == null || allowMultiple) {
            String rawExclusions = adaptor.getParameter(ConfigurationParameters.PARAM_PATH_EXCLUSIONS);
            if (StringUtils.isNotBlank(rawExclusions)) {
                adaptor
                        .setAttribute(ServletConstants.ATTRIBUTE_PATH_EXCLUSIONS,
                                      PathExclusion.parsePathPatterns(rawExclusions));
            }
        } else {
            LOGGER.warn("Path Exclusions already configured, skipping additional attempt to automatically configure.");
        }

        // Configure the Authentication Engine
        if (adaptor.getAttribute(ServletConstants.ATTRIBUTE_JWT_ENGINE) == null || allowMultiple) {
            EngineFactory.configure(x -> adaptor.getParameter(x),
                                    e -> adaptor.setAttribute(ServletConstants.ATTRIBUTE_JWT_ENGINE, e));
        } else {
            LOGGER.warn(
                    "JWT Authentication Engine already configured, skipping additional attempt to automatically configure.");
        }
    }
}
