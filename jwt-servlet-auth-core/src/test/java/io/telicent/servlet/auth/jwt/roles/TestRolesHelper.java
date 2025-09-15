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
package io.telicent.servlet.auth.jwt.roles;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.telicent.servlet.auth.jwt.configuration.ClaimPath;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class TestRolesHelper {

    private static final String[] USER_AND_ADMIN = { "user", "admin" };

    @Test(expectedExceptions = NullPointerException.class)
    public void givenNullToken_whenCreatingHelper_thenNPE() {
        // Given, When and Then
        new RolesHelper(null, null);
    }

    @Test
    public void givenTokenAndNullRolesClaim_whenUsingHelper_thenUserNotInAnyRoles() {
        // Given
        Jws<Claims> jwt = Mockito.mock(Jws.class);

        // When
        RolesHelper helper = new RolesHelper(jwt, null);

        // Then
        Assert.assertFalse(helper.isUserInRole("test"));
    }

    @Test
    public void givenTokenAndEmptyRolesClaim_whenUsingHelper_thenUserNotInAnyRoles() {
        // Given
        Jws<Claims> jwt = Mockito.mock(Jws.class);

        // When
        RolesHelper helper = new RolesHelper(jwt, ClaimPath.EMPTY);

        // Then
        Assert.assertFalse(helper.isUserInRole("test"));
    }

    @Test
    public void givenEmptyTokenAndRolesClaim_whenUsingHelper_thenUserNotInAnyRoles() {
        // Given
        Jws<Claims> jwt = Mockito.mock(Jws.class);
        ClaimPath rolesClaim = ClaimPath.topLevel("roles");

        // When
        RolesHelper helper = new RolesHelper(jwt, rolesClaim);

        // Then
        Assert.assertFalse(helper.isUserInRole("test"));
    }

    private record Dummy(String toStringForm) {

        @Override
        public String toString() {
            return this.toStringForm;
        }
    }

    @DataProvider(name = "supportedRolesFormats")
    private Object[][] supportedRoles() {
        //@formatter:off
        return new Object[][] {
                // Single string
                { "user", new String[] { "user" } },
                // String with comma separated list of strings
                { "user,admin", USER_AND_ADMIN },
                // Comma separated string with excess whitespace and a blank entry
                { "  user , , admin ", USER_AND_ADMIN },
                // Collections of strings
                { List.of("user", "admin"), USER_AND_ADMIN },
                { Set.of("user", "admin"), USER_AND_ADMIN },
                // Collections with empty and duplicate entries plus excess whitespace in some entries
                { List.of("user", "", " admin   ", "", "admin"), USER_AND_ADMIN },
                // Array of strings
                { USER_AND_ADMIN, USER_AND_ADMIN },
                // Collection of objects which are convertible to string
                { List.of(new Dummy("user"), new Dummy(null), new Dummy("admin  ")), USER_AND_ADMIN },
                { Arrays.asList(null, new Dummy("admin"), new Dummy("user")), USER_AND_ADMIN },
        };
        //@formatter:on
    }

    @Test(dataProvider = "supportedRolesFormats")
    public void givenTokenWithRoles_whenUsingHelper_thenUserInDeclaredRoles(Object rolesValue, String[] expected) {
        // Given
        Jws<Claims> jwt = Mockito.mock(Jws.class);
        Claims claims = Mockito.mock(Claims.class);
        when(claims.get("roles")).thenReturn(rolesValue);
        when(jwt.getPayload()).thenReturn(claims);

        // When
        RolesHelper helper = new RolesHelper(jwt, ClaimPath.topLevel("roles"));

        // Then
        for (String role : expected) {
            Assert.assertTrue(helper.isUserInRole(role));
        }
        Assert.assertFalse(helper.isUserInRole("test"));
    }

    @Test(dataProvider = "supportedRolesFormats")
    public void givenTokenWithNestedRoles_whenUsingHelper_thenUserInDeclaredRoles(Object rolesValue,
                                                                                  String[] expected) {
        // Given
        Jws<Claims> jwt = Mockito.mock(Jws.class);
        Claims claims = Mockito.mock(Claims.class);
        when(claims.get("details")).thenReturn(Map.of("roles", rolesValue));
        when(jwt.getPayload()).thenReturn(claims);

        // When
        RolesHelper helper = new RolesHelper(jwt, ClaimPath.of("details", "roles"));

        // Then
        for (String role : expected) {
            Assert.assertTrue(helper.isUserInRole(role));
        }
        Assert.assertFalse(helper.isUserInRole("test"));
    }

    @Test(dataProvider = "supportedRolesFormats")
    public void givenTokenWithDeeplyNestedRoles_whenUsingHelper_thenUserInDeclaredRoles(Object rolesValue,
                                                                                        String[] expected) {
        // Given
        Jws<Claims> jwt = Mockito.mock(Jws.class);
        Claims claims = Mockito.mock(Claims.class);
        when(claims.get("some")).thenReturn(Map.of("deep", Map.of("path", rolesValue)));
        when(jwt.getPayload()).thenReturn(claims);

        // When
        RolesHelper helper = new RolesHelper(jwt, ClaimPath.of("some", "deep", "path"));

        // Then
        for (String role : expected) {
            Assert.assertTrue(helper.isUserInRole(role));
        }
        Assert.assertFalse(helper.isUserInRole("test"));
    }

    @Test(dataProvider = "supportedRolesFormats")
    public void givenTokenWithDeeplyNestedRolesAtWrongPath_whenUsingHelper_thenUserNotInDeclaredRoles(Object rolesValue,
                                                                                                      String[] expected) {
        // Given
        Jws<Claims> jwt = Mockito.mock(Jws.class);
        Claims claims = Mockito.mock(Claims.class);
        when(claims.get("some")).thenReturn(Map.of("other", Map.of("path", rolesValue)));
        when(jwt.getPayload()).thenReturn(claims);

        // When
        RolesHelper helper = new RolesHelper(jwt, ClaimPath.of("some", "deep", "path"));

        // Then
        for (String role : expected) {
            Assert.assertFalse(helper.isUserInRole(role));
        }
        Assert.assertFalse(helper.isUserInRole("test"));
    }

    @Test(dataProvider = "supportedRolesFormats")
    public void givenTokenWithDeeplyNestedRolesWronglyNested_whenUsingHelper_thenUserNotInDeclaredRoles(
            Object rolesValue,
            String[] expected) {
        // Given
        Jws<Claims> jwt = Mockito.mock(Jws.class);
        Claims claims = Mockito.mock(Claims.class);
        when(claims.get("some")).thenReturn(Map.of("deep", List.of(Map.of("path", rolesValue))));
        when(jwt.getPayload()).thenReturn(claims);

        // When
        RolesHelper helper = new RolesHelper(jwt, ClaimPath.of("some", "deep", "path"));

        // Then
        for (String role : expected) {
            Assert.assertFalse(helper.isUserInRole(role));
        }
        Assert.assertFalse(helper.isUserInRole("test"));
    }

    @DataProvider(name = "unsupportedRolesFormats")
    private Object[][] unsupportedRoles() {
        return new Object[][] {
                // Maps are not supported
                { Map.of("user", "true", "admin", "false") },
                // Other primitive types not supported
                { 123 },
                { true },
                { 12.3e4 }
        };
    }

    @Test(dataProvider = "unsupportedRolesFormats")
    public void givenTokenWithUnsupportedRolesData_whenUsingHelper_thenUserNotInAnyRoles(Object unsupportedRolesValue) {
        // Given
        Jws<Claims> jwt = Mockito.mock(Jws.class);
        Claims claims = Mockito.mock(Claims.class);
        when(claims.get("roles")).thenReturn(unsupportedRolesValue);
        when(jwt.getPayload()).thenReturn(claims);

        // When
        RolesHelper helper = new RolesHelper(jwt, ClaimPath.topLevel("roles"));

        // Then
        Assert.assertFalse(helper.isUserInRole("test"));
    }
}
