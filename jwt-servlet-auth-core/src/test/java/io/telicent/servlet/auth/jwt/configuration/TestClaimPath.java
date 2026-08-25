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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class TestClaimPath {

    @Test
    public void givenTopLevelClaimPath_whenInspecting_thenReportsTopLevel() {
        // Given
        ClaimPath path = ClaimPath.topLevel("test");

        // When and Then
        Assert.assertTrue(path.isTopLevel());
    }

    @Test
    public void givenNestedClaimPath_whenInspectign_thenNotTopLevel() {
        // Given
        ClaimPath path = ClaimPath.of("some", "nested", "path");

        // When and Then
        Assert.assertFalse(path.isTopLevel());
    }

    @Test
    public void givenEmptyClaimPath_whenFindingValue_thenNull() {
        // Given
        ClaimPath path = ClaimPath.EMPTY;
        Jws<Claims> jws = mock(Jws.class);

        // When and Then
        Assert.assertNull(path.find(jws));
    }

    @Test
    public void givenClaimPath_whenFindingValueForNullJwt_thenNull() {
        // Given
        ClaimPath path = ClaimPath.of("a", "b");

        // When and Then
        Assert.assertNull(path.find(null));
    }

    @Test
    public void givenNullPathElements_whenCreatingClaimPath_thenPathIsEmpty() {
        // Given and When
        ClaimPath path = new ClaimPath(null);

        // Then
        Assert.assertTrue(path.isEmpty());
    }

    @Test
    public void givenEmptyPathElements_whenCreatingClaimPath_thenPathIsEmpty() {
        // Given and When
        ClaimPath path = new ClaimPath(new String[0]);

        // Then
        Assert.assertTrue(path.isEmpty());
    }

    @Test
    public void givenNullList_whenCreatingClaimPath_thenEmptyPath() {
        // Given and When
        ClaimPath path = ClaimPath.of((List<String>) null);

        // Then
        Assert.assertTrue(path.isEmpty());
    }

    @Test
    public void givenEmptyList_whenCreatingClaimPath_thenEmptyPath() {
        // Given and When
        ClaimPath path = ClaimPath.of(Collections.emptyList());

        // Then
        Assert.assertTrue(path.isEmpty());
    }

    @Test
    public void givenClaimPath_whenGettingPathElements_thenACopyIsReturned() {
        // Given
        ClaimPath path = ClaimPath.of("realm_access", "roles");

        // When
        String[] elements = path.path();
        elements[0] = "mutated";

        // Then
        Assert.assertEquals(path.path()[0], "realm_access");
    }

    @Test
    public void givenNullPathElements_whenGettingPathElements_thenNull() {
        // Given
        ClaimPath path = new ClaimPath(null);

        // When and Then
        Assert.assertNull(path.path());
    }

    @Test
    public void givenSourceArray_whenMutatedAfterConstruction_thenClaimPathIsUnaffected() {
        // Given
        String[] source = { "realm_access", "roles" };
        ClaimPath path = new ClaimPath(source);

        // When
        source[0] = "mutated";

        // Then
        Assert.assertEquals(path.path()[0], "realm_access");
        Assert.assertEquals(path, ClaimPath.of("realm_access", "roles"));
    }

    @Test
    public void givenClaimPathsWithSameElements_whenComparing_thenEqual_andHashCodesMatch() {
        // Given
        ClaimPath a = ClaimPath.of("realm_access", "roles");
        ClaimPath b = ClaimPath.of("realm_access", "roles");

        // When and Then
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void givenClaimPathsWithDifferentElements_whenComparing_thenNotEqual() {
        // Given
        ClaimPath a = ClaimPath.of("realm_access", "roles");
        ClaimPath b = ClaimPath.of("resource_access", "roles");

        // When and Then
        Assert.assertNotEquals(a, b);
    }

    @Test
    public void givenClaimPath_whenComparingToNullAndOtherTypes_thenNotEqual() {
        // Given
        ClaimPath path = ClaimPath.of("realm_access");
        Object nullReference = null;
        Object notAClaimPath = "realm_access";

        // When and Then
        Assert.assertFalse(path.equals(nullReference));
        Assert.assertFalse(path.equals(notAClaimPath));
    }

    @Test
    public void givenClaimPath_whenUsedAsAMapKey_thenAnEquivalentPathFindsTheEntry() {
        // Given
        Map<ClaimPath, String> cache = new HashMap<>();
        cache.put(ClaimPath.of("realm_access", "roles"), "cached");

        // When
        String found = cache.get(ClaimPath.of("realm_access", "roles"));

        // Then
        Assert.assertEquals(found, "cached", "Equivalent claim paths must resolve to the same map entry");
    }

    @Test
    public void givenClaimPath_whenConvertingToString_thenElementsAreRendered() {
        // Given
        ClaimPath path = ClaimPath.of("realm_access", "roles");

        // When and Then
        Assert.assertEquals(path.toString(), "ClaimPath[path=[realm_access, roles]]");
    }

    @Test
    public void givenNullPathElements_whenCheckingIfTopLevel_thenFalse() {
        // Given
        ClaimPath path = new ClaimPath(null);

        // When and Then
        Assert.assertFalse(path.isTopLevel());
    }
}
