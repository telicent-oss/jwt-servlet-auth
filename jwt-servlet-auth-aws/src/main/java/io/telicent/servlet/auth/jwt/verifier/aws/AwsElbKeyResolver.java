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

import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.security.InvalidKeyException;
import io.telicent.servlet.auth.jwt.verification.KeyUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Key;
import java.util.Objects;

/**
 * A key resolver that resolves AWS ELB public keys per <a
 * href="https://docs.aws.amazon.com/elasticloadbalancing/latest/application/listener-authenticate-users.html#user-claims-encoding">Authenticate
 * users using an Application Load Balancer</a>.
 */
public class AwsElbKeyResolver extends LocatorAdapter<Key> {

    private final String region;

    /**
     * Creates a new resolver
     *
     * @param region AWS region
     */
    public AwsElbKeyResolver(String region) {
        this.region = Objects.requireNonNull(region, "AWS region cannot be null");
    }

    @Override
    public Key locate(JwsHeader header) {
        if (StringUtils.isBlank(header.getKeyId())) {
            throw new InvalidKeyException(
                    "JWT contained no Key ID (kid) in Header, unable to resolve an AWS ELB Key without a valid Key ID");
        }

        try {
            String rawKeyUrl = AwsElbKeyUrlRegistry.prepareKeyUrl(this.region, header.getKeyId());
            URL keyUrl = new URL(rawKeyUrl);
            URLConnection connection = keyUrl.openConnection();
            try (InputStream input = connection.getInputStream()) {
                return KeyUtils.loadPublicKey(KeyUtils.EC, input);
            }
        } catch (Throwable e) {
            throw new InvalidKeyException(
                    String.format("Failed to resolve AWS ELB Key %s from URL %s: %s", header.getKeyId(),
                                  AwsElbKeyUrlRegistry.prepareKeyUrl(this.region, header.getKeyId()), e.getMessage()));
        }
    }

    @Override
    public String toString() {
        return "AwsElbKeyResolver{region=" + region + "}";
    }

}
