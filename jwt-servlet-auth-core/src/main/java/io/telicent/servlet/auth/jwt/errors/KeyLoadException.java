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
package io.telicent.servlet.auth.jwt.errors;

/**
 * A checked exception representing a failure to load a required key
 */
public class KeyLoadException extends Exception {

    /**
     * Creates a new exception with the given message
     *
     * @param message Message
     */
    public KeyLoadException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with the given message and cause
     *
     * @param message Message
     * @param cause   Cause
     */
    public KeyLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception with the given cause
     *
     * @param cause Cause
     */
    public KeyLoadException(Throwable cause) {
        super(cause);
    }
}
