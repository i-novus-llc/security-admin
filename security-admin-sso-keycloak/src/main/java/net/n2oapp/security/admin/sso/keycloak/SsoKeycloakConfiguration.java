package net.n2oapp.security.admin.sso.keycloak;

import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.impl.provider.SimpleSsoUserRoleProvider;
import net.n2oapp.security.admin.sso.keycloak.synchronization.UserSynchronizeJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestOperations;

import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;

/**
 * Конфигурация модуля взаимодействия с keycloak
 */
@Configuration
@DependsOn("liquibase")
@EnableConfigurationProperties(AdminSsoKeycloakProperties.class)
public class SsoKeycloakConfiguration {

    public static final String USER_SYNCHRONIZE_JOB_DETAIL = "User_Synchronize_Job_Detail";
    private static final String USER_SYNCHRONIZE_TRIGGER = "User_Synchronize_Trigger";

    @Autowired
    private AdminSsoKeycloakProperties properties;

    @Bean
    @ConditionalOnExpression("${access.keycloak.modify-enabled:true}")
    SsoUserRoleProvider ssoUserRoleProvider(AdminSsoKeycloakProperties properties) {
        return new KeycloakSsoUserRoleProvider(properties);
    }

    @Bean
    @ConditionalOnExpression("${access.keycloak.modify-enabled:false}")
    SsoUserRoleProvider ssoUserRoleProvider() {
        return new SimpleSsoUserRoleProvider();
    }

    @Bean
    KeycloakRestRoleService keycloakRestRoleService(AdminSsoKeycloakProperties properties,
                                                    @Qualifier("keycloakRestTemplate") RestOperations template) {
        return new KeycloakRestRoleService(properties, template);
    }

    @Bean
    KeycloakRestUserService keycloakRestUserService(AdminSsoKeycloakProperties properties,
                                                    @Qualifier("keycloakRestTemplate") RestOperations template,
                                                    KeycloakRestRoleService roleService) {
        return new KeycloakRestUserService(properties, template, roleService);
    }

    @Bean
    OAuth2RestOperations keycloakRestTemplate(AdminSsoKeycloakProperties properties) {
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
        resource.setClientId(properties.getAdminClientId());
        resource.setClientSecret(properties.getAdminClientSecret());
        resource.setAccessTokenUri(String.format("%s/realms/%s/protocol/openid-connect/token", properties.getServerUrl(), properties.getRealm()));
        return new OAuth2RestTemplate(resource);
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        if (properties.getSynchronizeEnabled()) {
            JobDetail userSynchronizeJobDetail = JobBuilder.newJob().ofType(UserSynchronizeJob.class)
                    .storeDurably()
                    .withIdentity(USER_SYNCHRONIZE_JOB_DETAIL)
                    .usingJobData(new JobDataMap())
                    .build();

            Trigger userSynchronizeJobTrigger = TriggerBuilder.newTrigger()
                    .forJob(userSynchronizeJobDetail)
                    .withIdentity(USER_SYNCHRONIZE_TRIGGER)
                    .withSchedule(cronSchedule(properties.getSynchronizeFrequency()))
                    .build();

            scheduler.scheduleJob(userSynchronizeJobDetail, Set.of(userSynchronizeJobTrigger), true);
        } else {
            scheduler.deleteJob(new JobKey(USER_SYNCHRONIZE_JOB_DETAIL));
        }
        return scheduler;
    }

}
