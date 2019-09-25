package net.n2oapp.security.admin.sso.keycloak;

import net.n2oapp.security.admin.sso.keycloak.synchronization.KeycloakUserSynchronizeProvider;
import net.n2oapp.security.admin.sso.keycloak.synchronization.UserSynchronizeJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
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

    @Value("${access.keycloak.sync-cron:0 0 0 1 1 ? 2100}")
    private String cronFrequency;

    @Autowired
    KeycloakUserSynchronizeProvider keycloakUserSynchronizeProvider;

    @Bean
    @Primary
    KeycloakSsoUserRoleProvider keycloakSsoUserRoleProvider(AdminSsoKeycloakProperties properties) {
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
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
        JobDetail userSynchronizeJobDetail = JobBuilder.newJob().ofType(UserSynchronizeJob.class)
                .storeDurably()
                .withIdentity(USER_SYNCHRONIZE_JOB_DETAIL)
                .usingJobData(new JobDataMap())
                .build();

        Trigger userSynchronizeJobTrigger = TriggerBuilder.newTrigger()
                .forJob(userSynchronizeJobDetail)
                .withIdentity("User_Synchronize_Trigger")
                .withSchedule(cronSchedule(cronFrequency))
                .build();

        schedulerFactoryBean.setSchedulerContextAsMap(
                Map.of(KeycloakUserSynchronizeProvider.class.getSimpleName(), keycloakUserSynchronizeProvider));

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.scheduleJob(userSynchronizeJobDetail, Set.of(userSynchronizeJobTrigger), true);
        return scheduler;
    }

}
