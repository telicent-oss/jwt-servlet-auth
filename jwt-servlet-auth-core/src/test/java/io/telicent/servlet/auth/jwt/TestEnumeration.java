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

import java.util.*;

public class TestEnumeration<T> implements Enumeration<T> {

    private final Queue<T> items = new LinkedList<>();

    public TestEnumeration(Collection<T> items) {
        for (T item : items) {
            this.items.add(item);
        }
    }

    @Override
    public boolean hasMoreElements() {
        return !this.items.isEmpty();
    }

    @Override
    public T nextElement() {
        if (this.items.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.items.poll();
    }

    @Override
    public Iterator<T> asIterator() {
        return this.items.iterator();
    }
}
