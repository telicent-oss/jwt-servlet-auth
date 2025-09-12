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

import org.apache.commons.lang3.Strings;
import org.mockito.ArgumentMatcher;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

/**
 * A Mockito argument matcher that matches on strings ignoring case
 */
public class EqualsIgnoreCase implements ArgumentMatcher<String> {

    public static String eqIgnoresCase(String wanted) {
        mockingProgress().getArgumentMatcherStorage().reportMatcher(new EqualsIgnoreCase(wanted));
        return null;
    }

    private final String wanted;

    public EqualsIgnoreCase(String wanted) {
        this.wanted = wanted;
    }

    @Override
    public boolean matches(String argument) {
        return Strings.CI.equals(this.wanted, argument);
    }

    @Override
    public String toString() {
        return String.format("eqIgnoresCase(%s)", this.wanted);
    }
}
