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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents discovered OpenID Connect configuration
 */
public class OidcConfiguration {

    @JsonProperty("jwks_uri")
    private String jwksUri;
    private String issuer;
    @JsonProperty("userinfo_endpoint")
    private String userinfoEndpoint;
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    /**
     * Gets the JWKS URI
     *
     * @return JWKS URI
     */
    public String getJwksUri() {
        return jwksUri;
    }

    /**
     * Sets the JWKS URI
     *
     * @param jwksUri JWKS URI
     */
    public void setJwksUri(String jwksUri) {
        this.jwksUri = jwksUri;
    }

    /**
     * Gets the Issuer
     *
     * @return Issuer
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Sets the Issuer
     *
     * @param issuer Issuer
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * Gets the User Info endpoint
     *
     * @return User Info endpoint
     */
    public String getUserinfoEndpoint() {
        return userinfoEndpoint;
    }

    /**
     * Sets the User Info endpoint
     *
     * @param userinfoEndpoint User Info endpoint
     */
    public void setUserinfoEndpoint(String userinfoEndpoint) {
        this.userinfoEndpoint = userinfoEndpoint;
    }

    /**
     * Sets additional configuration
     *
     * @param key   Key
     * @param value Value
     */
    @JsonAnySetter
    public void setProperty(String key, Object value) {
        this.additionalProperties.put(key, value);
    }

    /**
     * Gets additional configuration
     *
     * @return Additional configuration
     */
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }
}
