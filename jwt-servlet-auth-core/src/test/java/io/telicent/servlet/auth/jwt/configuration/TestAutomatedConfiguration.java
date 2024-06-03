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

import io.jsonwebtoken.Jwts;
import io.telicent.servlet.auth.jwt.JwtAuthenticationEngine;
import io.telicent.servlet.auth.jwt.PathExclusion;
import io.telicent.servlet.auth.jwt.ServletConstants;
import io.telicent.servlet.auth.jwt.fake.FakeEngine;
import io.telicent.servlet.auth.jwt.verification.SignedJwtVerifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class TestAutomatedConfiguration extends AbstractFactoryTests {

    private static final String EXAMPLE_JWKS_URL = "https://example.org/jwks.json";

    @Test
    public void givenNoConfig_whenAutomaticallyConfiguring_thenNothingIsConfigured() {
        // Given
        MapRuntimeConfigAdaptor config = new MapRuntimeConfigAdaptor();

        // When
        AutomatedConfiguration.configure(config);

        // Then
        Assert.assertNull(config.getAttribute(ServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNull(config.getAttribute(ServletConstants.ATTRIBUTE_PATH_EXCLUSIONS));
        Assert.assertNull(config.getAttribute(ServletConstants.ATTRIBUTE_JWT_ENGINE));
    }

    @Test
    public void givenConfig_whenAutomaticallyConfiguring_thenEverythingIsConfigured() {
        // Given
        MapRuntimeConfigAdaptor config = new MapRuntimeConfigAdaptor(
                Map.of(ConfigurationParameters.PARAM_JWKS_URL, EXAMPLE_JWKS_URL,
                       ConfigurationParameters.PARAM_PATH_EXCLUSIONS, "/public/*",
                       ConfigurationParameters.PARAM_HEADER_NAMES, "Authorization",
                       ConfigurationParameters.PARAM_USERNAME_CLAIMS, "email"));

        // When
        AutomatedConfiguration.configure(config);

        // Then
        Assert.assertNotNull(config.getAttribute(ServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNotNull(config.getAttribute(ServletConstants.ATTRIBUTE_PATH_EXCLUSIONS));
        Assert.assertNotNull(config.getAttribute(ServletConstants.ATTRIBUTE_JWT_ENGINE));
    }

    @Test
    public void givenPreExistingConfig_whenAutomaticallyConfiguring_thenReconfigurationIsSkipped() {
        // Given
        MapRuntimeConfigAdaptor config =
                new MapRuntimeConfigAdaptor(Map.of(ConfigurationParameters.PARAM_JWKS_URL, EXAMPLE_JWKS_URL));
        SignedJwtVerifier jwtVerifier =
                new SignedJwtVerifier(Jwts.parser().verifyWith(Jwts.SIG.HS256.key().build()).build());
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("/status/*");
        JwtAuthenticationEngine<?, ?> engine = new FakeEngine();
        config.setAttribute(ServletConstants.ATTRIBUTE_JWT_VERIFIER, jwtVerifier);
        config.setAttribute(ServletConstants.ATTRIBUTE_PATH_EXCLUSIONS, exclusions);
        config.setAttribute(ServletConstants.ATTRIBUTE_JWT_ENGINE, engine);

        // When
        AutomatedConfiguration.configure(config);

        // Then
        Assert.assertEquals(config.getAttribute(ServletConstants.ATTRIBUTE_JWT_VERIFIER), jwtVerifier);
        Assert.assertEquals(config.getAttribute(ServletConstants.ATTRIBUTE_PATH_EXCLUSIONS), exclusions);
        Assert.assertEquals(config.getAttribute(ServletConstants.ATTRIBUTE_JWT_ENGINE), engine);
    }

    @Test
    public void givenPreExistingConfigWithMultipleConfigsAllowed_whenAutomaticallyConfiguring_thenReconfigurationOccurs() {
        // Given
        MapRuntimeConfigAdaptor config =
                new MapRuntimeConfigAdaptor(Map.of(ConfigurationParameters.PARAM_JWKS_URL, EXAMPLE_JWKS_URL,
                                                   ConfigurationParameters.PARAM_ALLOW_MULTIPLE_CONFIGS, "true",
                                                   ConfigurationParameters.PARAM_HEADER_NAMES, "X-API-Key",
                                                   ConfigurationParameters.PARAM_PATH_EXCLUSIONS, "/healthz"));
        SignedJwtVerifier jwtVerifier =
                new SignedJwtVerifier(Jwts.parser().verifyWith(Jwts.SIG.HS256.key().build()).build());
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("/status/*");
        JwtAuthenticationEngine<?, ?> engine = new FakeEngine();
        config.setAttribute(ServletConstants.ATTRIBUTE_JWT_VERIFIER, jwtVerifier);
        config.setAttribute(ServletConstants.ATTRIBUTE_PATH_EXCLUSIONS, exclusions);
        config.setAttribute(ServletConstants.ATTRIBUTE_JWT_ENGINE, engine);

        // When
        AutomatedConfiguration.configure(config);

        // Then
        Assert.assertNotEquals(config.getAttribute(ServletConstants.ATTRIBUTE_JWT_VERIFIER), jwtVerifier);
        Assert.assertNotEquals(config.getAttribute(ServletConstants.ATTRIBUTE_PATH_EXCLUSIONS), exclusions);
        Assert.assertNotEquals(config.getAttribute(ServletConstants.ATTRIBUTE_JWT_ENGINE), engine);
    }
}
