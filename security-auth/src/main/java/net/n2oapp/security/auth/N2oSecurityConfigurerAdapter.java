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

import net.n2oapp.framework.access.simple.PermissionApi;
import net.n2oapp.security.auth.context.SpringSecurityUserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

public abstract class N2oSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Value("${n2o.api.url:/n2o}")
    private String n2oUrl;

    @Bean
    public PermissionApi securitySimplePermissionApi() {
        return new SecuritySimplePermissionApi();
    }

    @Bean
    public SpringSecurityUserContext springSecurityUserContext() {
        return new SpringSecurityUserContext();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        ignore(web.ignoring());
    }

    protected void ignore(WebSecurity.IgnoredRequestConfigurer ignore) {
        ignore.antMatchers("/static/**", "/public/**", "/dist/**", "/webjars/**", "/lib/**", "/build/**", "/bundle/**", "/error");
    }

    protected abstract void authorize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry url)
            throws Exception;

    protected ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry beforeAuthorize(HttpSecurity http)
            throws Exception {
        return http.authorizeRequests();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        authorize(beforeAuthorize(http));
    }

    protected HttpSecurity configureExceptionHandling(ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling) throws Exception {
        return exceptionHandling
                .authenticationEntryPoint(new N2oUrlAuthenticationEntryPoint("/login", n2oUrl))
                .and();
    }
}
