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
package io.telicent.servlet.auth.jwt.verification;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Deserializer;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Provides JWT parser builders with a shared JSON deserializer to reduce per-parser overhead.
 */
public final class JwtParsers {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Deserializer<Map<String, ?>> DESERIALIZER = buildDeserializer();

    private JwtParsers() {
    }

    public static JwtParserBuilder builder() {
        JwtParserBuilder builder = Jwts.parser();
        builder.json(DESERIALIZER);
        return builder;
    }

    @SuppressWarnings("unchecked")
    /**
     * This workaround is due to the runtime dependencies for jjwt-jackson in the pom file
     */
    private static Deserializer<Map<String, ?>> buildDeserializer() {
        try {
            Class<?> clazz = Class.forName("io.jsonwebtoken.jackson.io.JacksonDeserializer");
            Constructor<?> ctor = clazz.getConstructor(ObjectMapper.class);
            return (Deserializer<Map<String, ?>>) ctor.newInstance(OBJECT_MAPPER);
        } catch (ReflectiveOperationException | LinkageError e) {
            return null;
        }
    }
}
