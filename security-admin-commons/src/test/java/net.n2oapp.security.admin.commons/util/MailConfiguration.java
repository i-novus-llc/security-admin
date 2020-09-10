package net.n2oapp.security.admin.commons.util;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;

import java.util.Properties;

@TestConfiguration
public class MailConfiguration {
    private final String mailHost = "localhost";
    private final int mailPort = 2525;
    private final String mailUsername = "inovus.sec@gmail.com";
    private String mailPassword;
    private final Boolean mailSmtpAuth = false;
    private final Boolean mailSmtpStarttlsEnable = false;

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

    @Bean
    public TemplateEngine templateEngine() {
        return new TemplateEngine();
    }
}
