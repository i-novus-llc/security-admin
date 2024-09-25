package net.n2oapp.framework.security.autoconfigure.access;

import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.auth.common.KeycloakUserService;
import net.n2oapp.security.auth.common.UserAttributeKeys;
import net.n2oapp.security.auth.context.account.ContextUserInfoTokenServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@AutoConfiguration
@ConditionalOnClass(name = "net.n2oapp.security.auth.N2oSecurityCustomizer")
public class AccountContextAutoConfiguration {

    @Value("${access.service.userinfo-url}")
    private String userInfoUri;

    @Bean
    @ConditionalOnMissingBean
    public OAuth2UserService<OidcUserRequest, OidcUser> keycloakUserService(UserDetailsService userDetailsService, UserAttributeKeys userAttributeKeys) {
        return new KeycloakUserService(userAttributeKeys, userDetailsService, "keycloak");
    }

    @Bean
    @ConditionalOnMissingBean
    public ContextUserInfoTokenServices contextUserInfoTokenServices() {
        return new ContextUserInfoTokenServices(userInfoUri);
    }

}
