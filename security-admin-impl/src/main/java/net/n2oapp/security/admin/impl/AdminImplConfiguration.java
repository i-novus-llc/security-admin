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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@PropertySource("classpath:security.properties")
public class AdminImplConfiguration {

    @Value("${sec.password.generate.length}")
    private Integer passwordGeneratorLength;

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
}
