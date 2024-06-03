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
package io.telicent.servlet.auth.jwt.sources;

import io.telicent.servlet.auth.jwt.JwtHttpConstants;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestHeaderSource {

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*null/blank")
    public void header_source_bad_01() {
        new HeaderSource(null, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*null/blank")
    public void header_source_bad_02() {
        new HeaderSource("", null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*null/blank")
    public void header_source_bad_03() {
        new HeaderSource("   ", null);
    }

    @Test
    public void header_source_01() {
        HeaderSource header = new HeaderSource(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER);

        Assert.assertEquals(header.getHeader(), JwtHttpConstants.HEADER_AUTHORIZATION);
        Assert.assertEquals(header.getPrefix(), JwtHttpConstants.AUTH_SCHEME_BEARER);

        Assert.assertNull(header.getRawToken(null));
        Assert.assertNull(header.getRawToken(JwtHttpConstants.AUTH_SCHEME_BEARER));
        Assert.assertNull(header.getRawToken(JwtHttpConstants.AUTH_SCHEME_BEARER + "     "));
        Assert.assertNull(header.getRawToken("foo"));
        Assert.assertEquals(header.getRawToken(JwtHttpConstants.AUTH_SCHEME_BEARER + " foo"), "foo");
    }

    @Test
    public void header_source_02() {
        String customHeader = "Custom";
        HeaderSource header = new HeaderSource(customHeader, null);

        Assert.assertEquals(header.getHeader(), customHeader);
        Assert.assertNull(header.getPrefix());

        Assert.assertNull(header.getRawToken(null));
        Assert.assertEquals(header.getRawToken("foo"), "foo");
    }

    @DataProvider(name = "sources")
    public Object[][] headerSources() {
        return new Object[][] {
                { new HeaderSource(JwtHttpConstants.HEADER_AUTHORIZATION, JwtHttpConstants.AUTH_SCHEME_BEARER) },
                { new HeaderSource("X-Custom", "Token") },
                { new HeaderSource("X-API-Key", null) }
        };
    }

    @Test(dataProvider = "sources")
    public void givenHeaderSource_whenToString_thenAppropriateStringRepresentationIsReturned(HeaderSource source) {
        // Given

        // When
        String value = source.toString();

        // Then
        Assert.assertTrue(StringUtils.contains(value, source.getHeader()));
        Assert.assertEquals(StringUtils.contains(value, source.getPrefix()),
                            StringUtils.isNotBlank(source.getPrefix()));
    }
}
