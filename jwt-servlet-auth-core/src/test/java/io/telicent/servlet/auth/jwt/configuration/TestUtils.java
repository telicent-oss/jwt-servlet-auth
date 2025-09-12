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

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class TestUtils {

    @Test
    public void givenNoJws_whenFindingClaim_thenNull() {
        // Given, When and Then
        Assert.assertNull(Utils.findClaim(null, new String[] { "some", "path" }));
    }

    @Test
    public void givenNoClaimPath_whenFindingClaim_thenNull() {
        // Given
        Jws<Claims> jws = Mockito.mock(Jws.class);

        // When and Then
        Assert.assertNull(Utils.findClaim(jws, null));
    }

    @Test
    public void givenEmptyClaimPath_whenFindingClaim_thenNull() {
        // Given
        Jws<Claims> jws = Mockito.mock(Jws.class);

        // When and Then
        Assert.assertNull(Utils.findClaim(jws, new String[0]));
    }

    @Test(expectedExceptions = ClassCastException.class)
    @SuppressWarnings("unused")
    public void givenTopLevelClaimPath_whenFindingClaimWithWrongType_thenClassCastException() {
        // Given
        Jws<Claims> jws = Mockito.mock(Jws.class);
        Claims claims = Mockito.mock(Claims.class);
        when(claims.get("test")).thenReturn("foo");
        when(jws.getPayload()).thenReturn(claims);

        // When and Then
        Integer value = Utils.findClaim(jws, new String[] { "test" });
    }

    @Test
    public void givenTopLevelClaimPath_whenFindingClaim_thenReturned() {
        // Given
        Jws<Claims> jws = Mockito.mock(Jws.class);
        Claims claims = Mockito.mock(Claims.class);
        List<String> value = List.of("a", "b");
        when(claims.get("test")).thenReturn(value);
        when(jws.getPayload()).thenReturn(claims);

        // When
        List<String> found = Utils.findClaim(jws, new String[] { "test" });

        // Then
        Assert.assertNotNull(found);
        Assert.assertEquals(found, value);
    }

    @Test
    public void givenDeeplyNestedClaimPath_whenFindingClaim_thenReturned() {
        // Given
        Jws<Claims> jws = Mockito.mock(Jws.class);
        Claims claims = Mockito.mock(Claims.class);
        when(claims.get("test")).thenReturn(Map.of("a", Map.of("b", Map.of("c", Map.of("d", true)))));
        when(jws.getPayload()).thenReturn(claims);

        // When
        Boolean found = Utils.findClaim(jws, new String[] { "test", "a", "b", "c", "d"});

        // Then
        Assert.assertNotNull(found);
        Assert.assertTrue(found);
    }
}
