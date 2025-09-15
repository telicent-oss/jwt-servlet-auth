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
package io.telicent.servlet.auth.jwt.roles;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.telicent.servlet.auth.jwt.configuration.ClaimPath;
import io.telicent.servlet.auth.jwt.configuration.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A helper for obtaining roles information from a JWT
 * <p>
 * Roles information is obtained by extracting the configured claim and converting its value via the protected
 * {@link #loadRoles(Object)} method.  If your roles information is not provided in a format supported by this method
 * then you can extend this helper and override that method accordingly.
 * </p>
 */
public class RolesHelper {
    private final Jws<Claims> jws;
    private final ClaimPath rolesClaim;
    private Set<String> roles = null;

    /**
     * Creates a new roles helper
     *
     * @param jws        Verified JWT
     * @param rolesClaim Roles claim path that contains the users roles information
     */
    public RolesHelper(Jws<Claims> jws, ClaimPath rolesClaim) {
        this.jws = Objects.requireNonNull(jws, "JWT cannot be null");
        this.rolesClaim = rolesClaim;
    }

    /**
     * Gets whether the user has the given role
     *
     * @param role Role
     * @return True if they have the given role, false otherwise
     */
    public boolean isUserInRole(String role) {
        if (this.rolesClaim == null || this.rolesClaim.isEmpty()) {
            // No roles claim so user not considered to be in any role
            return false;
        }
        if (this.roles == null) {
            Object rawRoles = Utils.findClaim(this.jws, this.rolesClaim);
            this.roles = loadRoles(rawRoles);
        }
        return this.roles.contains(role);
    }

    /**
     * Loads the roles by converting the provided roles claim value
     * <p>
     * Provided a non-null value is returned the loaded roles are cached for the lifetime of the security context and
     * this method will not be called again.
     * </p>
     * <p>
     * This method supports role claims given in the following formats:
     * </p>
     * <ol>
     *     <li>If the claim value is a {@link String} then either return a single role, <strong>UNLESS</strong> the
     *     values contains a {@code ,} character in which case split the value into multiple roles.</li>
     *     <li>If the claim value is a {@link Collection} then convert the collection values to strings and use those as
     *     the roles.</li>
     *     <li>If the claim value is an array of strings then use that as the roles.</li>
     *     <li>For any other value return an empty set.</li>
     * </ol>
     *
     * @param rawRoles Raw roles claim value, may be {@code null} if no such claim present in the JWT
     * @return Loaded roles
     */
    protected Set<String> loadRoles(Object rawRoles) {
        if (rawRoles == null) {
            return Collections.emptySet();
        } else if (rawRoles instanceof String singleRole) {
            // NB - Filter out empty roles and strip extra whitespace around role names
            if (Strings.CS.contains(singleRole, ",")) {
                //@formatter:off
                return Arrays.stream(StringUtils.split(singleRole, ","))
                             .filter(StringUtils::isNotBlank)
                             .map(StringUtils::strip)
                             .collect(Collectors.toSet());
                //@formatter:on
            } else if (StringUtils.isNotBlank(singleRole)) {
                return Collections.singleton(StringUtils.strip(singleRole));
            } else {
                return Collections.emptySet();
            }
        } else if (rawRoles instanceof Collection<?> roleSet) {
            // NB - Filter for nulls twice as objects may be non-null but could have a null string representation, also
            //      strip any extra whitespace around role names
            //@formatter:off
            return roleSet.stream()
                          .filter(Objects::nonNull)
                          .map(Object::toString)
                          .filter(Objects::nonNull)
                          .map(StringUtils::strip)
                          .collect(Collectors.toSet());
            //@formatter:on
        } else if (rawRoles instanceof String[] roleArray) {
            // NB - Filter for empty roles and strip any extra whitespace around role names
            //@formatter:off
            return Arrays.stream(roleArray)
                         .filter(StringUtils::isNotBlank)
                         .map(StringUtils::strip)
                         .collect(Collectors.toSet());
            //@formatter:on
        } else {
            return Collections.emptySet();
        }
    }
}
