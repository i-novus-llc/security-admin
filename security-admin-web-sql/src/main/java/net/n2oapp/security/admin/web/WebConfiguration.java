package net.n2oapp.security.admin.web;

import net.n2oapp.security.admin.n2o.JdbcRoleService;
import net.n2oapp.security.admin.n2o.JdbcUserService;
import net.n2oapp.security.admin.n2o.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Конфигурация web-sql
 */
@Configuration
@PropertySource("classpath:META-INF/conf/n2o.properties")
public class WebConfiguration {

    @Value("${sec.password.generate.length}")
    private Integer passwordGeneratorLength;

    @Value("${sec.password.generate.enabled}")
    private Boolean passwordGenerateEnabled;

    @Value("${sec.password.mail.subject}")
    private String passwordMailSubject;

    @Value("${sec.password.mail.body.path}")
    private Resource passwordMailBodyPath;

    @Value("${n2o.ui.url}")
    private String n2oUiUrl;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private TransactionTemplate transactionTemplate;

    @Bean
    public JdbcRoleService jdbcRoleService() {
        return new JdbcRoleService(transactionTemplate);
    }

    @Bean
    public JdbcUserService jdbcUserService() {
        JdbcUserService jdbcUserService = new JdbcUserService(transactionTemplate, jdbcTemplate);
        jdbcUserService.setPasswordGenerator(passwordGenerator());
        jdbcUserService.setPasswordEncoder(passwordEncoder());
        jdbcUserService.setGenerate(passwordGenerateEnabled);
        jdbcUserService.setMailSubject(passwordMailSubject);
        jdbcUserService.setMailBody(passwordMailBodyPath);
        jdbcUserService.setMailApp(n2oUiUrl);
        return jdbcUserService;
    }

    @Bean
    public PasswordGenerator passwordGenerator() {
        PasswordGenerator passwordGenerator = new PasswordGenerator();
        passwordGenerator.setLength(passwordGeneratorLength);
        return passwordGenerator;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
