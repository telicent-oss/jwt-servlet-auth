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

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * A registry of AWS Regions to ELB Key Lookup URLs
 */
public class AwsElbKeyUrlRegistry {

    /**
     * The default Key URL format used to locate an AWS ELB public key based upon a region and a Key ID
     */
    public static final String DEFAULT_KEY_URL_FORMAT = "https://public-keys.auth.elb.%s.amazonaws.com/%s";
    /**
     * The Key URL format for locating public keys for the US GovCloud West 1 Region
     */
    public static final String GOVCLOUD_WEST_KEY_URL_FORMAT =
            "https://s3-us-gov-west-1.amazonaws.com/aws-elb-public-keys-prod-us-gov-west-1/%s";
    /**
     * The Key URL format for locating public keys for the US GovCloud East 1 Region
     */
    public static final String GOVCLOUD_EAST_KEY_URL_FORMAT =
            "https://s3-us-gov-east-1.amazonaws.com/aws-elb-public-keys-prod-us-gov-east-1/%s";
    private static final Map<String, String> REGIONS_TO_URL_FORMATS = new HashMap<>();
    /**
     * US GovCloud West 1 region
     */
    public static final String REGION_US_GOV_WEST_1 = "us-gov-west-1";
    /**
     * US GovCloud East 1 region
     */
    public static final String REGION_US_GOV_EAST_1 = "us-gov-east-1";

    static {
        reset();
    }

    private AwsElbKeyUrlRegistry() {}

    /**
     * Resets the ELB URL Registry
     * <p>
     * Typically only needed in unit test scenarios
     * </p>
     */
    public static void reset() {
        REGIONS_TO_URL_FORMATS.clear();
        REGIONS_TO_URL_FORMATS.put(REGION_US_GOV_WEST_1, GOVCLOUD_WEST_KEY_URL_FORMAT);
        REGIONS_TO_URL_FORMATS.put(REGION_US_GOV_EAST_1, GOVCLOUD_EAST_KEY_URL_FORMAT);
    }

    /**
     * Gets the Key URL format for an AWS region
     *
     * @return Key URL Format
     */
    public static String lookupUrlFormat(String region) {
        return REGIONS_TO_URL_FORMATS.getOrDefault(region, DEFAULT_KEY_URL_FORMAT);
    }

    /**
     * Prepares the actual Key URL for looking up a Key with the given ID
     *
     * @param region AWS Region
     * @param keyId  Key ID
     * @return Key URL
     */
    public static String prepareKeyUrl(String region, String keyId) {
        String keyFormat = lookupUrlFormat(region);
        switch (StringUtils.countMatches(keyFormat, "%s")) {
            case 0:
                throw new IllegalArgumentException(
                        "Key URL Format for region " + region + " fails to include at least one %s for injecting the Key ID to lookup");
            case 1:
                return String.format(keyFormat, keyId);
            case 2:
                return String.format(keyFormat, region, keyId);
            default:
                throw new IllegalArgumentException(
                        "Key URL Format for region " + region + " contains too many %s specifiers");
        }
    }

    /**
     * Registers a region which needs a custom URL Format
     * <p>
     * Primarily intended only for unit test scenarios but could also be useful if your AWS deployment is particularly
     * quirky and your applications can't directly reach back to AWS ELB to obtain the public keys.  In which case you
     * could choose to customise the URL format to allow them to obtain the keys via some other service.
     * </p>
     *
     * @param region    Region
     * @param urlFormat URL Format, should contain at least one {@code %s} placeholder for injecting the Key ID.  If two
     *                  such placeholders are present then the Region and Key ID will be injected.
     */
    public static void register(String region, String urlFormat) {
        REGIONS_TO_URL_FORMATS.put(region, urlFormat);
    }
}
