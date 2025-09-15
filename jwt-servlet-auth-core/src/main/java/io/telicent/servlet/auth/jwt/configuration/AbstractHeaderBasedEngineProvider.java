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
import org.apache.commons.lang3.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        if (Utils.parseParameter(paramSupplier.apply(ConfigurationParameters.PARAM_USE_DEFAULT_HEADERS),
                                 Boolean::parseBoolean, false)) {
            sources.addAll(JwtHttpConstants.DEFAULT_HEADER_SOURCES);
        }
        List<String> headers = Utils.parseParameter(paramSupplier.apply(ConfigurationParameters.PARAM_HEADER_NAMES),
                                                    AbstractHeaderBasedEngineProvider::parseList, null);
        if (headers != null) {
            List<String> prefixes =
                    Utils.parseParameter(paramSupplier.apply(ConfigurationParameters.PARAM_HEADER_PREFIXES),
                                         AbstractHeaderBasedEngineProvider::parseList, null);
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
    protected List<ClaimPath> configureUsernameClaims(Function<String, String> paramSupplier) {
        List<String> rawClaims =
                Utils.parseParameter(paramSupplier.apply(ConfigurationParameters.PARAM_USERNAME_CLAIMS),
                                     AbstractHeaderBasedEngineProvider::parseList, null);
        if (rawClaims == null) {
            return null;
        }
        return rawClaims.stream().map(AbstractHeaderBasedEngineProvider::parseClaimPath).collect(Collectors.toList());
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
     * Tries to configure the roles claim
     *
     * @param paramSupplier Parameter supplier
     * @return Roles claim, or {@code null} if no configuration provided
     */
    protected ClaimPath configureRolesClaim(Function<String, String> paramSupplier) {
        return Utils.parseParameter(paramSupplier.apply(ConfigurationParameters.PARAM_ROLES_CLAIM),
                                    AbstractHeaderBasedEngineProvider::parseClaimPath, null);
    }

    /**
     * Parses a claim path from a raw configuration value
     * @param value Raw value
     * @return Claim path, or {@code null} if not parseable
     */
    protected static ClaimPath parseClaimPath(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        // Split into path elements
        List<String> pathElements = parseDottedPath(value);
        if (pathElements == null) {
            return null;
        }
        return ClaimPath.of(pathElements);
    }

    /**
     * Utility method for splitting a dot separated path into an array
     *
     * @param value Dot separated path
     * @return Path array
     */
    protected static List<String> parseDottedPath(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        // Split into elements based on the . character
        // Remove any empty elements and strip excess whitespace around the elements
        return Arrays.stream(value.split("\\."))
                     .filter(StringUtils::isNotBlank)
                     .map(StringUtils::strip)
                     .collect(Collectors.toList());
    }

    /**
     * Utility method for splitting a comma separated string into a list.
     *
     * @param value Comma-separated string.
     * @return A List of values
     */
    protected static List<String> parseList(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        // NB - We intentionally DO NOT use StringUtils.split() here as that eliminates empty elements and for some
        //      configuration we rely upon two separately configured lists being aligned (including their empty
        //      elements)
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
        List<ClaimPath> usernameClaims = this.configureUsernameClaims(paramSupplier);
        ClaimPath rolesClaim = this.configureRolesClaim(paramSupplier);

        try {
            JwtAuthenticationEngine<TRequest, TResponse> engine =
                    createEngine(headerSources, realm, usernameClaims, rolesClaim);
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
     * @param rolesClaim     Roles claim
     * @param <TRequest>     Request type
     * @param <TResponse>    Response type
     * @return JWT Authentication Engine
     */
    protected abstract <TRequest, TResponse> JwtAuthenticationEngine<TRequest, TResponse> createEngine(
            List<HeaderSource> headerSources, String realm, List<ClaimPath> usernameClaims, ClaimPath rolesClaim);
}
