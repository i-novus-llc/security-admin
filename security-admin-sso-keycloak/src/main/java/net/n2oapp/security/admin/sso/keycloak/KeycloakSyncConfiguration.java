package net.n2oapp.security.admin.sso.keycloak;

import net.n2oapp.security.admin.impl.scheduled.SynchronizationInfo;
import net.n2oapp.security.admin.sso.keycloak.synchronization.UserSynchronizeJob;
import org.quartz.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
public class KeycloakSyncConfiguration {

    public static final String USER_SYNCHRONIZE_JOB_DETAIL = "User_Synchronize_Job_Detail";
    private static final String USER_SYNCHRONIZE_TRIGGER = "User_Synchronize_Trigger";

    @Bean
    @ConditionalOnProperty(name = "access.keycloak.synchronize-enabled")
    public SynchronizationInfo userJobAndTrigger(AdminSsoKeycloakProperties properties) {
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
        return new SynchronizationInfo(userSynchronizeJobDetail, Set.of(userSynchronizeJobTrigger));
    }
}
