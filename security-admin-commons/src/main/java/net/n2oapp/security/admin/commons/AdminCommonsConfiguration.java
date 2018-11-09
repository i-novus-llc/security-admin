package net.n2oapp.security.admin.commons;

import net.n2oapp.security.admin.commons.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;

/**
 * Конфигурация security-admin-commons
 */
@Configuration
@PropertySource(value = {"classpath:mail.properties", "classpath:placeholders.properties"},
        ignoreResourceNotFound = true)
@ComponentScan("net.n2oapp.security.admin.commons")
public class AdminCommonsConfiguration {

    @Value("${sec.password.generate.length}")
    private Integer passwordGeneratorLength;

    @Value("${sec.spring.mail.host}")
    private String mailHost;

    @Value("${sec.spring.mail.port}")
    private int mailPort;

    @Value("${sec.spring.mail.username}")
    private String mailUsername;

    @Value("${sec.spring.mail.password}")
    private String mailPassword;

    @Value("${sec.spring.mail.properties.mail.smtp.auth}")
    private Boolean mailSmtpAuth;

    @Value("${sec.spring.mail.properties.mail.smtp.starttls.enable}")
    private Boolean mailSmtpStarttlsEnable;


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
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);

        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", mailSmtpAuth);
        props.put("mail.smtp.starttls.enable", mailSmtpStarttlsEnable);

        return mailSender;
    }
}
