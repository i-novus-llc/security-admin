package net.n2oapp.security.admin.sql;

import net.n2oapp.security.admin.sql.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;

/**
 * Конфигурация security-admin-sql
 */
@Configuration
@PropertySource("classpath:mail.properties")
@ComponentScan("net.n2oapp.security.admin.sql")
public class AdminSqlConfiguration {

    @Value("${sec.password.generate.length}")
    private Integer passwordGeneratorLength;

    @Value("${sec.password.mail.subject}")
    private String passwordMailSubject;

    @Value("${sec.password.mail.body.path}")
    private Resource passwordMailBodyPath;

    @Value("${sec.spring.mail.host}")
    private String mailHost;

    @Value("${sec.spring.mail.port}")
    private Integer mailPort;

    @Value("${sec.spring.mail.username}")
    private String mailUsername;

    @Value("${sec.spring.mail.password}")
    private String mailPassword;

    @Value("${sec.spring.mail.properties.mail.smtp.starttls.enable}")
    private Boolean smtpStarttlsEnable;

    @Value("${sec.spring.mail.properties.mail.smtp.starttls}")
    private String smtpStarttls;

    @Value("${sec.spring.mail.properties.mail.smtp.auth.enable}")
    private Boolean smtpAuthEnable;

    @Value("${sec.spring.mail.properties.mail.smtp.auth}")
    private String smtpAuth;

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


    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);

        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put(smtpAuth, smtpAuthEnable);
        props.put(smtpStarttls, smtpStarttlsEnable);

        return mailSender;
    }


}



