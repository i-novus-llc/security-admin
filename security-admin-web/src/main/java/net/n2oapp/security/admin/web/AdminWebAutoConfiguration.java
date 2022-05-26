package net.n2oapp.security.admin.web;

import net.n2oapp.framework.config.register.InfoConstructor;
import net.n2oapp.framework.config.register.scanner.DefaultInfoScanner;
import net.n2oapp.framework.config.register.scanner.DefaultXmlInfoScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebAutoConfiguration {

    public static final String DEFAULT_PATTERN = "net/n2oapp/security/admin/web/default/**/*.xml";

    @Bean
    public DefaultInfoScanner<InfoConstructor> defaultInfoScanner() {
        return new DefaultXmlInfoScanner(DEFAULT_PATTERN);
    }
}
