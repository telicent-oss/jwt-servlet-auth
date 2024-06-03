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
package io.telicent.servlet.auth.jwt.errors;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestErrors {

    @Test
    public void config_error_01() {
        Exception e = new AuthenticationConfigurationError("misconfiguration");
        Assert.assertNotNull(e.getMessage());
        Assert.assertEquals(e.getMessage(), "misconfiguration");
        Assert.assertNull(e.getCause());
    }

    @Test
    public void config_error_02() {
        Throwable cause = new RuntimeException("cause");
        Exception e = new AuthenticationConfigurationError("misconfiguration", cause);
        Assert.assertNotNull(e.getMessage());
        Assert.assertEquals(e.getMessage(), "misconfiguration");
        Assert.assertNotNull(e.getCause());
        Assert.assertEquals(e.getCause().getMessage(), "cause");
    }
}
