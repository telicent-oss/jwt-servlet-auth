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
package io.telicent.servlet.auth.jwt.jaxrs3.examples;

import io.telicent.servlet.auth.jwt.jaxrs3.JwtAuthFilter;
import jakarta.ws.rs.core.Application;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A toy JAX-RS application for integration testing
 */
public class HelloWorldApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new LinkedHashSet<>();
        classes.add(HelloWorldResource.class);
        // Add the JWT Auth Filter to our application
        classes.add(JwtAuthFilter.class);
        return classes;
    }
}
