package net.n2oapp.security.admin.impl;

import net.n2oapp.platform.jaxrs.MapperConfigurer;
import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.commons.AdminCommonsConfiguration;
import net.n2oapp.security.admin.impl.provider.SimpleSsoUserRoleProvider;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.UserServiceImpl;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.i_novus.ms.audit.client.impl.converter.RequestConverter;

import java.util.Locale;
import ru.inovus.ms.rdm.provider.RdmMapperConfigurer;
import ru.inovus.ms.rdm.service.api.DraftService;
import ru.inovus.ms.rdm.service.api.PublishService;
import ru.inovus.ms.rdm.service.api.RefBookService;
import ru.inovus.ms.rdm.service.api.VersionService;


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

    @EnableJaxRsProxyClient(
            classes = {RefBookService.class, DraftService.class,
                    PublishService.class, VersionService.class},
            address = "${rdm.rest.url}"
    )
    @SpringBootConfiguration
    public static class RdmProxyConfiguration {
        @Bean
        public MapperConfigurer cxfObjectMapperConfigurer() {
            return new RdmMapperConfigurer();
        }
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource){
        return new MessageSourceAccessor(messageSource, new Locale("ru"));
    }

    @Bean
    public RequestConverter requestConverter() {
        return new RequestConverter(
                this::getUser,
                () -> "Access",
                this::getRequestHost
        );
    }

    private ru.i_novus.ms.audit.client.model.User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        net.n2oapp.security.auth.common.User user = (net.n2oapp.security.auth.common.User)auth.getPrincipal();
        return new ru.i_novus.ms.audit.client.model.User("UNKNOWN", user.getEmail());
    }

    private String getRequestHost() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes.getRequest().getHeader(HttpHeaders.HOST);
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
