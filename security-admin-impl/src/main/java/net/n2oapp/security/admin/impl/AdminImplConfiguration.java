package net.n2oapp.security.admin.impl;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.commons.AdminCommonsConfiguration;
import net.n2oapp.security.admin.impl.provider.SimpleSsoUserRoleProvider;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.i_novus.ms.audit.client.AuditClient;
import ru.i_novus.ms.audit.client.impl.SimpleAuditClientImpl;
import ru.i_novus.ms.audit.client.impl.converter.RequestConverter;
import ru.i_novus.ms.audit.client.model.User;
import ru.i_novus.ms.audit.service.api.AuditRest;


@Configuration
@PropertySource("classpath:security.properties")
@EnableJpaRepositories(basePackages = "net.n2oapp.security.admin.impl")
@EntityScan("net.n2oapp.security.admin.impl")
@ComponentScan({"net.n2oapp.security.admin.impl", "net.n2oapp.security.admin.api"})
@Import(AdminCommonsConfiguration.class)
public class AdminImplConfiguration {


    @Bean
    public UserService userService(UserRepository userRepository, RoleRepository roleRepository, SsoUserRoleProvider ssoUserRoleProvider) {
        return new UserServiceImpl(userRepository, roleRepository, ssoUserRoleProvider);
    }

    @Bean
    public SimpleSsoUserRoleProvider simpleSsoUserRoleProvider() {
        return new SimpleSsoUserRoleProvider();
    }


    @Bean
    public RequestConverter requestConverter() {
        return new RequestConverter(
                () -> new User("UNKNOWN", "UNKNOWN"),   //FIXME
                () -> "Access",
                () -> "SOURCE_WORKSTATION"
        );
    }

    @Configuration
    @EnableJaxRsProxyClient(
            classes = {AuditRest.class},
            address = "${audit.rest.url}")  //TODO добавить
    static class AuditClientConfiguration {
        @Bean
        public AuditClient simpleAuditClient(@Qualifier("auditRestJaxRsProxyClient") AuditRest auditRest) {
            SimpleAuditClientImpl simpleAuditClient = new SimpleAuditClientImpl();
            simpleAuditClient.setAuditRest(auditRest);
            return simpleAuditClient;
        }
    }


}
