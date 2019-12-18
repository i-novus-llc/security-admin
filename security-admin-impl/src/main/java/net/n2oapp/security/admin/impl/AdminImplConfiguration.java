package net.n2oapp.security.admin.impl;

import net.n2oapp.platform.jaxrs.MapperConfigurer;
import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.UserLevelService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.commons.AdminCommonsConfiguration;
import net.n2oapp.security.admin.impl.audit.AuditHelper;
import net.n2oapp.security.admin.impl.provider.SimpleSsoUserRoleProvider;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.UserLevelServiceImpl;
import net.n2oapp.security.admin.impl.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.inovus.ms.rdm.api.provider.RdmMapperConfigurer;
import ru.inovus.ms.rdm.api.service.DraftService;
import ru.inovus.ms.rdm.api.service.PublishService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
@PropertySource("classpath:security.properties")
@EnableJpaRepositories(basePackages = "net.n2oapp.security.admin.impl")
@EntityScan("net.n2oapp.security.admin.impl")
@ComponentScan({"net.n2oapp.security.admin.impl", "net.n2oapp.security.admin.api"})
@Import(AdminCommonsConfiguration.class)
public class AdminImplConfiguration {

    @Value("${access.level.federal}")
    private Boolean userLevelValueFederal;

    @Value("${access.level.regional}")
    private Boolean userLevelValueRegional;

    @Value("${access.level.org}")
    private Boolean userLevelValueOrg;

    @Bean
    public UserService userService(UserRepository userRepository, RoleRepository roleRepository, SsoUserRoleProvider ssoUserRoleProvider) {
        return new UserServiceImpl(userRepository, roleRepository, ssoUserRoleProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public SsoUserRoleProvider ssoUserRoleProvider() {
        return new SimpleSsoUserRoleProvider();
    }

    @EnableJaxRsProxyClient(
            classes = {DraftService.class, PublishService.class},
            address = "${rdm.client.export.url}"
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

    @Bean
    public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
        return new MessageSourceAccessor(messageSource, new Locale("ru"));
    }

    @Bean
    public AuditHelper getAuditHelper() {
        return new AuditHelper();
    }

//    @Configuration
//    static class LoadersConfiguration implements ServerLoaderConfigurer {
//
//        @Override
//        public void configure(ServerLoaderRunner runner) {
//            runner.add(ServerLoaderRoute.asIterable("permissions", Permission.class, PermissionServerLoader.class));
//        }
//    }
}
