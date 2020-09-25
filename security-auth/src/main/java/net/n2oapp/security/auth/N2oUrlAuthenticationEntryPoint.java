/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.n2oapp.security.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class N2oUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private final String n2oUrl;

    public N2oUrlAuthenticationEntryPoint(String loginFormUrl, String n2oUrl) {
        super(loginFormUrl);
        this.n2oUrl = n2oUrl;
    }

    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        if (isN2oRequest(request) && authException != null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
            return;
        }
        super.commence(request, response, authException);
    }

    private boolean isN2oRequest(HttpServletRequest request) {
        return request.getServletPath().startsWith(n2oUrl);
    }
}