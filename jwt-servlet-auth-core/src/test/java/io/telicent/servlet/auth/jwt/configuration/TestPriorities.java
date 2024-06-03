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

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestPriorities {

    public ConfigurationProvider create(int priority) {
        return new ConfigurationProvider() {
            @Override
            public int priority() {
                return priority;
            }
        };
    }

    @DataProvider(name = "factories")
    public Object[][] factories() {
        ConfigurationProvider zero = create(0);
        return new Object[][] {
                { List.of(create(0), create(10), create(5), create(-1), create(Integer.MAX_VALUE)) },
                { List.of(create(100), create(200)) },
                { List.of(create(1), create(2), create(3)) },
                { List.of(zero, zero, zero) },
                { List.of(create(0), create(0), create(0)) },
                { Arrays.asList(null, create(77), null) }
        };
    }

    private void verifySortOrder(List<ConfigurationProvider> factories) {
        int current = Integer.MAX_VALUE;
        boolean seenAnyNulls = false;
        for (ConfigurationProvider factory : factories) {
            if (factory != null) {
                Assert.assertFalse(seenAnyNulls, "Null factories should always be given the lowest priority");
                Assert.assertTrue(factory.priority() <= current,
                                  "Expected lower priority than " + current + " but got " + factory.priority());
                current = factory.priority();
            } else {
                seenAnyNulls = true;
            }
        }
    }

    @Test(dataProvider = "factories")
    public void givenFactoriesWithDifferentPriorities_whenSorting_thenSortOrderIsCorrect(
            List<ConfigurationProvider> factories) {
        // Given
        List<ConfigurationProvider> toSort = new ArrayList<>(factories);

        // When
        ConfigurationProvider.sort(toSort);

        // Then
        verifySortOrder(toSort);
    }
}
