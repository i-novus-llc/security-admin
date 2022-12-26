package net.n2oapp.security.auth.simple;

import net.n2oapp.framework.access.data.SecurityProvider;
import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.security.auth.N2oSecurityCustomizer;
import net.n2oapp.security.auth.N2oUrlFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import static net.n2oapp.security.auth.simple.SpringConfigUtil.*;

/**
 * Адаптер для настройки безопасности с простой аутентификацией по логину и паролю
 */
@Import({SimpleAuthConfig.class, SimpleAuthController.class})
public abstract class SimpleSecurityCustomizer extends N2oSecurityCustomizer {

    @Value("${n2o.access.schema.id}")
    private String schemaId;

    @Value("${n2o.access.deny_urls}")
    private Boolean defaultUrlAccessDenied;

    @Lazy
    @Autowired
    private MetadataEnvironment environment;

    @Autowired
    private SecurityProvider securityProvider;

    private DaoAuthenticationProvider daoAuthenticationProvider;

    public SimpleSecurityCustomizer(DaoAuthenticationProvider daoAuthenticationProvider) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {
        configureAuthorizeAuthRequests(http.authorizeRequests());
        configureExceptionHandling(http.exceptionHandling());
        configureLogin(http.formLogin());
        configureLogout(http.logout());
        http.addFilterAfter(new N2oUrlFilter(schemaId, defaultUrlAccessDenied, environment, securityProvider), FilterSecurityInterceptor.class);
        http.headers().contentTypeOptions().disable();
        http.csrf().disable();
    }
}
