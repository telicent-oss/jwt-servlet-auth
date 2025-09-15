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
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

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
        Jws<Claims> jws = Mockito.mock(Jws.class);

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
}
