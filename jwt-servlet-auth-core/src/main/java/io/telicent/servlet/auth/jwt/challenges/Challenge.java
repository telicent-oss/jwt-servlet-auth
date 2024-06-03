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
package io.telicent.servlet.auth.jwt.challenges;

import java.util.Objects;

/**
 * Represents a pending authentication challenge to be issued
 */
public class Challenge {

    private final int statusCode;
    private final String errorCode, errorDescription;

    /**
     * Creates a new challenge
     *
     * @param statusCode       Status Code
     * @param errorCode        Error Code
     * @param errorDescription Error description
     */
    public Challenge(int statusCode, String errorCode, String errorDescription) {
        this.statusCode = statusCode;
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode cannot be null");
        this.errorDescription = Objects.requireNonNull(errorDescription, "errorDescription cannot be null");
    }

    /**
     * Gets the status code for the challenge
     *
     * @return Status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the error code for the challenge
     *
     * @return Error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the error description for the challenge
     *
     * @return Error description
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Challenge{statusCode=")
               .append(statusCode)
               .append(", errorCode=")
               .append(errorCode)
               .append(", errorDescription=")
               .append(errorDescription)
               .append("}");
        return builder.toString();
    }
}
