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

import io.telicent.servlet.auth.jwt.JwtAuthenticationEngine;
import io.telicent.servlet.auth.jwt.PathExclusion;
import io.telicent.servlet.auth.jwt.fake.FakeEngine;
import io.telicent.servlet.auth.jwt.verification.FakeTokenVerifier;
import io.telicent.servlet.auth.jwt.verification.JwtVerifier;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class TestFrozenFilterConfiguration {

    @Test
    public void givenFrozenFilterConfiguration_whenSettingEngine_thenOldValueIsPreserved() {
        // Given
        FrozenFilterConfiguration config = new FrozenFilterConfiguration();
        config.tryFreezeEngineConfiguration(Mockito.mock(JwtAuthenticationEngine.class), Mockito.mock(
                JwtAuthenticationEngine.class));
        JwtAuthenticationEngine<?, ?> other = new FakeEngine();

        // When
        config.tryFreezeEngineConfiguration(other, Mockito.mock(JwtAuthenticationEngine.class));

        // Then
        Assert.assertNotEquals(config.getEngine(), other);
    }

    @Test
    public void givenFrozenFilterConfiguration_whenSettingVerifier_thenOldValueIsPreserved() {
        // Given
        FrozenFilterConfiguration config = new FrozenFilterConfiguration();
        config.tryFreezeVerifierConfiguration(Mockito.mock(JwtVerifier.class));
        JwtVerifier other = new FakeTokenVerifier();

        // When
        config.tryFreezeVerifierConfiguration(other);

        // Then
        Assert.assertNotEquals(config.getVerifier(), other);
    }

    @Test
    public void givenFrozenFilterConfiguration_whenSettingExclusions_thenOldValueIsPreserved() {
        // Given
        FrozenFilterConfiguration config = new FrozenFilterConfiguration();
        config.tryFreezeExclusionsConfiguration(Collections.<PathExclusion>emptyList());
        List<PathExclusion> other = PathExclusion.parsePathPatterns("/status/*");

        // When
        config.tryFreezeExclusionsConfiguration(other);

        // Then
        Assert.assertNotEquals(config.getExclusions(), other);
    }

    @Test
    public void givenEmptyFilterConfiguration_whenCheckingForWarning_thenNothingHappens() {
        // Given
        FrozenFilterConfiguration config = new FrozenFilterConfiguration();

        // When and Then
        config.warnIfModificationAttempted("test", x -> null, config.getEngine());
    }

    @Test
    public void givenFrozenFilterConfiguration_whenCheckingForWarningWithSameObject_thenNothingHappens() {
        // Given
        FrozenFilterConfiguration config = new FrozenFilterConfiguration();
        FakeEngine engine = new FakeEngine();
        config.tryFreezeEngineConfiguration(engine, Mockito.mock(JwtAuthenticationEngine.class));

        // When and Then
        config.warnIfModificationAttempted("test", x -> engine, config.getEngine());
    }

    @Test
    public void givenFrozenFilterConfiguration_whenCheckingForWarningWithDifferentObject_thenWarningIssued() {
        // Given
        FrozenFilterConfiguration config = new FrozenFilterConfiguration();
        FakeEngine engine = new FakeEngine();
        FakeEngine other = new FakeEngine();
        config.tryFreezeEngineConfiguration(engine, Mockito.mock(JwtAuthenticationEngine.class));

        // When
        config.warnIfModificationAttempted("test", x -> other, config.getEngine());

        // Then
        // TODO Check the warning was issued
    }
}
