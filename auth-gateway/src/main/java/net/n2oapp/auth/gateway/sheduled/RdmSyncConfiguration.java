package net.n2oapp.auth.gateway.sheduled;

import net.n2oapp.auth.gateway.scheduled.DepartmentSynchronizeJob;
import net.n2oapp.auth.gateway.scheduled.OrganizationSynchronizeJob;
import net.n2oapp.auth.gateway.scheduled.RegionSynchronizeJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import ru.inovus.ms.rdm.sync.rest.RdmSyncRest;

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
    @Autowired
    private RdmSyncRest rdmSyncRest;

    @Bean
    public Map<JobDetail, Set<? extends Trigger>> scheduleJobs() {
        JobDetail appSysExportJobDetail = JobBuilder.newJob().ofType(ApplicationSystemExportJob.class)
                .storeDurably()
                .withIdentity("app_sys_export_job")
                .withDescription("Export Applications and Systems")
                .usingJobData(new JobDataMap())
                .build();
        JobDetail regionJobDetail = JobBuilder.newJob().ofType(RegionSynchronizeJob.class)
                .storeDurably()
                .withIdentity("region_job_detail")
                .build();
        JobDetail organizationJobDetail = JobBuilder.newJob().ofType(OrganizationSynchronizeJob.class)
                .storeDurably()
                .withIdentity("organization_job_detail")
                .build();
        JobDetail departmentJobDetail = JobBuilder.newJob().ofType(DepartmentSynchronizeJob.class)
                .storeDurably()
                .withIdentity("Department_Job_Detail")
                .build();

        Trigger triggerForAppSystemExportJob = TriggerBuilder.newTrigger().forJob(appSysExportJobDetail)
                .withIdentity("app_sys_export_trigger")
                .withDescription("Trigger for app_sys_export_job")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        CronTrigger regionTrigger = TriggerBuilder.newTrigger().forJob(regionJobDetail)
                .withIdentity("region_trigger")
                .withSchedule(cronSchedule(regionUpdateCronExpression))
                .build();
        CronTrigger organizationTrigger = TriggerBuilder.newTrigger().forJob(organizationJobDetail)
                .withIdentity("organization_trigger")
                .withSchedule(cronSchedule(organizationUpdateCronExpression))
                .build();
        CronTrigger departmentTrigger = TriggerBuilder.newTrigger().forJob(departmentJobDetail)
                .withIdentity("Department_Trigger")
                .withSchedule(cronSchedule(departmentUpdateCronExpression))
                .build();


        return Map.of(appSysExportJobDetail, Set.of(triggerForAppSystemExportJob),
                regionJobDetail, Set.of(regionTrigger),
                organizationJobDetail, Set.of(organizationTrigger),
                departmentJobDetail, Set.of(departmentTrigger));
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean, Map<JobDetail, Set<? extends Trigger>> scheduleJobs) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.scheduleJobs(scheduleJobs, true);
        scheduler.getContext().put(RdmSyncRest.class.getSimpleName(), rdmSyncRest);
        return scheduler;
    }
}
