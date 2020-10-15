package net.n2oapp.auth.gateway;

import net.n2oapp.security.admin.impl.scheduled.*;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import ru.inovus.ms.rdm.sync.rest.RdmSyncRest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
@ConditionalOnProperty(prefix = "rdm.sync", name = "enabled", havingValue = "true")
public class RdmSyncConfiguration {

    public static final String APP_SYS_EXPORT_JOB_NAME = "app_sys_export_job";
    public static final String REGION_SYNC_JOB_NAME = "region_job_detail";
    public static final String DEPARTMENT_SYNC_JOB_NAME = "department_job_detail";
    public static final String ORGANIZATION_SYNC_JOB_NAME = "organization_job_detail";

    @Value("${rdm.cron.export}")
    private String cronExpression;

    @Value("${rdm.cron.import.region}")
    private String regionUpdateCronExpression;

    @Value("${rdm.cron.import.organization}")
    private String organizationUpdateCronExpression;

    @Value("${rdm.cron.import.department}")
    private String departmentUpdateCronExpression;

    @Autowired(required = false)
    private RdmSyncRest rdmSyncRest;

    @Bean
    public SynchronizationInfo appSysExportJobAndTrigger() {
        JobDetail appSysExportJobDetail = JobBuilder.newJob().ofType(ApplicationSystemExportJob.class)
                .storeDurably()
                .withIdentity(APP_SYS_EXPORT_JOB_NAME)
                .withDescription("Export Applications and Systems")
                .usingJobData(new JobDataMap())
                .build();
        Trigger triggerForAppSystemExportJob = TriggerBuilder.newTrigger().forJob(appSysExportJobDetail)
                .withIdentity("app_sys_export_trigger")
                .withDescription("Trigger for app_sys_export_job")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        return new SynchronizationInfo(appSysExportJobDetail, Set.of(triggerForAppSystemExportJob));
    }

    @Bean
    public SynchronizationInfo regionJobAndTrigger() {
        JobDetail regionJobDetail = JobBuilder.newJob().ofType(RegionSynchronizeJob.class)
                .storeDurably()
                .withIdentity(REGION_SYNC_JOB_NAME)
                .build();
        CronTrigger regionTrigger = TriggerBuilder.newTrigger().forJob(regionJobDetail)
                .withIdentity("region_trigger")
                .withSchedule(cronSchedule(regionUpdateCronExpression))
                .build();

        return new SynchronizationInfo(regionJobDetail, Set.of(regionTrigger));
    }

    @Bean
    public SynchronizationInfo departmentJobAndTrigger() {
        JobDetail departmentJobDetail = JobBuilder.newJob().ofType(DepartmentSynchronizeJob.class)
                .storeDurably()
                .withIdentity(DEPARTMENT_SYNC_JOB_NAME)
                .build();
        CronTrigger departmentTrigger = TriggerBuilder.newTrigger().forJob(departmentJobDetail)
                .withIdentity("Department_Trigger")
                .withSchedule(cronSchedule(departmentUpdateCronExpression))
                .build();

        return new SynchronizationInfo(departmentJobDetail, Set.of(departmentTrigger));
    }

    @Bean
    @ConditionalOnProperty(name = "access.organization-persist-mode", havingValue = "sync")
    public SynchronizationInfo organizationJobAndTrigger() {
        JobDetail organizationJobDetail = JobBuilder.newJob().ofType(OrganizationSynchronizeJob.class)
                .storeDurably()
                .withIdentity(ORGANIZATION_SYNC_JOB_NAME)
                .build();
        CronTrigger organizationTrigger = TriggerBuilder.newTrigger().forJob(organizationJobDetail)
                .withIdentity("organization_trigger")
                .withSchedule(cronSchedule(organizationUpdateCronExpression))
                .build();
        return new SynchronizationInfo(organizationJobDetail, Set.of(organizationTrigger));
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean, List<SynchronizationInfo> synchronizationInfos) throws SchedulerException {
        Map<JobDetail, Set<? extends Trigger>> scheduleJobs = synchronizationInfos.stream().collect(Collectors.toMap(SynchronizationInfo::getJob, SynchronizationInfo::getTrigger));
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.scheduleJobs(scheduleJobs, true);
        scheduler.getContext().put(RdmSyncRest.class.getSimpleName(), rdmSyncRest);

        return scheduler;
    }
}
