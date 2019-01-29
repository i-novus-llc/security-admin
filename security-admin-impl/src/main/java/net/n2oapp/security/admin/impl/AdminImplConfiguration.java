package net.n2oapp.security.admin.impl;

import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.commons.AdminCommonsConfiguration;
import net.n2oapp.security.admin.impl.provider.SimpleSsoUserRoleProvider;
import net.n2oapp.security.admin.impl.repository.EmployeeBankRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.UserServiceImpl;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@PropertySource("classpath:security.properties")
@EnableJpaRepositories(basePackages = "net.n2oapp.security.admin.impl")
@EntityScan("net.n2oapp.security.admin.impl")
@ComponentScan({"net.n2oapp.security.admin.impl", "net.n2oapp.security.admin.api"})
@Import(AdminCommonsConfiguration.class)
public class AdminImplConfiguration {


    @Bean
    public UserService userService(UserRepository userRepository, RoleRepository roleRepository, EmployeeBankRepository employeeBankRepository, SsoUserRoleProvider ssoUserRoleProvider) {
        return new UserServiceImpl(userRepository, roleRepository, employeeBankRepository, ssoUserRoleProvider);
    }


    @Bean
    public SimpleSsoUserRoleProvider simpleSsoUserRoleProvider() {
        return new SimpleSsoUserRoleProvider();
    }





}
