package net.n2oapp.framework.security.auth.oauth2;

import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.auth.N2oSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;

/**
 * Адаптер для настройки SSO аутентификации по протоколу OAuth2 OpenId Connect
 */
@EnableOAuth2Sso
public abstract class OpenIdSecurityConfigurerAdapter extends N2oSecurityConfigurerAdapter {

    @Value("${security.oauth2.sso.logout-uri:/logout}")
    private String ssoLogout;
    @Autowired
    private OAuth2SsoProperties sso;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        authorize(beforeAuthorize(http));
        configureExceptionHandling(http.exceptionHandling());
        configureLogout(http.logout());
        http.csrf().disable();
    }

    @Bean
    protected OpenIdPrincipalExtractor principalExtractor(UserDetailsService userDetailsService) {
        return new OpenIdPrincipalExtractor(userDetailsService);
    }

    protected void configureExceptionHandling(ExceptionHandlingConfigurer<HttpSecurity> exceptions)
            throws Exception {
//        ContentNegotiationStrategy contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
//        MediaTypeRequestMatcher preferredMatcher = new MediaTypeRequestMatcher(
//                contentNegotiationStrategy, MediaType.APPLICATION_XHTML_XML,
//                new MediaType("image", "*"), MediaType.TEXT_HTML, MediaType.TEXT_PLAIN);
//        preferredMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
//        exceptions.defaultAuthenticationEntryPointFor(
//                new LoginUrlAuthenticationEntryPoint(sso.getLoginPath()),
//                preferredMatcher);
//        // When multiple entry points are provided the default is the first one
//        exceptions.defaultAuthenticationEntryPointFor(
//                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
//                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
    }

    protected LogoutConfigurer<HttpSecurity> configureLogout(LogoutConfigurer<HttpSecurity> logout) throws Exception {
        return logout.logoutSuccessUrl(ssoLogout != null ? ssoLogout : "/");
    }
}
