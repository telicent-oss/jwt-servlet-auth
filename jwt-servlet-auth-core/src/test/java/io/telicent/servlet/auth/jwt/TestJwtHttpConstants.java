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

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestJwtHttpConstants {

    @DataProvider(name = "valid")
    public Object[][] validValues() {
        return new Object[][] {
                { "text/plain" },
                { "text/html;q=1.0,text/xml;q=0.1" },
                { "sub.domain.com" },
                { "A human readable string" },
                { "With, basic; punctuation-characters" },
                { "foo=1+3" }
        };
    }

    @DataProvider(name = "invalid")
    public Object[][] invalidValues() {
        return new Object[][] {
                { "with\nnew line" },
                { "with\ttabs" },
                { "foo=\"bar\"" },
                { "with\\backslashes" },
                // Note that while " is valid within an HTTP Header value where it is used we generally are using it to
                // express parameters within an HTTP header value.  Thus, we don't want to allow the values we're
                // expressing in those parameters to break those parameters.
                { "realm=\"sub.domain.com\"" }
        };
    }

    @Test(dataProvider = "valid")
    public void givenValidHeaderValue_whenSanitising_thenUnchanged(String value) {
        // Given and When
        String sanitised = JwtHttpConstants.sanitiseHeaderParameterValue(value);

        // Then
        Assert.assertEquals(sanitised, value);
    }

    @Test(dataProvider = "invalid")
    public void givenInvalidHeaderValue_whenSanitising_thenChanged(String value) {
        // Given and When
        String sanitised = JwtHttpConstants.sanitiseHeaderParameterValue(value);

        // Then
        Assert.assertNotEquals(sanitised, value);
    }
}
