package net.n2oapp.security.auth.simple;

import net.n2oapp.framework.api.context.ContextProcessor;
import net.n2oapp.framework.api.data.DomainProcessor;
import net.n2oapp.framework.api.data.QueryProcessor;
import net.n2oapp.framework.engine.data.N2oInvocationFactory;
import net.n2oapp.framework.engine.data.N2oQueryProcessor;
import net.n2oapp.security.admin.api.criteria.BaseCriteriaConstructor;
import net.n2oapp.security.auth.N2oSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import static net.n2oapp.security.auth.simple.SpringConfigUtil.*;

/**
 * Адаптер для настройки безопасности с простой аутентификацией по логину и паролю
 */
public abstract class SimpleSecurityConfigurerAdapter extends N2oSecurityConfigurerAdapter {

    private DaoAuthenticationProvider daoAuthenticationProvider;

    public SimpleSecurityConfigurerAdapter(DaoAuthenticationProvider daoAuthenticationProvider) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
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
        http.headers().contentTypeOptions().disable();
        http.csrf().disable();

    }

    protected abstract void authorize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry url)
            throws Exception;

    @Bean
    public QueryProcessor queryProcessor(ContextProcessor contextProcessor,
                                         DomainProcessor domainProcessor,
                                         N2oInvocationFactory invocationFactory) {
        N2oQueryProcessor queryProcessor = new N2oQueryProcessor(contextProcessor, domainProcessor, invocationFactory);
        queryProcessor.setCriteriaResolver(new BaseCriteriaConstructor());
        return queryProcessor;
    }
}
