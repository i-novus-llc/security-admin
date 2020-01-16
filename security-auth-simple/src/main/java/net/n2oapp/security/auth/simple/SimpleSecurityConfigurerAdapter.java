package net.n2oapp.security.auth.simple;

import net.n2oapp.framework.access.data.SecurityProvider;
import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.security.auth.N2oSecurityConfigurerAdapter;
import net.n2oapp.security.auth.N2oUrlFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import static net.n2oapp.security.auth.simple.SpringConfigUtil.*;

/**
 * Адаптер для настройки безопасности с простой аутентификацией по логину и паролю
 */
@Import({SimpleAuthConfig.class, SimpleAuthController.class})
public abstract class SimpleSecurityConfigurerAdapter extends N2oSecurityConfigurerAdapter {

    @Value("${n2o.access.schema.id}")
    private String schemaId;
    @Value("${n2o.access.deny_urls}")
    private Boolean defaultUrlAccessDenied;
    @Autowired
    private MetadataEnvironment environment;
    @Autowired
    private SecurityProvider securityProvider;

    private DaoAuthenticationProvider daoAuthenticationProvider;

    public SimpleSecurityConfigurerAdapter(DaoAuthenticationProvider daoAuthenticationProvider) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider);
    }

    @Override
    protected ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry beforeAuthorize(HttpSecurity http) throws Exception {
        return configureAuthorizeAuthRequests(super.beforeAuthorize(http));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        configureExceptionHandling(http.exceptionHandling());
        authorize(beforeAuthorize(http));
        configureLogin(http.formLogin());
        configureLogout(http.logout());
        http.addFilterAfter(new N2oUrlFilter(schemaId, defaultUrlAccessDenied, environment, securityProvider), FilterSecurityInterceptor.class);
        http.headers().contentTypeOptions().disable();
        http.csrf().disable();

    }

    protected abstract void authorize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry url)
            throws Exception;
}
