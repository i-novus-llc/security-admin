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

import net.n2oapp.framework.access.data.SecurityProvider;
import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.security.auth.context.SpringSecurityUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * Адаптер для настройки SSO аутентификации по протоколу OAuth2 OpenId Connect
 */
@EnableWebSecurity
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = KeycloakLogoutHandler.class))
public abstract class OpenIdSecurityCustomizer extends N2oSecurityCustomizer {

    @Value("${access.keycloak.logout-uri:/}")
    private String ssoLogoutUri;

    @Value("${n2o.access.schema.id}")
    private String schemaId;
    @Value("${n2o.access.deny_urls}")
    private Boolean defaultUrlAccessDenied;

    @Lazy
    @Autowired
    private MetadataEnvironment environment;
    @Autowired
    private KeycloakLogoutHandler keycloakLogoutHandler;
    @Autowired
    private SecurityProvider securityProvider;

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {
        configureLogout(http.logout());
        http.oauth2Login();
        http.addFilterAfter(new N2oUrlFilter(schemaId, defaultUrlAccessDenied, environment, securityProvider), FilterSecurityInterceptor.class);
    }

    @Override
    public SpringSecurityUserContext springSecurityUserContext() {
        return new SpringUserContextWithToken();
    }

    protected LogoutConfigurer<HttpSecurity> configureLogout(LogoutConfigurer<HttpSecurity> logout) {
        logout.logoutSuccessUrl(ssoLogoutUri);
        return logout.addLogoutHandler(keycloakLogoutHandler);
    }
}
