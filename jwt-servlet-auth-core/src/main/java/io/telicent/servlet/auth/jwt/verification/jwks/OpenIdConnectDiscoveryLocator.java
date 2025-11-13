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
import io.jsonwebtoken.security.InvalidKeyException;
import io.telicent.servlet.auth.jwt.configuration.ConfigurationParameters;
import io.telicent.servlet.auth.jwt.configuration.oidc.OpenIdConnectVerificationProvider;
import io.telicent.servlet.auth.jwt.configuration.oidc.OpenIdDiscoveryConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

/**
 * A JWKS locator that discovers the JWKS URL via an Open ID Connect configuration endpoint
 */
public class OpenIdConnectDiscoveryLocator extends AbstractJwksLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenIdConnectDiscoveryLocator.class);

    private final URI discoveryUri;
    private URI jwksUri = null;
    private long lastDiscoveryAttemptAt = Long.MIN_VALUE;
    private final Duration retryInterval;
    private boolean nonStandardWarning = false;

    /**
     * Creates a new OpenID Connect discovery locator
     *
     * @param discoveryUri Discovery URI, this should be the OpenID Connect configuration endpoint, which is usually the
     *                     {@value OpenIdConnectVerificationProvider#WELL_KNOWN_OPENID_CONFIGURATION} endpoint of your
     *                     OpenID Connect compliant authentication server
     */
    public OpenIdConnectDiscoveryLocator(URI discoveryUri) {
        this(createDefaultClient(), discoveryUri, null);
    }

    /**
     * Creates a new OpenID Connect discovery locator
     *
     * @param discoveryUri  Discovery URI, this should be the OpenID Connect configuration endpoint, which is usually
     *                      the {@value OpenIdConnectVerificationProvider#WELL_KNOWN_OPENID_CONFIGURATION} endpoint of
     *                      your OpenID Connect compliant authentication server
     * @param retryInterval Retry interval to wait before re-attempting configuration discovery if a previous attempt
     *                      failed
     */
    public OpenIdConnectDiscoveryLocator(URI discoveryUri, Duration retryInterval) {
        this(createDefaultClient(), discoveryUri, retryInterval);
    }

    /**
     * Creates a new OpenID Connect discovery locator
     *
     * @param client        HTTP Client to use
     * @param discoveryUri  Discovery URI, this should be the OpenID Connect configuration endpoint, which is usually
     *                      the {@value OpenIdConnectVerificationProvider#WELL_KNOWN_OPENID_CONFIGURATION} endpoint of
     *                      your OpenID Connect compliant authentication server
     * @param retryInterval Retry interval to wait before re-attempting configuration discovery if a previous attempt
     *                      failed
     */
    public OpenIdConnectDiscoveryLocator(HttpClient client, URI discoveryUri, Duration retryInterval) {
        super(client);
        this.discoveryUri =
                Objects.requireNonNull(discoveryUri, "Open ID Connect configuration Discovery URL cannot be null");
        this.nonStandardWarning = !Strings.CS.endsWith(discoveryUri.toString(),
                                                      OpenIdConnectVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION);
        this.retryInterval = retryInterval != null ? retryInterval :
                             Duration.ofSeconds(ConfigurationParameters.DEFAULT_OIDC_RETRY_INTERVAL);
        if (this.retryInterval.isNegative()) {
            throw new IllegalArgumentException("retryInterval cannot be negative");
        }
    }

    @Override
    protected URI getJwksURI() {
        if (this.jwksUri != null) {
            return this.jwksUri;
        }

        // Don't DoS the discovery endpoint if we can't resolve it, wait at least the retry interval before attempting
        // discovery again
        if (this.lastDiscoveryAttemptAt != Long.MIN_VALUE) {
            Duration elapsed = Duration.ofMillis(System.currentTimeMillis() - this.lastDiscoveryAttemptAt);
            if (elapsed.compareTo(this.retryInterval) < 0) {
                throw new InvalidKeyException(
                        "Unable to resolve JWKS URL via OpenID Connect configuration discovery and retry interval (" + this.retryInterval + ") has not yet elapsed");
            }
        }

        // Issue a warning once, and once only, if the configured Discovery URI is non-standard
        if (this.nonStandardWarning) {
            LOGGER.warn(
                    "Non-standard OpenID Connect discovery endpoint in-use (does not end with expected " + OpenIdConnectVerificationProvider.WELL_KNOWN_OPENID_CONFIGURATION + " suffix)");
            this.nonStandardWarning = false;
        }

        try {
            // Make a GET request to obtain the OpenID Connect configuration
            HttpRequest request = HttpRequest.newBuilder(discoveryUri).build();
            this.lastDiscoveryAttemptAt = System.currentTimeMillis();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                // Assuming an OK response parse it and extract the bit of configuration we care about
                OpenIdDiscoveryConfiguration configuration =
                        new ObjectMapper().readValue(response.body(), OpenIdDiscoveryConfiguration.class);
                if (StringUtils.isNotBlank(configuration.getJwksUri())) {
                    LOGGER.info("Obtained OpenID Connect configuration from {} provided JWKS URL {}", discoveryUri,
                                configuration.getJwksUri());
                    this.jwksUri = URI.create(configuration.getJwksUri());
                } else {
                    LOGGER.warn("Obtained OpenID Connect configuration from {} did not specify a jwks_uri",
                                discoveryUri);
                }
            } else {
                LOGGER.warn("Obtaining OpenID Connect configuration from {} failed with HTTP status {}", discoveryUri,
                            response.statusCode());
            }

        } catch (Throwable e) {
            LOGGER.warn("Failed to obtain OpenID Connect discovery configuration: {}", e.getMessage());
        }

        if (this.jwksUri == null) {
            throw new InvalidKeyException("Unable to resolve JWKS URL via OpenID Connect configuration discovery");
        } else {
            return this.jwksUri;
        }
    }

    @Override
    public String toString() {
        return "OpenIdDiscoveryLocator{discoveryUrl=" + this.discoveryUri.toString() + ", jwksUrl=" + (
                this.jwksUri != null ? this.jwksUri.toString() :
                "<not yet discovered>") + ", retryInterval=" + this.retryInterval.toString() + "}";
    }
}
