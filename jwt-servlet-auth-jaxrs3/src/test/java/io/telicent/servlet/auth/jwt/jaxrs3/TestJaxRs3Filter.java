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

import io.telicent.servlet.auth.jwt.*;
import io.telicent.servlet.auth.jwt.sources.HeaderSource;
import io.telicent.servlet.auth.jwt.verification.JwtVerifier;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import org.testng.Assert;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TestJaxRs3Filter
        extends AbstractFilterTests<ContainerRequestContext, ContainerResponseContext, JwtAuthFilter> {

    @Override
    protected ContainerRequestContext createMockRequest(Map<String, String> headers) {
        return TestJaxRs3Engine.mockRequest(TestJaxRs3Engine.TEST_REQUEST_URI, headers);
    }

    @Override
    protected ContainerRequestContext createMockRequest(String requestPath, Map<String, String> headers) {
        return TestJaxRs3Engine.mockRequest(URI.create(requestPath), headers);
    }

    @Override
    protected ContainerResponseContext createMockResponse() {
        return mock(ContainerResponseContext.class);
    }

    @Override
    protected JwtAuthenticationEngine<ContainerRequestContext, ContainerResponseContext> createEngine() {
        return new JaxRs3JwtAuthenticationEngine();
    }

    @Override
    protected JwtAuthenticationEngine<ContainerRequestContext, ContainerResponseContext> createEngine(String authHeader,
                                                                                                      String authHeaderPrefix,
                                                                                                      String realm,
                                                                                                      String usernameClaim) {
        return new JaxRs3JwtAuthenticationEngine(List.of(new HeaderSource(authHeader, authHeaderPrefix)), realm,
                                                 usernameClaim != null ? List.of(usernameClaim) : null, null);
    }

    @Override
    protected JwtAuthenticationEngine<ContainerRequestContext, ContainerResponseContext> createEngine(
            List<HeaderSource> authHeaders, String realm, List<String> usernameClaims) {
        return new JaxRs3JwtAuthenticationEngine(authHeaders, realm, usernameClaims, null);
    }

    @Override
    protected void verifyStatusCode(ContainerRequestContext request, ContainerResponseContext response,
                                    int expectedStatus) {
        TestJaxRs3Engine.verifyStatusCode(request, expectedStatus);
    }

    @Override
    protected String verifyHeaderPresent(ContainerRequestContext request, ContainerResponseContext response,
                                         String expectedHeader) {
        return TestJaxRs3Engine.verifyHeaderPresent(request, expectedHeader);
    }

    @Override
    protected String getAuthenticatedUser(ContainerRequestContext authenticatedRequest) {
        return TestJaxRs3Engine.verifyAuthenticatedUser(authenticatedRequest);
    }

    @Override
    protected String getAuthenticatedUser(JwtAuthFilter filter, ContainerRequestContext authenticatedRequest) {
        return getAuthenticatedUser(authenticatedRequest);
    }

    @Override
    protected void verifyNoChallenge(ContainerRequestContext request, ContainerResponseContext response) {
        verify(request, never()).abortWith(any());
    }

    @Override
    protected JwtAuthFilter createFilter(
            JwtAuthenticationEngine<ContainerRequestContext, ContainerResponseContext> engine, JwtVerifier verifier,
            List<PathExclusion> exclusions) {
        ServletContext context = mock(ServletContext.class);
        when(context.getAttribute(eq(JwtServletConstants.ATTRIBUTE_JWT_ENGINE))).thenReturn(engine);
        when(context.getAttribute(eq(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER))).thenReturn(verifier);
        when(context.getAttribute(eq(JwtServletConstants.ATTRIBUTE_PATH_EXCLUSIONS))).thenReturn(exclusions);

        JwtAuthFilter filter = new JwtAuthFilter();
        filter.setContext(context);
        return filter;
    }

    @Override
    protected void invokeFilter(JwtAuthFilter filter, ContainerRequestContext containerRequestContext,
                                ContainerResponseContext containerResponseContext) {
        try {
            filter.filter(containerRequestContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object verifyRequestAttribute(ContainerRequestContext containerRequestContext, String attribute) {
        Object value = containerRequestContext.getProperty(attribute);
        Assert.assertNotNull(value);
        return value;
    }
}
