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

import io.telicent.servlet.auth.jwt.JwtHttpConstants;
import io.telicent.servlet.auth.jwt.JwtAuthenticationEngine;
import io.telicent.servlet.auth.jwt.sources.HeaderSource;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An abstract base class for use by concrete {@link EngineProvider} implementations
 */
public abstract class AbstractHeaderBasedEngineProvider implements EngineProvider {

    /**
     * Tries to configure the header sources
     *
     * @param paramSupplier Parameter supplier
     * @return Header sources, or {@code null} if no configuration provided
     */
    protected List<HeaderSource> configureHeaders(Function<String, String> paramSupplier) {
        List<HeaderSource> sources = new ArrayList<>();

        if (Utils.parseParameter(paramSupplier.apply(ConfigurationParameters.PARAM_USE_DEFAULT_HEADERS), Boolean::parseBoolean, false)) {
            sources.addAll(JwtHttpConstants.DEFAULT_HEADER_SOURCES);
        }
        List<String> headers =
                Utils.parseParameter(paramSupplier.apply(ConfigurationParameters.PARAM_HEADER_NAMES),
                                     AbstractHeaderBasedEngineProvider::parseList, null);
        if (headers != null) {
            List<String> prefixes =
                    Utils.parseParameter(paramSupplier.apply(ConfigurationParameters.PARAM_HEADER_PREFIXES),
                                         AbstractHeaderBasedEngineProvider::parseList,
                                         null);
            for (int i = 0; i < headers.size(); i++) {
                String prefix = prefixes != null && i < prefixes.size() ? prefixes.get(i) : null;
                sources.add(new HeaderSource(headers.get(i), prefix));
            }
        }

        return sources.isEmpty() ? null : sources;
    }

    /**
     * Tries to configure the username claims
     *
     * @param paramSupplier Parameter supplier
     * @return Username claims, or {@code null} if no configuration provided
     */
    protected List<String> configureUsernameClaims(Function<String, String> paramSupplier) {
        return Utils.parseParameter(paramSupplier.apply(ConfigurationParameters.PARAM_USERNAME_CLAIMS),
                                    AbstractHeaderBasedEngineProvider::parseList, null);
    }

    /**
     * Tries to configure the realm
     *
     * @param paramSupplier Parameter supplier
     * @return Realm, or {@code null} if no configuration provided
     */
    protected String configureRealm(Function<String, String> paramSupplier) {
        return paramSupplier.apply(ConfigurationParameters.PARAM_REALM);
    }

    /**
     * Utility method for splitting a comma seperated string into a list.
     * @param value Comma-separated string.
     * @return A List of values
     */
    protected static List<String> parseList(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Arrays.stream(value.split(",")).toList();
    }

    @Override
    public <TRequest, TResponse> boolean configure(Function<String, String> paramSupplier,
                                                   Consumer<JwtAuthenticationEngine<TRequest, TResponse>> jwtAuthenticationEngineConsumer) {
        List<HeaderSource> headerSources = this.configureHeaders(paramSupplier);
        if (headerSources == null) {
            return false;
        }
        String realm = this.configureRealm(paramSupplier);
        List<String> usernameClaims = this.configureUsernameClaims(paramSupplier);

        try {
            JwtAuthenticationEngine<TRequest, TResponse> engine = createEngine(headerSources, realm, usernameClaims);
            if (engine == null) {
                return false;
            }
            jwtAuthenticationEngineConsumer.accept(engine);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * Creates the actual engine implementation
     *
     * @param headerSources  Header Sources
     * @param realm          Realm
     * @param usernameClaims Username claims
     * @param <TRequest>     Request type
     * @param <TResponse>    Response type
     * @return JWT Authentication Engine
     */
    protected abstract <TRequest, TResponse> JwtAuthenticationEngine<TRequest, TResponse> createEngine(
            List<HeaderSource> headerSources, String realm, List<String> usernameClaims);
}
