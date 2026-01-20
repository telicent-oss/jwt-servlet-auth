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

import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Locator;
import io.telicent.servlet.auth.jwt.errors.KeyLoadException;
import io.telicent.servlet.auth.jwt.verification.JwtVerifier;
import io.telicent.servlet.auth.jwt.verification.JwtParsers;
import io.telicent.servlet.auth.jwt.verification.KeyUtils;
import io.telicent.servlet.auth.jwt.verification.SignedJwtVerifier;
import io.telicent.servlet.auth.jwt.verification.jwks.CachedJwksKeyLocator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.security.Key;
import java.security.PublicKey;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A provider for creating {@link JwtVerifier} from configuration.  This is able to configure a
 * {@link SignedJwtVerifier} using a secret/public key, or a JWKS URL, plus applies other common configuration (e.g.
 * allowed clock skew) to the verifier.
 */
public class DefaultVerificationProvider implements VerificationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultVerificationProvider.class);

    /**
     * Available configuration parameters
     */
    public static final String[] PARAMETERS = new String[] {
            ConfigurationParameters.PARAM_PUBLIC_KEY,
            ConfigurationParameters.PARAM_SECRET_KEY,
            ConfigurationParameters.PARAM_JWKS_URL,
            ConfigurationParameters.PARAM_KEY_ALGORITHM,
            ConfigurationParameters.PARAM_ALLOWED_CLOCK_SKEW
    };

    /**
     * Creates the new default verification provider
     */
    public DefaultVerificationProvider() {
    }

    private Map<String, String> prepareParameters(Function<String, String> paramSupplier) {
        Map<String, String> parameters = new HashMap<>();
        for (String param : DefaultVerificationProvider.PARAMETERS) {
            parameters.put(param, paramSupplier.apply(param));
        }
        parameters.entrySet().removeIf(e -> e.getValue() == null);
        return parameters;
    }

    /**
     * Configures a JWT Verifier in a runtime independent way
     *
     * @param paramSupplier    Supplier function where configuration parameters can be obtained from
     * @param verifierConsumer Consumer function that takes the configured verifier
     */
    @Override
    public boolean configure(Function<String, String> paramSupplier, Consumer<JwtVerifier> verifierConsumer) {
        Map<String, String> parameters = prepareParameters(paramSupplier);
        if (parameters.isEmpty()) {
            LOGGER.info(
                    "No relevant parameters provided to allow default JWT verifier configuration, authentication will not be possible unless the verifier is separately configured.");
        } else {
            try {
                JwtVerifier jwtVerifier = create(parameters);
                verifierConsumer.accept(jwtVerifier);
                LOGGER.info("Configured the default JWT verifier: {}", jwtVerifier);
                return true;
            } catch (KeyLoadException e) {
                LOGGER.error("Failed to configure default JWT verifier: ", e);
            }
        }
        return false;
    }

    /**
     * Creates a {@link JwtVerifier} from the given parameters
     *
     * @param parameters Parameters
     * @return JWT Verifier
     * @throws KeyLoadException Thrown if the parameters are insufficient to create a verifier
     */
    private JwtVerifier create(Map<String, String> parameters) throws KeyLoadException {
        String jwksUrl = parameters.get(ConfigurationParameters.PARAM_JWKS_URL);
        String secretKey = parameters.get(ConfigurationParameters.PARAM_SECRET_KEY);
        String publicKey = parameters.get(ConfigurationParameters.PARAM_PUBLIC_KEY);
        String algorithm = parameters.get(ConfigurationParameters.PARAM_KEY_ALGORITHM);
        Integer cacheKeysFor =
                Utils.parseParameter(parameters, ConfigurationParameters.PARAM_JWKS_CACHE_KEYS_FOR, Integer::parseInt,
                                     ConfigurationParameters.DEFAULT_JWKS_CACHE_KEYS_FOR);

        if (StringUtils.isNotBlank(jwksUrl)) {
            Locator<Key> jwks = new CachedJwksKeyLocator(asURI(jwksUrl), HttpClient.newBuilder().build(),
                                                         Duration.ofMinutes(cacheKeysFor));
            return create(parameters, JwtParsers.builder().keyLocator(jwks), SignedJwtVerifier.debugStringForLocator(jwks));
        } else if (StringUtils.isNotBlank(secretKey)) {
            SecretKey secret = KeyUtils.loadSecretKey(new File(secretKey));
            return create(parameters, JwtParsers.builder().verifyWith(secret), SignedJwtVerifier.SECRET_KEY_DEBUG_STRING);
        } else if (StringUtils.isNotBlank(publicKey)) {
            PublicKey key = KeyUtils.loadPublicKey(algorithm, new File(publicKey));
            return create(parameters, JwtParsers.builder().verifyWith(key), SignedJwtVerifier.debugStringForPublicKey(key));
        } else {
            throw new KeyLoadException("No parameter available to supply a key or JWKS URL for JWT verification.");
        }

    }

    private static URI asURI(String jwksUrl) throws KeyLoadException {
        try {
            URI uri = URI.create(jwksUrl);
            if (StringUtils.isBlank(uri.getScheme())) {
                // Possible that the URL is just a plain local filename, try that and see if it works?
                File jwksFile = new File(jwksUrl);
                if (jwksFile.exists()) {
                    try {
                        return jwksFile.toURI();
                    } catch (IllegalArgumentException e) {
                        throw invalidJwksUrl(e);
                    }
                }
                throw invalidJwksUrl(null);
            }
            return uri;
        } catch (IllegalArgumentException e) {
            throw invalidJwksUrl(e);
        }
    }

    private static KeyLoadException invalidJwksUrl(Throwable e) {
        return new KeyLoadException("Parameter " + ConfigurationParameters.PARAM_JWKS_URL + " is not a valid URL", e);
    }

    private JwtVerifier create(Map<String, String> parameters, JwtParserBuilder builder, String debugString) {
        Integer allowedClockSkew =
                Utils.parseParameter(parameters, ConfigurationParameters.PARAM_ALLOWED_CLOCK_SKEW, Integer::parseInt,
                                     null);
        if (allowedClockSkew != null) {
            builder.clockSkewSeconds(allowedClockSkew);
        }
        // TODO Allow configuring various requirements on the JWT parser e.g. issuer
        return new SignedJwtVerifier(builder.build(), debugString);
    }
}
