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
package io.telicent.servlet.auth.jwt.verifier.aws;

/**
 * Useful constants related to AWS
 */
public class AwsConstants {

    private AwsConstants() {}

    /**
     * HTTP Header added by AWS ELB that contains a JWT with claims about the user
     */
    public static final String HEADER_DATA = "X-Amzn-Oidc-Data";

    /**
     * HTTP Header added by AWS ELB that contains a JWT access token
     */
    public static final String HEADER_ACCESS_TOKEN = "X-Amzn-Oidc-AccessToken";

    /**
     * HTTP Header added by AWS ELB that contains just the users identity
     */
    public static final String HEADER_IDENTITY = "X-Amzn-Oidc-Identity";
}
