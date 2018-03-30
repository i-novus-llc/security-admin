package net.n2oapp.security.auth.simple;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Адаптер для настройки безопасности с простой аутентификацией по логину и паролю
 */
public class SimpleSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SpringConfigUtil.cofigureHttp(http);
    }
}
