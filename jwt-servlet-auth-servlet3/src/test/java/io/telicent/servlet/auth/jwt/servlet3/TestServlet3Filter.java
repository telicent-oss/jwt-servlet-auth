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

import io.telicent.servlet.auth.jwt.*;
import io.telicent.servlet.auth.jwt.configuration.RuntimeConfigurationAdaptor;
import io.telicent.servlet.auth.jwt.sources.HeaderSource;
import io.telicent.servlet.auth.jwt.verification.JwtVerifier;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.Assert;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TestServlet3Filter extends
        AbstractConfigurableFilterTests<HttpServletRequest, HttpServletResponse, JwtAuthFilter> {
    protected ServletContext context;

    @Override
    protected HttpServletRequest createMockRequest(String requestUri, Map<String, String> headers) {
        HttpServletRequest request = TestServlet3Engine.mockRequest(URI.create(requestUri), headers);
        when(request.getServletContext()).thenReturn(this.context);
        return request;
    }

    @Override
    protected HttpServletRequest createMockRequest(FilterConfigAdaptorWrapper config, Map<String, String> headers) {
        HttpServletRequest request = TestServlet3Engine.mockRequest(null, headers);
        ServletContext context = mock(ServletContext.class);
        when(context.getAttribute(any())).thenAnswer(a -> config.getAttribute(a.getArgument(0, String.class)));
        when(request.getServletContext()).thenReturn(context);
        return request;
    }

    @Override
    protected JwtAuthFilter createFilter(JwtAuthenticationEngine<HttpServletRequest, HttpServletResponse> engine,
                                         JwtVerifier verifier, List<PathExclusion> exclusions) {
        this.context = null;
        ServletContext context = mock(ServletContext.class);
        when(context.getAttribute(eq(JwtServletConstants.ATTRIBUTE_JWT_ENGINE))).thenReturn(engine);
        when(context.getAttribute(eq(JwtServletConstants.ATTRIBUTE_JWT_VERIFIER))).thenReturn(verifier);
        when(context.getAttribute(eq(JwtServletConstants.ATTRIBUTE_PATH_EXCLUSIONS))).thenReturn(exclusions);
        this.context = context;

        return new JwtAuthFilter();
    }

    @Override
    protected void invokeFilter(JwtAuthFilter filter, HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse) {
        try {
            filter.doFilter(httpServletRequest, httpServletResponse, mock(FilterChain.class));
        } catch (IOException e) {
            Assert.fail("Unexpected IO Error: " + e.getMessage());
        } catch (ServletException e) {
            Assert.fail("Unexpected Servlet Error: " + e.getMessage());
        }
    }

    @Override
    protected void verifyNoChallenge(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        verify(httpServletResponse, never()).setStatus(anyInt());
    }

    @Override
    protected HttpServletRequest createMockRequest(Map<String, String> headers) {
        HttpServletRequest request = TestServlet3Engine.mockRequest(null, headers);
        when(request.getServletContext()).thenReturn(this.context);
        return request;
    }

    @Override
    protected HttpServletResponse createMockResponse() {
        return mock(HttpServletResponse.class);
    }

    @Override
    protected JwtAuthenticationEngine<HttpServletRequest, HttpServletResponse> createEngine() {
        return new Servlet3JwtAuthenticationEngine();
    }

    @Override
    protected JwtAuthenticationEngine<HttpServletRequest, HttpServletResponse> createEngine(String authHeader,
                                                                                            String authHeaderPrefix,
                                                                                            String realm,
                                                                                            String usernameClaim) {
        return new Servlet3JwtAuthenticationEngine(List.of(new HeaderSource(authHeader, authHeaderPrefix)), realm,
                                                   usernameClaim != null ? List.of(usernameClaim) : null);
    }

    @Override
    protected JwtAuthenticationEngine<HttpServletRequest, HttpServletResponse> createEngine(
            List<HeaderSource> authHeaders, String realm, List<String> usernameClaims) {
        return new Servlet3JwtAuthenticationEngine(authHeaders, realm, usernameClaims);
    }

    @Override
    protected void verifyStatusCode(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    int expectedStatus) throws IOException {
        TestServlet3Engine.verifyStatusCode(httpServletResponse, expectedStatus);
    }

    @Override
    protected String verifyHeaderPresent(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                         String expectedHeader) {
        return TestServlet3Engine.verifyHeaderPresent(httpServletResponse, expectedHeader);
    }

    @Override
    protected String getAuthenticatedUser(HttpServletRequest authenticatedRequest) {
        return authenticatedRequest.getRemoteUser();
    }

    @Override
    protected String getAuthenticatedUser(JwtAuthFilter filter, HttpServletRequest authenticatedRequest) {
        HttpServletRequest lastResult = filter.lastResult();
        return lastResult != null ? lastResult.getRemoteUser() : null;
    }

    @Override
    protected RuntimeConfigurationAdaptor createConfigAdaptor(Map<String, String> configuration) {
        return new Servlet3FilterConfigAdaptor(new FilterConfig() {
            @Override
            public String getFilterName() {
                return "Test";
            }

            @Override
            public ServletContext getServletContext() {
                return Mockito.mock(ServletContext.class);
            }

            @Override
            public String getInitParameter(String name) {
                return configuration.get(name);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return Collections.enumeration(configuration.keySet());
            }
        });
    }

    @Override
    protected JwtAuthFilter createUnconfiguredFilter() {
        return new JwtAuthFilter();
    }

    @Override
    protected Object verifyRequestAttribute(HttpServletRequest httpServletRequest, String attribute) {
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(httpServletRequest).setAttribute(eq(attribute), captor.capture());
        Object value = captor.getValue();
        Assert.assertNotNull(value, "Attribute " + attribute + " value unexpectedly null");
        return value;
    }
}
