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
package io.telicent.servlet.auth.jwt.verifier.aws;

import io.telicent.servlet.auth.jwt.verification.JwtVerifier;
import io.telicent.servlet.auth.jwt.configuration.VerificationProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A provider of automated configuration for {@link AwsElbJwtVerifier} verifiers
 */
public class AwsVerificationProvider implements VerificationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsVerificationProvider.class);

    /**
     * Parameter used to configure the AWS region from which public keys should be obtained for JWT verification
     */
    public static final String PARAM_AWS_REGION = "jwt.aws.region";

    @Override
    public boolean configure(Function<String, String> paramSupplier, Consumer<JwtVerifier> verifierConsumer) {
        String region = paramSupplier.apply(PARAM_AWS_REGION);
        if (StringUtils.isNotBlank(region)) {
            AwsElbJwtVerifier jwtVerifier = new AwsElbJwtVerifier(region);
            verifierConsumer.accept(jwtVerifier);
            LOGGER.info("Configured the AWS JWT Verifier: {}", jwtVerifier);
            return true;
        } else {
            LOGGER.info("No relevant parameters to allow AWS verifier configuration.");
        }
        return false;
    }

    @Override
    public int priority() {
        return 1;
    }
}
