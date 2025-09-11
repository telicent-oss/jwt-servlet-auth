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
package io.telicent.servlet.auth.jwt.servlet3;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.telicent.servlet.auth.jwt.roles.RolesHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.security.Principal;

/**
 * An authenticated HTTP Servlet Request, which is a decorator around the original request
 */
public class AuthenticatedHttpServletRequest extends HttpServletRequestWrapper {

    private final Jws<Claims> jws;
    private final String username;
    private final RolesHelper rolesHelper;

    /**
     * Creates a new authenticated request
     *
     * @param request    Original request
     * @param jws        Verified JWT
     * @param username   Username extracted from the JWT
     * @param rolesClaim Roles claim
     */
    public AuthenticatedHttpServletRequest(HttpServletRequest request, Jws<Claims> jws, String username,
                                           String[] rolesClaim) {
        super(request);
        this.username = username;
        this.jws = jws;
        this.rolesHelper = createRolesHelper(jws, rolesClaim);
    }

    /**
     * Creates the roles helper used by the {@link #isUserInRole(String)} method
     *
     * @param jws        JWT
     * @param rolesClaim Roles claim
     * @return Roles helper
     */
    protected RolesHelper createRolesHelper(Jws<Claims> jws, String[] rolesClaim) {
        return new RolesHelper(jws, rolesClaim);
    }

    @Override
    public String getRemoteUser() {
        return this.username;
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.rolesHelper != null && this.rolesHelper.isUserInRole(role);
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> username;
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
