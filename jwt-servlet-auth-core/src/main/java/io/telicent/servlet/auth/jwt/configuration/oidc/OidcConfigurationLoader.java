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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

/**
 * A helper for loading OpenID Connect configuration from the configuration discovery endpoint
 */
public class OidcConfigurationLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(OidcConfigurationLoader.class);

    private final HttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates a new OpenID Connect configuration discovery loader using the given HTTP Client
     *
     * @param httpClient HTTP Client
     */
    public OidcConfigurationLoader(HttpClient httpClient) {
        this.client = Objects.requireNonNull(httpClient, "HTTP client cannot be null");
    }

    /**
     * Loads configuration from the given (if possible)
     *
     * @param discoveryUri Configuration discovery URI
     * @return Configuration, or {@code null} if unable to load
     */
    public OidcConfiguration load(URI discoveryUri) {
        try {
            // Make a GET request to obtain the OpenID Connect configuration
            HttpRequest request = HttpRequest.newBuilder(discoveryUri).build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                // Assuming an OK response parse it and extract the bit of configuration we care about
                OidcConfiguration configuration =
                        objectMapper.readValue(response.body(), OidcConfiguration.class);
                OidcRegistry.register(discoveryUri, configuration);
                return configuration;
            } else {
                LOGGER.warn("Obtaining OpenID Connect configuration from {} failed with HTTP status {}", discoveryUri,
                            response.statusCode());
            }
        } catch (Throwable e) {
            LOGGER.warn("Failed to obtain OpenID Connect discovery configuration: {}", e.getMessage());
        }

        return null;
    }
}
