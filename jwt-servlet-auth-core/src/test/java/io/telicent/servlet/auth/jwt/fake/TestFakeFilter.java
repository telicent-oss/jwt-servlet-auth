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
package io.telicent.servlet.auth.jwt.fake;

import io.telicent.servlet.auth.jwt.PathExclusion;
import io.telicent.servlet.auth.jwt.errors.AuthenticationConfigurationError;
import io.telicent.servlet.auth.jwt.verification.FakeTokenVerifier;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TestFakeFilter {

    private final FakeFilter filter = new FakeFilter();

    @BeforeMethod
    private void preTest() {
        this.filter.reset();
    }

    @Test(expectedExceptions = AuthenticationConfigurationError.class)
    public void wrong_engine_type() {
        this.filter.doFilter(new FakeRequest(), new FakeResponse(), Collections.emptyList(), new Object(),
                             new Object());
    }

    @Test(expectedExceptions = AuthenticationConfigurationError.class)
    public void no_engine() {
        FakeFilter filter = new FakeFilter(null);
        filter.doFilter(new FakeRequest(), new FakeResponse(), Collections.emptyList(), null, new Object());
    }

    @Test(expectedExceptions = AuthenticationConfigurationError.class)
    public void wrong_verifier_type() {
        this.filter.doFilter(new FakeRequest(), new FakeResponse(), Collections.emptyList(), new FakeEngine(),
                             new Object());
    }

    @Test(expectedExceptions = AuthenticationConfigurationError.class)
    public void no_verifier() {
        this.filter.doFilter(new FakeRequest(), new FakeResponse(), Collections.emptyList(), new FakeEngine(), null);
    }

    @Test
    public void no_exclusions() {
        this.filter.doFilter(new FakeRequest(), new FakeResponse(), null, new FakeEngine(), new FakeTokenVerifier());
        Assert.assertFalse(this.filter.wasExcluded());
        Assert.assertTrue(this.filter.wasEngineCalled());
    }

    @Test(expectedExceptions = AuthenticationConfigurationError.class)
    public void wrong_exclusions_type() {
        this.filter.doFilter(new FakeRequest(), new FakeResponse(), new HashMap<String, String>(), new FakeEngine(),
                             new FakeTokenVerifier());
    }

    @Test
    public void use_default_engine() {
        this.filter.doFilter(new FakeRequest(), new FakeResponse(), Collections.emptyList(), null,
                             new FakeTokenVerifier());
        Assert.assertFalse(this.filter.wasExcluded());
        Assert.assertTrue(this.filter.wasEngineCalled());
    }

    @DataProvider(name = "exclusions")
    private Object[][] getExclusionTests() {
        return new Object[][] {
                { "", false },
                { "/other", false },
                { "/secure/path", false },
                { "/fixed", true },
                { "/status/", true },
                { "/status/healthz", true },
                { "/fixed-other", false },
                { "/fixed/insecure", true },
                { "/status/insecure", true }
        };
    }

    @Test(dataProvider = "exclusions")
    public void excluded_paths(String requestPath, boolean shouldBeExcluded) {
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("/fixed,/status/*,*insecure");
        this.filter.doFilter(new FakeRequest(requestPath), new FakeResponse(), exclusions, null,
                             new FakeTokenVerifier());
        Assert.assertEquals(this.filter.wasExcluded(), shouldBeExcluded);
        Assert.assertEquals(this.filter.wasEngineCalled(), !shouldBeExcluded);
    }

    @Test(dataProvider = "exclusions")
    public void excluded_paths_no_exclusions_configured_01(String requestPath, boolean shouldBeExcluded) {
        // When no exclusions configured nothing will be excluded
        this.filter.doFilter(new FakeRequest(requestPath), new FakeResponse(), null, null, new FakeTokenVerifier());
        Assert.assertFalse(this.filter.wasExcluded());
        Assert.assertTrue(this.filter.wasEngineCalled());
    }

    @Test(dataProvider = "exclusions")
    public void excluded_paths_no_exclusions_configured_02(String requestPath, boolean shouldBeExcluded) {
        // When no exclusions configured nothing will be excluded
        this.filter.doFilter(new FakeRequest(requestPath), new FakeResponse(), Collections.emptyList(), null,
                             new FakeTokenVerifier());
        Assert.assertFalse(this.filter.wasExcluded());
        Assert.assertTrue(this.filter.wasEngineCalled());
    }
}
