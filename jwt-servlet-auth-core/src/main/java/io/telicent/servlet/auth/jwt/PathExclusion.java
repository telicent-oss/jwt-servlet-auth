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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Represents a path exclusion
 */
public class PathExclusion {

    private final boolean wildcard;
    private final String pattern;
    private final Pattern regex;

    /**
     * Creates a new path exclusion
     *
     * @param pathPattern Path exclusion pattern
     */
    public PathExclusion(String pathPattern) {
        if (StringUtils.isBlank(pathPattern)) {
            throw new IllegalArgumentException("Cannot have a blank path exclusion");
        } else if (StringUtils.containsOnly(pathPattern, " /*")) {
            throw new IllegalArgumentException("Cannot have a path exclusion that excludes all paths");
        }

        this.wildcard = StringUtils.contains(pathPattern, "*");
        this.regex = this.wildcard ? parsePathPattern(pathPattern) : null;
        this.pattern = pathPattern;
    }

    /**
     * Parses raw path patterns, specified as a comma separated list of strings, into a list of path exclusions
     *
     * @param rawPathPatterns Raw path patterns
     * @return List of path exclusions
     */
    public static List<PathExclusion> parsePathPatterns(String rawPathPatterns) {
        if (StringUtils.isBlank(rawPathPatterns))
            return Collections.emptyList();

        String[] rawPatterns = StringUtils.split(rawPathPatterns, ",");
        List<PathExclusion> exclusions = new ArrayList<>();
        for (String rawPattern : rawPatterns) {
            if (StringUtils.isBlank(rawPattern))
                continue;
            exclusions.add(new PathExclusion(rawPattern));
        }
        return exclusions;
    }

    /**
     * Parses a path pattern into a regular expression
     * <p>
     * Raw path patterns may use {@code *} as a wildcard, when converting to a regex we want a {@code *} to match zero
     * or more characters so need to convert into the appropriate regex syntax
     * </p>
     *
     * @param pathPattern Path pattern
     * @return Compiled regular expression
     */
    private static Pattern parsePathPattern(String pathPattern) {
        try {
            return Pattern.compile(pathPattern.replace("*", ".*"));
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException(
                    String.format("Path pattern %s can not be converted into a valid regular expression", pathPattern),
                    e);
        }
    }

    /**
     * Gets whether this exclusion is a wildcard i.e. whether it can match many paths
     *
     * @return True if a wildcard, false otherwise
     */
    public boolean isWildcard() {
        return this.wildcard;
    }

    /**
     * Gets the pattern for this exclusion
     *
     * @return Pattern
     */
    public String getPattern() {
        return this.pattern;
    }


    /**
     * Gets whether the given path matches this exclusion
     *
     * @param path Path
     * @return True if the exclusion matches, false otherwise
     */
    public boolean matches(String path) {
        if (StringUtils.isBlank(path)) {
            return false;
        } else if (this.wildcard) {
            return this.regex.matcher(path).matches();
        } else {
            return StringUtils.equals(this.pattern, path);
        }
    }
}
