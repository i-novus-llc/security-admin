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
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public abstract class N2oSecurityCustomizer {

    @Bean
    public PermissionApi securitySimplePermissionApi() {
        return new SecuritySimplePermissionApi();
    }

    @Bean
    public SpringSecurityUserContext springSecurityUserContext() {
        return new SpringSecurityUserContext();
    }

    protected void ignore(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/static/**", "/public/**", "/dist/**", "/webjars/**", "/lib/**", "/build/**", "/bundle/**", "/error", "/serviceWorker.js", "/css/**", "/manifest.json", "/favicon.ico").permitAll();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        ignore(http);
        configureHttpSecurity(http);
        return http.build();
    }

    protected void configureHttpSecurity(HttpSecurity http) throws Exception {
    }
}
