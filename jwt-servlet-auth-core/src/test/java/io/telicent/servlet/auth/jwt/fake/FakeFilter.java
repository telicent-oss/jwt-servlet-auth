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

import io.telicent.servlet.auth.jwt.AbstractJwtAuthFilter;
import io.telicent.servlet.auth.jwt.configuration.FrozenFilterConfiguration;

public class FakeFilter extends AbstractJwtAuthFilter<FakeRequest, FakeResponse> {

    private FakeEngine defaultEngine;
    private FrozenFilterConfiguration<FakeRequest, FakeResponse> configuration = new FrozenFilterConfiguration<>();
    private boolean excluded = false;
    private boolean calledEngine = false;

    public FakeFilter() {
        this(new FakeEngine());
    }

    public FakeFilter(FakeEngine defaultEngine) {
        this.defaultEngine = defaultEngine;
    }

    public void doFilter(FakeRequest request, FakeResponse response, Object rawExclusions, Object rawEngine,
                         Object rawVerifier) {
        this.configuration.tryFreezeExclusionsConfiguration(rawExclusions);
        if (this.isExcludedPath(request.requestUrl, this.configuration.getExclusions())) {
            this.excluded = true;
            return;
        }

        this.configuration.tryFreezeEngineConfiguration(rawEngine, this.defaultEngine);
        this.configuration.tryFreezeVerifierConfiguration(rawVerifier);

        this.configuration.getEngine().authenticate(request, response, this.configuration.getVerifier());
        this.calledEngine = true;
    }

    public boolean wasExcluded() {
        return this.excluded;
    }

    public boolean wasEngineCalled() {
        return this.calledEngine;
    }

    public void reset() {
        this.excluded = false;
        this.calledEngine = false;
        this.configuration = new FrozenFilterConfiguration<>();
    }
}
