package net.n2oapp.auth.gateway.sheduled;

import net.n2oapp.auth.gateway.scheduled.OrganizationSynchronizeJob;
import net.n2oapp.auth.gateway.scheduled.RegionSynchronizeJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Map;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
public class RdmExportConfiguration {

    @Value("${rdm.export.cron}")
    private String cronExpression;

    @Value("${region-update.cron-expression}")
    private String regionUpdateCronExpression;

    @Value("${organization-update.cron-expression}")
    private String organizationUpdateCronExpression;

    @Value("${department-update.cron-expression}")
    private String departmentUpdateCronExpression;

    @Bean
    public Map<JobDetail, Set<? extends Trigger>> scheduleJobs() {
        JobDetail jobDetail = JobBuilder.newJob().ofType(ApplicationSystemExportJob.class)
                .storeDurably()
                .withIdentity("app_sys_export_job")
                .withDescription("Export Applications and Systems")
                .usingJobData(new JobDataMap())
                .build();
        JobDetail region_job_detail = JobBuilder.newJob().ofType(RegionSynchronizeJob.class)
                .storeDurably()
                .withIdentity("Region_Job_Detail")
                .build();
        JobDetail organizationJobDetail = JobBuilder.newJob().ofType(OrganizationSynchronizeJob.class)
                .storeDurably()
                .withIdentity("Organization_Job_Detail")
                .build();
        /* todo пока отключен
        JobDetail departmentJobDetail = JobBuilder.newJob().ofType(DepartmentSynchronizeJob.class)
                .storeDurably()
                .withIdentity("Department_Job_Detail")
                .build();*/

        Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity("app_sys_export_trigger")
                .withDescription("Trigger for app_sys_export_job")
                .withSchedule(cronSchedule(cronExpression))
                .build();
        CronTrigger regionTrigger = TriggerBuilder.newTrigger().forJob(region_job_detail)
                .withIdentity("Region_Trigger")
                .withSchedule(cronSchedule(regionUpdateCronExpression))
                .build();
        CronTrigger organizationTrigger = TriggerBuilder.newTrigger().forJob(organizationJobDetail)
                .withIdentity("Organization_Trigger")
                .withSchedule(cronSchedule(organizationUpdateCronExpression))
                .build();
        /*  todo пока отключен
            CronTrigger departmentTrigger = TriggerBuilder.newTrigger().forJob(departmentJobDetail)
                .withIdentity("Department_Trigger")
                .withSchedule(cronSchedule(departmentUpdateCronExpression))
                .build();*/

        return Map.of(jobDetail, Set.of(trigger, regionTrigger, organizationTrigger));
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean, Map<JobDetail, Set<? extends Trigger>> scheduleJobs) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.scheduleJobs(scheduleJobs, true);
        return scheduler;
    }

}
