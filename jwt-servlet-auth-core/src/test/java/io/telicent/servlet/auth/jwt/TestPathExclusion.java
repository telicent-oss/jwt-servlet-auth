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
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestPathExclusion {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenNullPattern_whenCreatingExclusion_thenIllegalArgument() {
        new PathExclusion(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenEmptyPattern_whenCreatingExclusion_thenIllegalArgument() {
        new PathExclusion("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenBlankPattern_whenCreatingExclusion_thenIllegalArgument() {
        new PathExclusion("    ");
    }

    @Test
    public void givenRegexChars_whenCreatingExclusion_thenTreatedAsLiterals() {
        PathExclusion exclusion = new PathExclusion("/path(unfinished_regex_clause*");

        Assert.assertTrue(exclusion.matches("/path(unfinished_regex_clause"));
        Assert.assertFalse(exclusion.matches("/path/unfinished_regex_clause"));
    }

    @DataProvider(name = "excludeAllPatterns")
    private Object[][] excludeAllPatterns() {
        return new Object[][] {
                { "*" },
                { "/*" },
                { "*/" },
                { "**" },
                { "*/*" },
                { " * " },
                { "/*/*" },
                { "/*/*/*" }
        };
    }

    @Test(dataProvider = "excludeAllPatterns", expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*excludes all paths")
    public void givenAnExcludeAllPattern_whenCreating_thenIllegalArgumentError(String pattern) {
        // Given, When and Then
        new PathExclusion(pattern);
    }

    @Test
    public void givenFixedPathExclusion_whenTestingPaths_thenOnlyExactPathIsMatched() {
        // Given
        PathExclusion exclusion = new PathExclusion("/fixed");
        verifyParsed(exclusion, false, "/fixed");

        // When and Then
        Assert.assertTrue(exclusion.matches("/fixed"));
        Assert.assertFalse(exclusion.matches("/fixed-thing"));
        Assert.assertFalse(exclusion.matches("/other"));
        verifyBlankPathsAreNotMatched(exclusion);
    }

    private static void verifyBlankPathsAreNotMatched(PathExclusion exclusion) {
        Assert.assertFalse(exclusion.matches(null));
        Assert.assertFalse(exclusion.matches(""));
        Assert.assertFalse(exclusion.matches("   "));
    }

    @Test
    public void givenWildcardPathExclusion_whenTestingPaths_thenSubPathsAreCorrectlyMatched() {
        // Given
        PathExclusion exclusion = new PathExclusion("/status/*");
        verifyParsed(exclusion, true, "/status/*");

        // When and Then
        Assert.assertTrue(exclusion.matches("/status/"));
        Assert.assertTrue(exclusion.matches("/status/healthz"));
        Assert.assertFalse(exclusion.matches("/fixed"));
        verifyBlankPathsAreNotMatched(exclusion);
    }

    @Test
    public void givenNullPatterns_whenParsingExclusions_thenNoExclusions() {
        // Given and When
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns(null);

        // Then
        Assert.assertEquals(exclusions.size(), 0);
    }

    @Test
    public void givenEmptyPatterns_whenParsingExclusions_thenNoExclusions() {
        // Given and When
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("");

        // Then
        Assert.assertEquals(exclusions.size(), 0);
    }

    @Test
    public void givenBlankPatterns_whenParsingExclusions_thenNoExclusions() {
        // Given and When
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("    ");

        // Then
        Assert.assertEquals(exclusions.size(), 0);
    }

    @Test
    public void givenMultiplePatterns_whenParsingExclusions_thenAsExpected() {
        // Given and When
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("/fixed,/status/*");
        Assert.assertEquals(exclusions.size(), 2);

        // Then
        verifyParsed(exclusions.get(0), false, "/fixed");
        verifyParsed(exclusions.get(1), true, "/status/*");
    }

    private static void verifyParsed(PathExclusion exclusion, boolean shouldBeWildcard, String expectedPattern) {
        Assert.assertEquals(exclusion.isWildcard(), shouldBeWildcard);
        Assert.assertEquals(exclusion.getPattern(), expectedPattern);
    }

    @Test
    public void givenMultiplePatternsWithSomeEmpty_whenParsingExclusions_thenEmptyPatternsIgnored() {
        // Given and When
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("/fixed,,/status/*,");

        // Then
        Assert.assertEquals(exclusions.size(), 2);
        verifyParsed(exclusions.get(0), false, "/fixed");
        verifyParsed(exclusions.get(1), true, "/status/*");
    }

    @Test
    public void givenAllEmptyPatterns_whenParsingExclusions_thenEmptyPatternsIgnored() {
        // Given and When
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns(",,,");

        // Then
        Assert.assertEquals(exclusions.size(), 0);
    }

    @Test
    public void givenAllBlankPatterns_whenParsingExclusions_thenBlankPatternsIgnored() {
        // Given and When
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns(",  ,  ,");

        // Then
        Assert.assertEquals(exclusions.size(), 0);
    }

    @Test
    public void givenWildcardExclusionWithRegexChars_whenTestingForExclusion_thenFails() {
        // Given
        PathExclusion exclusion = new PathExclusion("/$/status/*");

        // When
        boolean excluded = exclusion.matches("/$/status/health");

        // Then
        Assert.assertTrue(excluded);
    }

    @Test
    public void givenWildcardExclusionWithEscapedRegexChars_whenTestingForExclusion_thenSuccess() {
        // Given
        PathExclusion exclusion = new PathExclusion("/\\$/status/*");

        // When
        boolean excluded = exclusion.matches("/$/status/health");

        // Then
        Assert.assertFalse(excluded);
    }

    @Test
    public void givenPathExclusions_whenInsertingIntoMaps_thenUsableAsKeys() {
        // Given
        PathExclusion a = new PathExclusion("/a");
        PathExclusion aWild = new PathExclusion("/a/*");
        PathExclusion b = new PathExclusion("/b");
        Map<PathExclusion, Integer> map = new HashMap<PathExclusion, Integer>();

        // When
        map.put(a, 1);
        map.put(aWild, 2);
        map.put(b, 3);

        // Then
        Assert.assertEquals(map.size(), 3);
        Assert.assertEquals(map.get(a), 1);
        Assert.assertEquals(map.get(aWild), 2);
        Assert.assertEquals(map.get(b), 3);
    }

    @Test
    public void givenTwoInstancesOfSamePathExclusion_whenComparingForEquality_thenEqual() {
        // Given
        PathExclusion x = new PathExclusion("/a");
        PathExclusion y = new PathExclusion("/a");

        // When and Then
        Assert.assertEquals(x, y);
        Assert.assertEquals(y, x);
        Assert.assertEquals(x.hashCode(), y.hashCode());
    }

    @DataProvider(name = "differentPatterns")
    private Object[][] differentPatterns() {
        return new Object[][] {
                { "/x", "/y"},
                { "/x/", "/x/*"},
                { "/x/*", "/x" },
                { "/a/b", "/a/c"},
                { "/a", "/a/b/c/d/e/f"}
        };
    }

    @Test(dataProvider = "differentPatterns")
    public void givenDifferentPathExclusions_whenComparingForEquality_thenNotEqual(String x, String y) {
        // Given
        PathExclusion a = new PathExclusion(x);
        PathExclusion b = new PathExclusion(y);

        // When and Then
        Assert.assertNotEquals(a, b);
        Assert.assertNotEquals(b, a);
        Assert.assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void givenPathExclusion_whenComparingEqualityWithSelf_thenEqual() {
        // Given
        PathExclusion x = new PathExclusion("/a");

        // When and Then
        Assert.assertTrue(x.equals(x));
    }

    @Test
    public void givenPathExclusion_whenComparingEqualityWithNull_thenNotEqual() {
        // Given
        PathExclusion x = new PathExclusion("/a");

        // When and Then
        Assert.assertFalse(x.equals(null));
    }

    @Test
    public void givenPathExclusion_whenComparingEqualityWithAnotherType_thenNotEqual() {
        // Given
        PathExclusion x = new PathExclusion("/a");

        // When and Then
        Assert.assertFalse(x.equals(new Object()));
    }

    @Test
    public void givenPathExclusionAsMapKey_whenRetrievingViaDifferentInstance_thenSuccess() {
        // Given
        PathExclusion x = new PathExclusion("/a");
        Map<PathExclusion, Integer> map = new HashMap<>();
        map.put(x, 1);

        // When and Then
        PathExclusion y = new PathExclusion("/a");
        Assert.assertEquals(map.get(y), 1);
    }

    @Test
    public void givenFixedPattern_whenToString_thenCorrect() {
        // Given
        PathExclusion fixed = new PathExclusion("/fixed");

        // When
        String value = fixed.toString();

        // Then
        Assert.assertTrue(Strings.CS.contains(value, "wildcard=false"));
        Assert.assertTrue(Strings.CS.contains(value, "pattern=/fixed"));
    }

    @Test
    public void givenWildcardPattern_whenToString_thenCorrect() {
        // Given
        PathExclusion wildcard = new PathExclusion("/status/*");

        // When
        String value = wildcard.toString();

        // Then
        Assert.assertTrue(Strings.CS.contains(value, "wildcard=true"));
        Assert.assertTrue(Strings.CS.contains(value, "pattern=/status/*"));
    }
}
