package net.n2oapp.security.admin.impl;

import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.impl.provider.SimpleSsoUserRoleProvider;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.UserServiceImpl;
import net.n2oapp.security.admin.impl.util.PasswordGenerator;
import org.modelmapper.ModelMapper;
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

@Configuration
@PropertySource("classpath:security.properties")
@ComponentScan({"net.n2oapp.security.admin.impl", "net.n2oapp.security.admin.api"})
public class AdminImplConfiguration {

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

    @Value("${sec.spring.mail.properties.mail.smtp.auth.enable}")
    private Boolean mailSmtpAuthEnable;

    @Value("${sec.spring.mail.properties.mail.smtp.starttls.enable}")
    private Boolean mailSmtpStarttlsEnable;

    @Value("${sec.spring.mail.properties.mail.smtp.auth}")
    private Boolean mailSmtpAuth;

    @Bean
    public UserService userService(UserRepository userRepository, RoleRepository roleRepository, SsoUserRoleProvider ssoUserRoleProvider) {
        return new UserServiceImpl(userRepository, roleRepository, ssoUserRoleProvider, passwordGenerator(),
                passwordEncoder());
    }

    @Bean
    public SimpleSsoUserRoleProvider simpleSsoUserRoleProvider() {
        return new SimpleSsoUserRoleProvider();
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

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
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
