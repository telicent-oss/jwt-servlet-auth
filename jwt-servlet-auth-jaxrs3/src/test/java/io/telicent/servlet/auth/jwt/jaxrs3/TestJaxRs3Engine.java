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

import io.telicent.servlet.auth.jwt.AbstractHeaderBasedEngineTests;
import io.telicent.servlet.auth.jwt.JwtAuthenticationEngine;
import io.telicent.servlet.auth.jwt.sources.HeaderSource;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import org.mockito.ArgumentCaptor;
import org.testng.Assert;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.telicent.servlet.auth.jwt.EqualsIgnoreCase.eqIgnoresCase;
import static org.mockito.Mockito.*;

public class TestJaxRs3Engine
        extends AbstractHeaderBasedEngineTests<ContainerRequestContext, ContainerResponseContext> {

    public static final URI TEST_REQUEST_URI;

    static {
        try {
            TEST_REQUEST_URI = new URI("https://example.org/test");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ContainerRequestContext createMockRequest(Map<String, String> headers) {
        return mockRequest(TEST_REQUEST_URI, headers);
    }

    public static ContainerRequestContext mockRequest(URI requestUri, Map<String, String> headers) {
        ContainerRequestContext request = mock(ContainerRequestContext.class);
        MultivaluedMap<String, String> mockHeaders = mock(MultivaluedMap.class);
        headers.forEach((key, value) -> {
            when(mockHeaders.containsKey(eqIgnoresCase(key))).thenReturn(true);
            when(mockHeaders.get(eqIgnoresCase(key))).thenReturn(Collections.singletonList(value));
        });
        when(mockHeaders.keySet()).thenReturn(headers.keySet());
        when(mockHeaders.entrySet()).thenReturn(
                headers.entrySet().stream().map(entry -> new Map.Entry<String, List<String>>() {
                    @Override
                    public String getKey() {
                        return entry.getKey();
                    }

                    @Override
                    public List<String> getValue() {
                        return Collections.singletonList(entry.getValue());
                    }

                    @Override
                    public List<String> setValue(List<String> value) {
                        return null;
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (o == null) {
                            return false;
                        }
                        if (this == o) {
                            return true;
                        }

                        if (!(o instanceof Map.Entry<?, ?>)) {
                            return false;
                        }

                        Map.Entry<String, List<String>> other = (Map.Entry<String, List<String>>) o;
                        return Objects.equals(this.getKey(), other.getKey()) && Objects.equals(this.getValue(),
                                                                                               other.getValue());
                    }

                    @Override
                    public int hashCode() {
                        return entry.hashCode();
                    }
                }).collect(Collectors.toSet()));
        when(request.getHeaders()).thenReturn(mockHeaders);
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUri()).thenReturn(requestUri);
        when(uriInfo.getPath()).thenReturn(requestUri.getPath().substring(1));
        when(request.getUriInfo()).thenReturn(uriInfo);
        return request;
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
                                                 usernameClaim != null ? List.of(usernameClaim) : null);
    }

    @Override
    protected JwtAuthenticationEngine<ContainerRequestContext, ContainerResponseContext> createEngine(
            List<HeaderSource> authHeaders, String realm, List<String> usernameClaims) {
        return new JaxRs3JwtAuthenticationEngine(authHeaders, realm, usernameClaims);
    }

    @Override
    protected boolean throwsOnUnexpectedErrors() {
        return true;
    }

    @Override
    protected void verifyStatusCode(ContainerRequestContext request, ContainerResponseContext response,
                                    int expectedStatus) {
        verifyStatusCode(request, expectedStatus);
    }

    public final static void verifyStatusCode(ContainerRequestContext request, int expectedStatus) {
        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captor.capture());
        Response actualResponse = captor.getValue();
        Assert.assertEquals(actualResponse.getStatus(), expectedStatus);
    }

    @Override
    protected String verifyHeaderPresent(ContainerRequestContext request, ContainerResponseContext response,
                                         String expectedHeader) {
        return verifyHeaderPresent(request, expectedHeader);
    }

    public final static String verifyHeaderPresent(ContainerRequestContext request, String expectedHeader) {
        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(request).abortWith(captor.capture());
        Response actualResponse = captor.getValue();
        List<String> challenges = actualResponse.getStringHeaders().get(expectedHeader);
        Assert.assertNotNull(challenges);
        Assert.assertFalse(challenges.isEmpty());
        return challenges.get(0);
    }

    @Override
    protected String getAuthenticatedUser(ContainerRequestContext authenticatedRequest) {
        return verifyAuthenticatedUser(authenticatedRequest);
    }

    public final static String verifyAuthenticatedUser(ContainerRequestContext authenticatedRequest) {
        ArgumentCaptor<SecurityContext> captor = ArgumentCaptor.forClass(SecurityContext.class);
        verify(authenticatedRequest).setSecurityContext(captor.capture());
        SecurityContext context = captor.getValue();
        return context.getUserPrincipal().getName();
    }
}
