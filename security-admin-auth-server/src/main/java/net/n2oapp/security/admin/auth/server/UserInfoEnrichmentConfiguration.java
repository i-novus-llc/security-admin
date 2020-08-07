package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.auth.server.oauth.UserInfoService;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Configuration
public class UserInfoEnrichmentConfiguration {

    @Value("${access.userinfo.include-claims:}")
    private Set<String> wantedEnrichers;

    @Bean
    public UserInfoService userInfoService(UserRepository userRepository, @Value("${access.permission.enabled}") Boolean permissionEnabled, List<UserInfoEnricher<?>> enrichers) {
        UserInfoEnrichersConfigurer configurer = new UserInfoEnrichersConfigurer(wantedEnrichers, enrichers);
        return new UserInfoService(userRepository, permissionEnabled, configurer.configure());
    }

    public static class UserInfoEnrichersConfigurer {

        private final Set<String> wantedEnrichers;
        private final Collection<UserInfoEnricher<?>> beans;


        public UserInfoEnrichersConfigurer(Set<String> wantedEnrichers, Collection<UserInfoEnricher<?>> beans) {
            this.wantedEnrichers = wantedEnrichers;
            this.beans = beans;
        }

        public List<UserInfoEnricher<?>> configure() {
            List<UserInfoEnricher<?>> result = new ArrayList<>();
            if (wantedEnrichers != null && beans != null) {
                for (UserInfoEnricher<?> bean : beans) {
                    if (wantedEnrichers.contains(bean.getAlias()))
                        result.add(bean);
                }
            }
            return result;
        }
    }
}
