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

import io.telicent.servlet.auth.jwt.verification.CachingJwtVerifier;
import io.telicent.servlet.auth.jwt.verification.JwtVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A factory for creating {@link JwtVerifier} from configuration
 * <p>
 * Whichever provider configures the verifier, it is wrapped in a {@link CachingJwtVerifier} unless that is disabled
 * via {@link ConfigurationParameters#PARAM_VERIFIED_CACHE_SECONDS} or
 * {@link ConfigurationParameters#PARAM_VERIFIED_CACHE_SIZE}.
 * </p>
 */
public class VerificationFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerificationFactory.class);

    private static final List<VerificationProvider> PROVIDERS = new ArrayList<>();

    static {
        try {
            ServiceLoader<VerificationProvider> loader = ServiceLoader.load(VerificationProvider.class);
            Iterator<VerificationProvider> iter = loader.iterator();
            while (iter.hasNext()) {
                PROVIDERS.add(iter.next());
            }
        } catch (ServiceConfigurationError e) {
            LOGGER.warn("Failed to load a VerificationProvider: ", e);
        } finally {
            ConfigurationProvider.sort(PROVIDERS);
        }
    }

    private VerificationFactory() {
    }

    /**
     * Configures a JWT Verifier in a runtime independent way
     *
     * @param paramSupplier    Supplier function where configuration parameters can be obtained from
     * @param verifierConsumer Consumer function that takes the configured verifier
     */
    // Sonar S4276 - deliberately NOT UnaryOperator<String>.  This maps a parameter NAME to a parameter VALUE, so it is
    // not an operation within a single domain; that both are String is coincidental and UnaryOperator would mislead
    // implementors.  These are also advertised ServiceLoader extension points, and UnaryOperator extends Function
    // rather than the reverse, so narrowing the declared type would break external implementations at compile time
    // and pre-compiled ones with AbstractMethodError.
    @SuppressWarnings("java:S4276")
    public static void configure(Function<String, String> paramSupplier, Consumer<JwtVerifier> verifierConsumer) {
        Consumer<JwtVerifier> cachingConsumer = verifier -> verifierConsumer.accept(withCaching(paramSupplier, verifier));
        for (VerificationProvider provider : PROVIDERS) {
            if (provider.configure(paramSupplier, cachingConsumer)) {
                return;
            }
        }
        LOGGER.warn("Failed to configure any JWT verifier from the available providers");
    }


    /**
     * Wraps a verifier in a {@link CachingJwtVerifier} as configured, unless caching is disabled
     *
     * @param paramSupplier Supplier function where configuration parameters can be obtained from
     * @param verifier      Verifier
     * @return Verifier to use
     */
    // Sonar S4276 - deliberately NOT UnaryOperator<String>, see configure() above
    @SuppressWarnings("java:S4276")
    static JwtVerifier withCaching(Function<String, String> paramSupplier, JwtVerifier verifier) {
        if (verifier == null || verifier instanceof CachingJwtVerifier) {
            return verifier;
        }
        int seconds = Utils.parseParameter(paramSupplier.apply(ConfigurationParameters.PARAM_VERIFIED_CACHE_SECONDS),
                                           Integer::parseInt, ConfigurationParameters.DEFAULT_VERIFIED_CACHE_SECONDS);
        int size = Utils.parseParameter(paramSupplier.apply(ConfigurationParameters.PARAM_VERIFIED_CACHE_SIZE),
                                        Integer::parseInt, ConfigurationParameters.DEFAULT_VERIFIED_CACHE_SIZE);
        if (seconds <= 0 || size <= 0) {
            LOGGER.info("Verified JWT caching is disabled ({}={}, {}={})",
                        ConfigurationParameters.PARAM_VERIFIED_CACHE_SECONDS, seconds,
                        ConfigurationParameters.PARAM_VERIFIED_CACHE_SIZE, size);
            return verifier;
        }
        LOGGER.info("Successfully verified JWTs will be remembered for up to {} seconds (at most {} tokens), set {} to 0 to "
                    + "disable", seconds, size, ConfigurationParameters.PARAM_VERIFIED_CACHE_SECONDS);
        return new CachingJwtVerifier(verifier, size, Duration.ofSeconds(seconds));
    }
}
