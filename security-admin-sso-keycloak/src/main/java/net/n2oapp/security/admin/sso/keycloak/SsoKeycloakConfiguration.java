package net.n2oapp.security.admin.sso.keycloak;

import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.sso.keycloak.synchronization.UserSynchronizeJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

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
    SsoUserRoleProvider ssoUserRoleProvider(AdminSsoKeycloakProperties properties) {
        return new KeycloakSsoUserRoleProvider(properties);
    }

    @Bean
    KeycloakRestRoleService keycloakRestRoleService(AdminSsoKeycloakProperties properties) {
        return new KeycloakRestRoleService(properties);
    }

    @Bean
    KeycloakRestUserService keycloakRestUserService(AdminSsoKeycloakProperties properties) {
        return new KeycloakRestUserService(properties);
    }

    @Bean
    OAuth2RestOperations keycloakRestTemplate(AdminSsoKeycloakProperties properties) {
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
        resource.setClientId(properties.getAdminClientId());
        resource.setClientSecret(properties.getAdminClientSecret());
        resource.setAccessTokenUri(String.format("%s/realms/%s/protocol/openid-connect/token", properties.getServerUrl(), properties.getRealm()));
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resource);
        return restTemplate;
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
