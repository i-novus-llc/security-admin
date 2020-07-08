package net.n2oapp.auth.gateway.scheduled;

import net.n2oapp.security.admin.sso.keycloak.AdminSsoKeycloakProperties;
import net.n2oapp.security.admin.sso.keycloak.synchronization.UserSynchronizeJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import ru.inovus.ms.rdm.sync.rest.RdmSyncRest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
public class RdmSyncConfiguration {

    @Value("${rdm.cron.export}")
    private String cronExpression;

    @Value("${rdm.cron.import.region}")
    private String regionUpdateCronExpression;

    @Value("${rdm.cron.import.organization}")
    private String organizationUpdateCronExpression;

    @Value("${rdm.cron.import.department}")
    private String departmentUpdateCronExpression;

    @Value("${access.organization-sync-or-crud}")
    private String organizationSyncOrCrud;

    @Autowired
    private RdmSyncRest rdmSyncRest;

    @Autowired
    private AdminSsoKeycloakProperties properties;

    public static final String USER_SYNCHRONIZE_JOB_DETAIL = "User_Synchronize_Job_Detail";
    private static final String USER_SYNCHRONIZE_TRIGGER = "User_Synchronize_Trigger";

    @Bean
    public Map<JobDetail, Set<? extends Trigger>> scheduleJobs() {

        JobDetail appSysExportJobDetail = JobBuilder.newJob().ofType(ApplicationSystemExportJob.class)
                .storeDurably()
                .withIdentity("app_sys_export_job")
                .withDescription("Export Applications and Systems")
                .usingJobData(new JobDataMap())
                .build();
        Trigger triggerForAppSystemExportJob = TriggerBuilder.newTrigger().forJob(appSysExportJobDetail)
                .withIdentity("app_sys_export_trigger")
                .withDescription("Trigger for app_sys_export_job")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();


        JobDetail regionJobDetail = JobBuilder.newJob().ofType(RegionSynchronizeJob.class)
                .storeDurably()
                .withIdentity("region_job_detail")
                .build();
        CronTrigger regionTrigger = TriggerBuilder.newTrigger().forJob(regionJobDetail)
                .withIdentity("region_trigger")
                .withSchedule(cronSchedule(regionUpdateCronExpression))
                .build();


        JobDetail departmentJobDetail = JobBuilder.newJob().ofType(DepartmentSynchronizeJob.class)
                .storeDurably()
                .withIdentity("Department_Job_Detail")
                .build();
        CronTrigger departmentTrigger = TriggerBuilder.newTrigger().forJob(departmentJobDetail)
                .withIdentity("Department_Trigger")
                .withSchedule(cronSchedule(departmentUpdateCronExpression))
                .build();


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


        Map jobDetailAndTriggerMap = new HashMap();
        jobDetailAndTriggerMap.put(appSysExportJobDetail, Set.of(triggerForAppSystemExportJob));
        jobDetailAndTriggerMap.put(regionJobDetail, Set.of(regionTrigger));
        jobDetailAndTriggerMap.put(departmentJobDetail, Set.of(departmentTrigger));
        if ("rdm".equals(organizationSyncOrCrud)) {
            JobDetail organizationJobDetail = JobBuilder.newJob().ofType(OrganizationSynchronizeJob.class)
                    .storeDurably()
                    .withIdentity("organization_job_detail")
                    .build();
            CronTrigger organizationTrigger = TriggerBuilder.newTrigger().forJob(organizationJobDetail)
                    .withIdentity("organization_trigger")
                    .withSchedule(cronSchedule(organizationUpdateCronExpression))
                    .build();
            jobDetailAndTriggerMap.put(organizationJobDetail, Set.of(organizationTrigger));
        }
        if (properties.getSynchronizeEnabled()) {
            jobDetailAndTriggerMap.put(userSynchronizeJobDetail, Set.of(userSynchronizeJobTrigger));
        }
        return jobDetailAndTriggerMap;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean, Map<JobDetail, Set<? extends Trigger>> scheduleJobs) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.scheduleJobs(scheduleJobs, true);
        scheduler.getContext().put(RdmSyncRest.class.getSimpleName(), rdmSyncRest);

        return scheduler;
    }
}
