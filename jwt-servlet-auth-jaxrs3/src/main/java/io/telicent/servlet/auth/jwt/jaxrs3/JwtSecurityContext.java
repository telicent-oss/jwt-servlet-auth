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
package io.telicent.servlet.auth.jwt.jaxrs3;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.telicent.servlet.auth.jwt.JwtHttpConstants;
import io.telicent.servlet.auth.jwt.configuration.ClaimPath;
import io.telicent.servlet.auth.jwt.roles.RolesHelper;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.util.*;

/**
 * A JAX-RS {@link SecurityContext} based upon an authenticated JWT
 */
public class JwtSecurityContext implements SecurityContext {
    private final Jws<Claims> jws;
    private final boolean isSecure;
    private final String username;
    private final RolesHelper rolesHelper;

    /**
     * Creates a new security context
     *
     * @param jws              The verified JWT
     * @param username         Username extracted from the JWT (if any)
     * @param wasSecureChannel Whether the request was authenticated on a secure channel i.e. HTTPS
     * @param rolesClaim       Claim from which to extract the list of roles to use when answering
     *                         {@link #isUserInRole(String)} calls
     */
    public JwtSecurityContext(Jws<Claims> jws, String username, boolean wasSecureChannel, ClaimPath rolesClaim) {
        this.jws = Objects.requireNonNull(jws, "JWT cannot be null");
        this.username = username;
        this.isSecure = wasSecureChannel;
        this.rolesHelper = createRolesHelper(jws, rolesClaim);
    }

    /**
     * Creates a roles helper
     * <p>
     * May be overridden if an implementation needs to override how roles information is extracted from the JWT
     * </p>
     *
     * @param jws        JWT
     * @param rolesClaim Roles claim
     * @return Roles helper
     */
    protected RolesHelper createRolesHelper(Jws<Claims> jws, ClaimPath rolesClaim) {
        return new RolesHelper(jws, rolesClaim);
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> this.username;
    }

    @Override
    public boolean isUserInRole(String role) {
        if (this.rolesHelper == null) {
            return false;
        }
        return this.rolesHelper.isUserInRole(role);
    }

    @Override
    public boolean isSecure() {
        return this.isSecure;
    }

    @Override
    public String getAuthenticationScheme() {
        return JwtHttpConstants.AUTH_SCHEME_BEARER;
    }

    /**
     * Gets the verified JSON Web Token (JWT) for the request
     *
     * @return Verified JWT
     */
    public Jws<Claims> getVerifiedJwt() {
        return this.jws;
    }
}
