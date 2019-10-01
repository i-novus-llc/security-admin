package net.n2oapp.security.admin.impl;

import net.n2oapp.platform.jaxrs.MapperConfigurer;
import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.UserLevelService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.commons.AdminCommonsConfiguration;
import net.n2oapp.security.admin.impl.provider.SimpleSsoUserRoleProvider;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.UserLevelServiceImpl;
import net.n2oapp.security.admin.impl.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.inovus.ms.rdm.provider.RdmMapperConfigurer;
import ru.inovus.ms.rdm.service.api.DraftService;
import ru.inovus.ms.rdm.service.api.PublishService;
import ru.inovus.ms.rdm.service.api.RefBookService;
import ru.inovus.ms.rdm.service.api.VersionService;

import java.util.ArrayList;
import java.util.List;


@Configuration
@PropertySource("classpath:security.properties")
@EnableJpaRepositories(basePackages = "net.n2oapp.security.admin.impl")
@EntityScan("net.n2oapp.security.admin.impl")
@ComponentScan({"net.n2oapp.security.admin.impl", "net.n2oapp.security.admin.api"})
@Import(AdminCommonsConfiguration.class)
public class AdminImplConfiguration {

    @Value("${user-level.value.federal}")
    private Boolean userLevelValueFederal;

    @Value("${user-level.value.regional}")
    private Boolean userLevelValueRegional;

    @Value("${user-level.value.org}")
    private Boolean userLevelValueOrg;

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
    public UserLevelService userLevelService() {
        List<UserLevel> actualUserLevels = new ArrayList<>();
        if (userLevelValueFederal != null && userLevelValueFederal) {
            actualUserLevels.add(UserLevel.FEDERAL);
        }
        if (userLevelValueRegional != null && userLevelValueRegional) {
            actualUserLevels.add(UserLevel.REGIONAL);
        }
        if (userLevelValueOrg != null && userLevelValueOrg) {
            actualUserLevels.add(UserLevel.ORGANIZATION);
        }
        return new UserLevelServiceImpl(actualUserLevels);
    }

}
