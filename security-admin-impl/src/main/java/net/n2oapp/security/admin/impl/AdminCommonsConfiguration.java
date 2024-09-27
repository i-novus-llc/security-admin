package net.n2oapp.security.admin.impl;

import net.n2oapp.security.admin.impl.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Конфигурация security-admin-commons
 */
@Configuration
@PropertySource({"classpath:mail.properties", "classpath:password.properties"})
public class AdminCommonsConfiguration {

    @Value("${sec.mail.host}")
    private String mailHost;

    @Value("${sec.mail.port}")
    private int mailPort;

    @Value("${sec.mail.username}")
    private String mailUsername;

    @Value("${sec.mail.password}")
    private String mailPassword;

    @Value("${sec.mail.smtp.auth}")
    private Boolean mailSmtpAuth;

    @Value("${sec.mail.smtp.starttls.enabled}")
    private Boolean mailSmtpStarttlsEnable;

    @Bean
    public PasswordGenerator passwordGenerator() {
        return new PasswordGenerator();
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
