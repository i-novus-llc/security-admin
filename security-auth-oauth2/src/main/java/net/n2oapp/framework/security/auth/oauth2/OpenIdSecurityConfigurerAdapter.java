package net.n2oapp.framework.security.auth.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetailsSource;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

/**
 * Адаптер для настройки SSO аутентификации по протоколу OAuth2 OpenId Connect
 */
@EnableOAuth2Client
public class OpenIdSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2ClientAuthenticationProcessingFilter oauth2SsoFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.apply(new OAuth2ClientAuthenticationConfigurer(oauth2SsoFilter));
        addAuthenticationEntryPoint(http);
    }

    @Bean
    protected OpenIdProperties openIdProperties() {
        return new OpenIdProperties();
    }

    @Bean
    protected ResourceServerTokenServices userInfoServices(OpenIdProperties properties,
                                                           OAuth2RestTemplate oauth2RestTemplate) {
        UserInfoTokenServices userInfoTokenServices = new UserInfoTokenServices(properties.getUserInfoUrl(), properties.getClientId());
        userInfoTokenServices.setRestTemplate(oauth2RestTemplate);
        return userInfoTokenServices;
    }

    @Bean
    protected OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails(OpenIdProperties properties) {
        AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();
        resource.setId("n2o-auth");
        resource.setAccessTokenUri(properties.getAccessTokenUrl());
        resource.setUserAuthorizationUri(properties.getUserAuthorizationUrl());
        resource.setClientId(properties.getClientId());
        resource.setClientSecret(properties.getClientSecret());
        resource.setScope(Arrays.asList(properties.getScopes()));
        return resource;
    }

    @Bean
    protected OAuth2RestTemplate oauth2RestTemplate(OAuth2ClientContext oauth2ClientContext,
                                          OAuth2ProtectedResourceDetails details) {
        OAuth2RestTemplate template = new OAuth2RestTemplate(details, oauth2ClientContext);
//        template.getInterceptors().add(new AcceptJsonRequestInterceptor());
        AuthorizationCodeAccessTokenProvider accessTokenProvider = new AuthorizationCodeAccessTokenProvider();
//        accessTokenProvider.setTokenRequestEnhancer(new AcceptJsonRequestEnhancer());
        template.setAccessTokenProvider(accessTokenProvider);
        return template;
    }

    @Bean
    protected OAuth2ClientAuthenticationProcessingFilter oauth2SsoFilter(ApplicationContext applicationContext,
                                                                         ResourceServerTokenServices tokenServices,
                                                                         OAuth2RestTemplate restTemplate,
                                                                         AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter("/login");
        filter.setRestTemplate(restTemplate);
        filter.setTokenServices(tokenServices);
        filter.setAuthenticationDetailsSource(authenticationDetailsSource);
        filter.setApplicationEventPublisher(applicationContext);
        return filter;
    }

    @Bean
    protected AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource() {
        return new OAuth2AuthenticationDetailsSource();
    }

    private void addAuthenticationEntryPoint(HttpSecurity http)
            throws Exception {
        ExceptionHandlingConfigurer<HttpSecurity> exceptions = http.exceptionHandling();
        ContentNegotiationStrategy contentNegotiationStrategy = http
                .getSharedObject(ContentNegotiationStrategy.class);
        if (contentNegotiationStrategy == null) {
            contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        }
        MediaTypeRequestMatcher preferredMatcher = new MediaTypeRequestMatcher(
                contentNegotiationStrategy, MediaType.APPLICATION_XHTML_XML,
                new MediaType("image", "*"), MediaType.TEXT_HTML, MediaType.TEXT_PLAIN);
        preferredMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        exceptions.defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint("/login"),
                preferredMatcher);
        // When multiple entry points are provided the default is the first one
        exceptions.defaultAuthenticationEntryPointFor(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
    }

    private static class OAuth2ClientAuthenticationConfigurer
            extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {


        private OAuth2ClientAuthenticationProcessingFilter filter;

        OAuth2ClientAuthenticationConfigurer(
                OAuth2ClientAuthenticationProcessingFilter filter) {
            this.filter = filter;
        }

        @Override
        public void configure(HttpSecurity builder) throws Exception {
            OAuth2ClientAuthenticationProcessingFilter ssoFilter = this.filter;
            ssoFilter.setSessionAuthenticationStrategy(
                    builder.getSharedObject(SessionAuthenticationStrategy.class));
            builder.addFilterAfter(ssoFilter,
                    AbstractPreAuthenticatedProcessingFilter.class);
        }

    }

    public void setOauth2SsoFilter(OAuth2ClientAuthenticationProcessingFilter oauth2SsoFilter) {
        this.oauth2SsoFilter = oauth2SsoFilter;
    }
}
