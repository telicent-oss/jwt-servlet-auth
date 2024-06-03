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
import io.telicent.servlet.auth.jwt.configuration.VerificationFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class TestAwsVerificationProvider {

    public static Function<String, String> mapSupplier(Map<String, String> map) {
        return x -> map.get(x);
    }

    @Test
    public void givenNoConfiguration_whenConfiguringVerifier_thenNothingIsConfigured() {
        // Given
        AtomicReference<JwtVerifier> verifier = new AtomicReference<>();

        // When
        VerificationFactory.configure(x -> null, y -> verifier.set(y));

        // Then
        Assert.assertNull(verifier.get());
    }

    @Test
    public void givenAwsRegion_whenConfiguringVerifier_thenVerifierIsConfigured() {
        // Given
        AtomicReference<JwtVerifier> verifier = new AtomicReference<>();
        Map<String, String> config = Map.of(AwsVerificationProvider.PARAM_AWS_REGION, "eu-west-1");

        // When
        VerificationFactory.configure(mapSupplier(config), x -> verifier.set(x));

        // Then
        Assert.assertNotNull(verifier.get());
        Assert.assertTrue(verifier.get() instanceof AwsElbJwtVerifier);
    }
}
