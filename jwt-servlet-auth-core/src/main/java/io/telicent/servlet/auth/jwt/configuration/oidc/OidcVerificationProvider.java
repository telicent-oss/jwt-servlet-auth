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
package io.telicent.servlet.auth.jwt.configuration.oidc;

import io.jsonwebtoken.Jwts;
import io.telicent.servlet.auth.jwt.configuration.ConfigurationParameters;
import io.telicent.servlet.auth.jwt.configuration.DefaultVerificationProvider;
import io.telicent.servlet.auth.jwt.configuration.Utils;
import io.telicent.servlet.auth.jwt.verification.JwtVerifier;
import io.telicent.servlet.auth.jwt.verification.SignedJwtVerifier;
import io.telicent.servlet.auth.jwt.verification.jwks.CachedJwksKeyLocator;
import io.telicent.servlet.auth.jwt.verification.jwks.OidcDiscoveryLocator;
import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A verification provider that automatically discovers the JWKS URL from an OpenID Connect compliant servers
 * {@value #WELL_KNOWN_OPENID_CONFIGURATION} endpoint specified via the
 * {@value ConfigurationParameters#PARAM_OIDC_PROVIDER_URL} configuration parameter
 */
public class OidcVerificationProvider extends DefaultVerificationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(OidcVerificationProvider.class);

    /**
     * The {@code /.well-known/} path that is used for some automatic configuration patterns
     */
    public static final String WELL_KNOWN_PATH = "/.well-known/";
    /**
     * The {@code openid-configuration} path that is used for OpenID Connect configuration discovery
     */
    public static final String OPENID_CONFIGURATION = "openid-configuration";
    /**
     * The well known URL path for OpenID Connect Configuration discovery
     */
    public static final String WELL_KNOWN_OPENID_CONFIGURATION = WELL_KNOWN_PATH + OPENID_CONFIGURATION;

    /**
     * Supported parameters for this verification provider
     */
    public static String[] OPENID_PARAMETERS = new String[] {
            ConfigurationParameters.PARAM_OIDC_PROVIDER_URL,
            ConfigurationParameters.PARAM_OIDC_RETRY_INTERVAL,
            ConfigurationParameters.PARAM_JWKS_CACHE_KEYS_FOR,
            ConfigurationParameters.PARAM_ALLOWED_CLOCK_SKEW
    };

    @Override
    public boolean configure(Function<String, String> paramSupplier, Consumer<JwtVerifier> verifierConsumer) {
        Map<String, String> parameters =
                DefaultVerificationProvider.prepareParameters(paramSupplier, OPENID_PARAMETERS);
        if (!parameters.containsKey(ConfigurationParameters.PARAM_OIDC_PROVIDER_URL)) {
            LOGGER.info(
                    "No relevant parameters provided to allow OIDC auto-configuration JWT verifier configuration, authentication will not be possible unless the verifier is separately configured.");
        } else {
            Integer retryInterval = Utils.parseParameter(parameters, ConfigurationParameters.PARAM_OIDC_RETRY_INTERVAL,
                                                         Integer::parseInt,
                                                         ConfigurationParameters.DEFAULT_OIDC_RETRY_INTERVAL);
            Integer cacheKeysFor = Utils.parseParameter(parameters, ConfigurationParameters.PARAM_JWKS_CACHE_KEYS_FOR,
                                                        Integer::parseInt,
                                                        ConfigurationParameters.DEFAULT_JWKS_CACHE_KEYS_FOR);
            String rawDiscoveryUri = parameters.get(ConfigurationParameters.PARAM_OIDC_PROVIDER_URL);
            URI discoveryUri = OidcVerificationProvider.prepareDiscoveryUri(rawDiscoveryUri);
            LOGGER.info(
                    "Resolved raw OpenID Connect configuration discovery URI {} to {}, if this is not correct ensure your configuration provides the full URI with the {} suffix",
                    rawDiscoveryUri, discoveryUri.toString(), OidcVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION);
            CachedJwksKeyLocator locator = new CachedJwksKeyLocator(
                    new OidcDiscoveryLocator(discoveryUri, Duration.ofSeconds(retryInterval)),
                    Duration.ofMinutes(cacheKeysFor));
            verifierConsumer.accept(create(parameters, Jwts.parser().keyLocator(locator),
                                           SignedJwtVerifier.debugStringForLocator(locator)));
            return true;
        }
        return false;
    }

    /**
     * Given a raw OpenID Connect Discovery URL ensure it ends with the expected
     * {@value #WELL_KNOWN_OPENID_CONFIGURATION} suffix
     *
     * @param rawDiscoveryUri Raw discovery URL
     * @return Discovery URI
     */
    public static URI prepareDiscoveryUri(String rawDiscoveryUri) {
        if (!Strings.CS.endsWith(rawDiscoveryUri, WELL_KNOWN_OPENID_CONFIGURATION)) {
            LOGGER.info("Adding suffix " + WELL_KNOWN_OPENID_CONFIGURATION + " to raw discovery URI {}",
                        rawDiscoveryUri);
            URI baseUri = URI.create(rawDiscoveryUri);
            if (!rawDiscoveryUri.endsWith(WELL_KNOWN_PATH)) {
                // Case of /.well-known/ in the path but not right at the end, leave the existing /.well-known/ intact
                // if represents the penultimate path segment
                if (rawDiscoveryUri.contains(WELL_KNOWN_PATH) && baseUri.getPath()
                                                                        .lastIndexOf("/") + 1 >= baseUri.getPath()
                                                                                                        .lastIndexOf(
                                                                                                                WELL_KNOWN_PATH) + WELL_KNOWN_PATH.length()) {
                    // Already contains a /.well-known/ as the penultimate section so just append the
                    // openid-configuration portion
                    return baseUri.resolve("./" + OPENID_CONFIGURATION);
                } else {
                    // Otherwise append the full /.well-known/openid-configuration
                    return baseUri.resolve("./" + WELL_KNOWN_OPENID_CONFIGURATION);
                }
            } else {
                // Already ends with /.well-known/ so just append the openid-configuration portion
                return baseUri.resolve("./" + OPENID_CONFIGURATION);
            }
        }

        // Already has the correct suffix so nothing to do
        return URI.create(rawDiscoveryUri);
    }

    @Override
    public int priority() {
        return 100;
    }
}
