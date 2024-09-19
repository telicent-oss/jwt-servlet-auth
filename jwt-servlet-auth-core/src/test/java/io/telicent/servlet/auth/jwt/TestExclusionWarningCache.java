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

import com.github.valfirst.slf4jtest.LoggingEvent;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import org.slf4j.event.Level;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestExclusionWarningCache {

    private final FilterForTest filter = new FilterForTest();
    private final TestLogger logger = TestLoggerFactory.getTestLogger(AbstractJwtAuthFilter.class);

    @BeforeMethod
    public void prepareTest() {
        this.logger.clearAll();
        this.filter.resetCache();
    }

    @AfterClass
    public void teardown() {
        this.logger.clearAll();
    }

    private List<String> generatePaths(String basePath, int count) {
        List<String> paths = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            paths.add(basePath + i);
        }
        return paths;
    }

    private void verifyWarnings(long expectedWarnings) {
        Assert.assertEquals(countWarningsIssued(), expectedWarnings);
    }

    private long countWarningsIssued() {
        return this.logger.getAllLoggingEvents()
                          .stream()
                          .filter(e -> e.getLevel() == Level.WARN)
                          .map(LoggingEvent::getFormattedMessage)
                          .filter(l -> l.contains("is excluded from JWT Authentication"))
                          .count();
    }

    private List<String> findWarningPaths() {
        return this.logger.getAllLoggingEvents()
                          .stream()
                          .filter(e -> e.getLevel() == Level.WARN)
                          .map(e -> e.getArguments().get(0))
                          .map(Object::toString)
                          .toList();
    }

    @Test
    public void givenExcludedPath_whenCheckingForExclusion_thenWarningIsIssued() {
        // Given
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("/healthz");
        String path = "/healthz";

        // When
        checkForExclusion(path, exclusions);

        // Then
        verifyWarnings(1);
    }

    @Test
    public void givenExcludedPath_whenCheckingForExclusionMultipleTimes_thenWarningIsIssuedOnlyOnce() {
        // Given
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("/healthz");
        String path = "/healthz";

        // When
        for (int i = 0; i < 100; i++) {
            checkForExclusion(path, exclusions);
        }

        // Then
        verifyWarnings(1);
    }

    @Test
    public void givenManyExcludedPaths_whenCheckingForExclusions_thenWarningIsIssuedOnlyOncePerPath() {
        // Given
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("/status/*");
        List<String> paths = this.generatePaths("/status/", FilterForTest.EXCLUSIONS_CACHE_SIZE);

        // When
        checkAllExclusions(paths, exclusions);

        // Then
        verifyWarnings(paths.size());
        verifyAtLeastOneWarningPerPath(paths);
    }

    private void checkAllExclusions(List<String> paths, List<PathExclusion> exclusions) {
        for (String path : paths) {
            checkForExclusion(path, exclusions);
        }
    }

    private void checkForExclusion(String path, List<PathExclusion> exclusions) {
        boolean excluded = this.filter.isExcludedPath(path, exclusions);
        Assert.assertTrue(excluded);
    }

    private void verifyAtLeastOneWarningPerPath(List<String> paths) {
        List<String> warningsIssuesFor = findWarningPaths();
        for (String path : paths) {
            Assert.assertTrue(warningsIssuesFor.contains(path), "No warning found for path " + path);
        }
    }

    @Test
    public void givenTooManyExcludedPaths_whenCheckingForExclusions_thenSomeWarningsAreReissued() {
        // Given
        List<PathExclusion> exclusions = PathExclusion.parsePathPatterns("/status/*");
        List<String> paths = this.generatePaths("/status/", FilterForTest.EXCLUSIONS_CACHE_SIZE * 2);

        // When
        // Do this once to ensure every path is warned for at least once
        checkAllExclusions(paths, exclusions);
        // Now test a bunch of paths at random to randomly overfill the cache and cause some number of extra exclusion
        // warnings to be generated
        Random random = new Random();
        for (int i = 1; i <= 1000; i++) {
            checkForExclusion(paths.get(random.nextInt(paths.size())), exclusions);
        }

        // Then
        Assert.assertTrue(countWarningsIssued() > paths.size());
        verifyAtLeastOneWarningPerPath(paths);
    }

    private static final class FilterForTest extends AbstractJwtAuthFilter<String, String> {

        public void resetCache() {
            EXCLUSION_WARNINGS_CACHE.invalidateAll();
        }
    }
}
