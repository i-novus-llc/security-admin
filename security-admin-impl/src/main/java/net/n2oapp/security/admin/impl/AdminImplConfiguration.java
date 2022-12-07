package net.n2oapp.security.admin.impl;

import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.UserLevelService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import net.n2oapp.security.admin.impl.provider.SimpleSsoUserRoleProvider;
import net.n2oapp.security.admin.impl.repository.AccountRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.UserLevelServiceImpl;
import net.n2oapp.security.admin.impl.service.UserServiceImpl;
import net.n2oapp.security.admin.impl.userInfo.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.*;

@Configuration
@PropertySource("classpath:security.properties")
@EnableJpaRepositories(basePackages = "net.n2oapp.security.admin.impl")
@EntityScan("net.n2oapp.security.admin.impl")
@ComponentScan({"net.n2oapp.security.admin.impl", "net.n2oapp.security.admin.api"})
public class AdminImplConfiguration {

    @Value("${access.level.federal}")
    private Boolean userLevelValueFederal;

    @Value("${access.level.regional}")
    private Boolean userLevelValueRegional;

    @Value("${access.level.org}")
    private Boolean userLevelValueOrg;

    @Value("${access.userinfo.include-claims:}")
    private Set<String> wantedEnrichers;

    @Bean
    public UserInfoService userInfoService(UserRepository userRepository, AccountRepository accountRepository, List<UserInfoEnricher<AccountEntity>> enrichers) {
        UserInfoEnrichersConfigurer configurer = new UserInfoEnrichersConfigurer(wantedEnrichers, enrichers);
        return new UserInfoService(userRepository, accountRepository, configurer.configure());
    }

    @Bean
    public UserService userService(UserRepository userRepository, SsoUserRoleProvider ssoUserRoleProvider) {
        return new UserServiceImpl(userRepository, ssoUserRoleProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public SsoUserRoleProvider ssoUserRoleProvider() {
        return new SimpleSsoUserRoleProvider();
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

    public static class UserInfoEnrichersConfigurer {

        private final Set<String> wantedEnrichers;
        private final Collection<UserInfoEnricher<AccountEntity>> beans;

        public UserInfoEnrichersConfigurer(Set<String> wantedEnrichers, Collection<UserInfoEnricher<AccountEntity>> beans) {
            this.wantedEnrichers = wantedEnrichers;
            this.beans = beans;
        }

        public List<UserInfoEnricher<AccountEntity>> configure() {
            List<UserInfoEnricher<AccountEntity>> result = new ArrayList<>();
            if (wantedEnrichers != null && beans != null) {
                for (UserInfoEnricher<AccountEntity> bean : beans) {
                    if (wantedEnrichers.contains(bean.getAlias()))
                        result.add(bean);
                }
            }
            return result;
        }
    }
}
