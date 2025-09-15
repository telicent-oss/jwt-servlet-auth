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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import io.telicent.servlet.auth.jwt.configuration.ConfigurationParameters;
import io.telicent.servlet.auth.jwt.configuration.RuntimeConfigurationAdaptor;
import io.telicent.servlet.auth.jwt.errors.KeyLoadException;
import io.telicent.servlet.auth.jwt.verification.KeyUtils;
import io.telicent.servlet.auth.jwt.verification.TestKeyUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyPair;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public abstract class AbstractConfigurableFilterTests<TRequest, TResponse, TFilter extends AbstractConfigurableJwtAuthFilter<TRequest, TResponse>>
        extends AbstractFilterTests<TRequest, TResponse, TFilter> {

    protected abstract RuntimeConfigurationAdaptor createConfigAdaptor(Map<String, String> configuration);

    /**
     * Creates a completely unconfigured instance of the filter
     *
     * @return Filter
     */
    protected abstract TFilter createUnconfiguredFilter();

    /**
     * Creates a mock request with the given Request URI and headers
     *
     * @param config  Configuration
     * @param headers Headers
     * @return Mock request
     */
    protected abstract TRequest createMockRequest(FilterConfigAdaptorWrapper config, Map<String, String> headers);

    /**
     * Creates a secret key for use in tests
     *
     * @param algorithm Algorithm
     * @return Secret Key
     * @throws IOException Thrown if the key cannot be generated/saved
     */
    public static File createSecretKey(MacAlgorithm algorithm) throws IOException {
        SecretKey key = algorithm.key().build();

        File keyFile = Files.createTempFile("secret", ".key").toFile();
        try (FileOutputStream output = new FileOutputStream(keyFile)) {
            output.write(key.getEncoded());
        }

        return keyFile;
    }

    @Test
    public void givenNoFilterConfiguration_whenConfiguringFilter_thenNothingIsConfigured() {
        // Given
        FilterConfigAdaptorWrapper config = new FilterConfigAdaptorWrapper(createConfigAdaptor(Map.of()));
        TFilter filter = this.createUnconfiguredFilter();

        // When
        filter.configure(config);

        // Then
        Assert.assertNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE));
    }

    @DataProvider(name = "macAlgorithms")
    public Object[][] macAlgorithms() {
        return new Object[][] {
                { Jwts.SIG.HS256 }, { Jwts.SIG.HS384 }, { Jwts.SIG.HS512 }
        };
    }

    @Test(dataProvider = "macAlgorithms")
    public void givenFilterConfigurationWithSecretKey_whenConfiguringFilter_thenVerifierIsConfigured_andFilterVerifiesRequest(
            MacAlgorithm algorithm) throws IOException, KeyLoadException {
        // Given
        File keyFile = createSecretKey(algorithm);
        FilterConfigAdaptorWrapper config = new FilterConfigAdaptorWrapper(
                createConfigAdaptor(Map.of(ConfigurationParameters.PARAM_SECRET_KEY, keyFile.getAbsolutePath())));
        TFilter filter = this.createUnconfiguredFilter();
        String jwt = Jwts.builder().subject("test").signWith(KeyUtils.loadSecretKey(keyFile)).compact();
        TRequest request = createMockRequest(config, Map.of(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                            JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt));
        TResponse response = createMockResponse();

        // When
        filter.configure(config);

        // Then
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE));

        // And
        this.invokeFilter(filter, request, response);
        this.verifyNoChallenge(request, response);
        Assert.assertEquals(this.getAuthenticatedUser(filter, request), "test");
    }

    @Test(dataProvider = "macAlgorithms")
    public void givenFilterConfigurationWithCustomHeader_whenConfiguringFilter_thenVerifierIsConfigured_andFilterVerifiesRequest(
            MacAlgorithm algorithm) throws IOException, KeyLoadException {
        // Given
        File keyFile = createSecretKey(algorithm);
        FilterConfigAdaptorWrapper config = new FilterConfigAdaptorWrapper(createConfigAdaptor(
                Map.of(ConfigurationParameters.PARAM_SECRET_KEY, keyFile.getAbsolutePath(),
                       ConfigurationParameters.PARAM_HEADER_NAMES, "X-Auth-Token")));
        TFilter filter = this.createUnconfiguredFilter();
        String jwt = Jwts.builder().subject("test").signWith(KeyUtils.loadSecretKey(keyFile)).compact();
        TRequest request = createMockRequest(config, Map.of("X-Auth-Token", jwt));
        TResponse response = createMockResponse();

        // When
        filter.configure(config);

        // Then
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE));

        // And
        this.invokeFilter(filter, request, response);
        this.verifyNoChallenge(request, response);
        Assert.assertEquals(this.getAuthenticatedUser(filter, request), "test");
    }

    @Test(dataProvider = "macAlgorithms")
    public void givenFilterConfigurationWithCustomHeaders_whenConfiguringFilter_thenVerifierIsConfigured_andFilterVerifiesRequest(
            MacAlgorithm algorithm) throws IOException, KeyLoadException {
        // Given
        File keyFile = createSecretKey(algorithm);
        FilterConfigAdaptorWrapper config = new FilterConfigAdaptorWrapper(createConfigAdaptor(
                Map.of(ConfigurationParameters.PARAM_SECRET_KEY, keyFile.getAbsolutePath(),
                       ConfigurationParameters.PARAM_HEADER_NAMES, "X-Auth-Token,X-Credentials",
                       ConfigurationParameters.PARAM_HEADER_PREFIXES, ",My Token Is")));
        TFilter filter = this.createUnconfiguredFilter();
        String jwt = Jwts.builder().subject("test").signWith(KeyUtils.loadSecretKey(keyFile)).compact();
        TRequest request = createMockRequest(config, Map.of("X-Credentials", "My Token Is " + jwt));
        TResponse response = createMockResponse();

        // When
        filter.configure(config);

        // Then
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE));

        // And
        this.invokeFilter(filter, request, response);
        this.verifyNoChallenge(request, response);
        Assert.assertEquals(this.getAuthenticatedUser(filter, request), "test");
    }

    @Test(dataProvider = "macAlgorithms")
    public void givenFilterConfigurationWithUsernameClaims_whenConfiguringFilter_thenVerifierIsConfigured_andFilterVerifiesRequest(
            MacAlgorithm algorithm) throws IOException, KeyLoadException {
        // Given
        File keyFile = createSecretKey(algorithm);
        FilterConfigAdaptorWrapper config = new FilterConfigAdaptorWrapper(createConfigAdaptor(
                Map.of(ConfigurationParameters.PARAM_SECRET_KEY, keyFile.getAbsolutePath(),
                       ConfigurationParameters.PARAM_USE_DEFAULT_HEADERS, "true",
                       ConfigurationParameters.PARAM_USERNAME_CLAIMS, "email")));
        TFilter filter = this.createUnconfiguredFilter();
        String jwt = Jwts.builder()
                         .subject("test")
                         .claims()
                         .add("email", "test@example.org")
                         .and()
                         .signWith(KeyUtils.loadSecretKey(keyFile))
                         .compact();
        TRequest request = createMockRequest(config, Map.of(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                            JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt));
        TResponse response = createMockResponse();

        // When
        filter.configure(config);

        // Then
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE));

        // And
        this.invokeFilter(filter, request, response);
        this.verifyNoChallenge(request, response);
        Assert.assertEquals(this.getAuthenticatedUser(filter, request), "test@example.org");
    }

    @Test(dataProvider = "macAlgorithms")
    public void givenFilterConfigurationWithMultipleUsernameClaims_whenConfiguringFilter_thenVerifierIsConfigured_andFilterVerifiesRequest(
            MacAlgorithm algorithm) throws IOException, KeyLoadException {
        // Given
        File keyFile = createSecretKey(algorithm);
        FilterConfigAdaptorWrapper config = new FilterConfigAdaptorWrapper(createConfigAdaptor(
                Map.of(ConfigurationParameters.PARAM_SECRET_KEY, keyFile.getAbsolutePath(),
                       ConfigurationParameters.PARAM_USE_DEFAULT_HEADERS, "true",
                       ConfigurationParameters.PARAM_USERNAME_CLAIMS, "email,name")));
        TFilter filter = this.createUnconfiguredFilter();
        String jwt = Jwts.builder()
                         .subject("test")
                         .claims()
                         .add("email", "")
                         .add("name", "Mr. T. Test")
                         .and()
                         .signWith(KeyUtils.loadSecretKey(keyFile))
                         .compact();
        TRequest request = createMockRequest(config, Map.of(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                            JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt));
        TResponse response = createMockResponse();

        // When
        filter.configure(config);

        // Then
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE));

        // And
        this.invokeFilter(filter, request, response);
        this.verifyNoChallenge(request, response);
        Assert.assertEquals(this.getAuthenticatedUser(filter, request), "Mr. T. Test");
    }

    @Test(dataProvider = "macAlgorithms")
    public void givenFilterConfigurationWithNestedUsernameClaims_whenConfiguringFilter_thenVerifierIsConfigured_andFilterVerifiesRequest(
            MacAlgorithm algorithm) throws IOException, KeyLoadException {
        // Given
        File keyFile = createSecretKey(algorithm);
        FilterConfigAdaptorWrapper config = new FilterConfigAdaptorWrapper(createConfigAdaptor(
                Map.of(ConfigurationParameters.PARAM_SECRET_KEY, keyFile.getAbsolutePath(),
                       ConfigurationParameters.PARAM_USE_DEFAULT_HEADERS, "true",
                       ConfigurationParameters.PARAM_USERNAME_CLAIMS, "details.name,details.email")));
        TFilter filter = this.createUnconfiguredFilter();
        String jwt = Jwts.builder()
                         .subject("test")
                         .claims(Map.of("details", Map.of("name", "Mr T. Test", "email", "test@example.org")))
                         .signWith(KeyUtils.loadSecretKey(keyFile))
                         .compact();
        TRequest request = createMockRequest(config, Map.of(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                            JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt));
        TResponse response = createMockResponse();

        // When
        filter.configure(config);

        // Then
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE));

        // And
        this.invokeFilter(filter, request, response);
        this.verifyNoChallenge(request, response);
        Assert.assertEquals(this.getAuthenticatedUser(filter, request), "Mr T. Test");
    }

    @Test(dataProvider = "macAlgorithms")
    public void givenFilterConfigurationWithCustomHeader_whenConfiguringFilter_thenVerifierIsConfigured_andFilterRejectsRequestUsingDefaultHeader(
            MacAlgorithm algorithm) throws IOException, KeyLoadException {
        // Given
        File keyFile = createSecretKey(algorithm);
        FilterConfigAdaptorWrapper config = new FilterConfigAdaptorWrapper(createConfigAdaptor(
                Map.of(ConfigurationParameters.PARAM_SECRET_KEY, keyFile.getAbsolutePath(),
                       ConfigurationParameters.PARAM_HEADER_NAMES, "X-Auth-Token")));
        TFilter filter = this.createUnconfiguredFilter();
        String jwt = Jwts.builder().subject("test").signWith(KeyUtils.loadSecretKey(keyFile)).compact();
        TRequest request = createMockRequest(config, Map.of(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                            JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt));
        TResponse response = createMockResponse();

        // When
        filter.configure(config);

        // Then
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE));

        // And
        this.invokeFilter(filter, request, response);
        this.verifyChallenge(request, response, 401);
    }

    @DataProvider(name = "publicKeyAlgorithms")
    public static Object[][] publicKeyAlgorithms() {
        return new Object[][] {
                { KeyUtils.RSA, Jwts.SIG.RS256.keyPair().build() },
                { KeyUtils.RSA, Jwts.SIG.RS384.keyPair().build() },
                { KeyUtils.RSA, Jwts.SIG.RS512.keyPair().build() },
                { KeyUtils.EC, Jwts.SIG.ES256.keyPair().build() },
                { KeyUtils.EC, Jwts.SIG.ES384.keyPair().build() },
                { KeyUtils.EC, Jwts.SIG.ES512.keyPair().build() }
        };
    }

    @Test(dataProvider = "publicKeyAlgorithms")
    public void givenFilterConfigurationWithPublicKey_whenConfiguringFilter_thenVerifierIsConfigured_andFilterVerifiesRequest(
            String algorithm, KeyPair keyPair) throws IOException {
        // Given
        File keyFile = TestKeyUtils.saveKeyToFile(Base64.getEncoder().encode(keyPair.getPublic().getEncoded()));
        FilterConfigAdaptorWrapper config = new FilterConfigAdaptorWrapper(createConfigAdaptor(
                Map.of(ConfigurationParameters.PARAM_PUBLIC_KEY, keyFile.getAbsolutePath(),
                       ConfigurationParameters.PARAM_KEY_ALGORITHM, algorithm)));
        TFilter filter = this.createUnconfiguredFilter();
        String jwt = Jwts.builder().subject("test").signWith(keyPair.getPrivate()).compact();
        TRequest request = createMockRequest(config, Map.of(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                            JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt));
        TResponse response = createMockResponse();

        // When
        filter.configure(config);

        // Then
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE));

        // And
        this.invokeFilter(filter, request, response);
        this.verifyNoChallenge(request, response);
        Assert.assertEquals(this.getAuthenticatedUser(filter, request), "test");
    }

    @Test(dataProvider = "publicKeyAlgorithms")
    public void givenFilterConfigurationWithTopLevelRolesClaimPath_whenConfiguringFilter_thenVerifierIsConfigured_andFilterVerifiesRequestButUserHasNoRoles(
            String algorithm, KeyPair keyPair) throws IOException {
        // Given
        File keyFile = TestKeyUtils.saveKeyToFile(Base64.getEncoder().encode(keyPair.getPublic().getEncoded()));
        FilterConfigAdaptorWrapper config = new FilterConfigAdaptorWrapper(createConfigAdaptor(
                Map.of(ConfigurationParameters.PARAM_PUBLIC_KEY, keyFile.getAbsolutePath(),
                       ConfigurationParameters.PARAM_KEY_ALGORITHM, algorithm,
                       ConfigurationParameters.PARAM_HEADER_NAMES, JwtHttpConstants.HEADER_AUTHORIZATION,
                       ConfigurationParameters.PARAM_HEADER_PREFIXES, JwtHttpConstants.AUTH_SCHEME_BEARER,
                       ConfigurationParameters.PARAM_ROLES_CLAIM, "roles")));
        TFilter filter = this.createUnconfiguredFilter();
        String jwt = Jwts.builder()
                         .subject("test")
                         .claims(Map.of("roles", List.of("USER", "ADMIN")))
                         .signWith(keyPair.getPrivate())
                         .compact();
        TRequest request = createMockRequest(config, Map.of(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                            JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt));
        TResponse response = createMockResponse();

        // When
        filter.configure(config);

        // Then
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE));

        // And
        this.invokeFilter(filter, request, response);
        this.verifyNoChallenge(request, response);
        Assert.assertEquals(this.getAuthenticatedUser(filter, request), "test");
        request = filter.lastAuthenticatedRequest;
        this.verifyHasRole(request, "USER");
        this.verifyHasRole(request, "ADMIN");
    }

    @Test(dataProvider = "publicKeyAlgorithms")
    public void givenFilterConfigurationWithEmptyRolesClaimPath_whenConfiguringFilter_thenVerifierIsConfigured_andFilterVerifiesRequestButUserHasNoRoles(
            String algorithm, KeyPair keyPair) throws IOException {
        // Given
        File keyFile = TestKeyUtils.saveKeyToFile(Base64.getEncoder().encode(keyPair.getPublic().getEncoded()));
        FilterConfigAdaptorWrapper config = new FilterConfigAdaptorWrapper(createConfigAdaptor(
                Map.of(ConfigurationParameters.PARAM_PUBLIC_KEY, keyFile.getAbsolutePath(),
                       ConfigurationParameters.PARAM_KEY_ALGORITHM, algorithm,
                       ConfigurationParameters.PARAM_HEADER_NAMES, JwtHttpConstants.HEADER_AUTHORIZATION,
                       ConfigurationParameters.PARAM_HEADER_PREFIXES, JwtHttpConstants.AUTH_SCHEME_BEARER,
                       ConfigurationParameters.PARAM_ROLES_CLAIM, "  ")));
        TFilter filter = this.createUnconfiguredFilter();
        String jwt = Jwts.builder()
                         .subject("test")
                         .claims(Map.of("roles", List.of("USER", "ADMIN")))
                         .signWith(keyPair.getPrivate())
                         .compact();
        TRequest request = createMockRequest(config, Map.of(JwtHttpConstants.HEADER_AUTHORIZATION,
                                                            JwtHttpConstants.AUTH_SCHEME_BEARER + " " + jwt));
        TResponse response = createMockResponse();

        // When
        filter.configure(config);

        // Then
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER));
        Assert.assertNotNull(config.getAttribute(JwtServletConstants.ATTRIBUTE_JWT_ENGINE));

        // And
        this.invokeFilter(filter, request, response);
        this.verifyNoChallenge(request, response);
        Assert.assertEquals(this.getAuthenticatedUser(filter, request), "test");
        request = filter.lastAuthenticatedRequest;
        this.verifyMissingRole(request, "USER");
        this.verifyMissingRole(request, "ADMIN");
    }

}
