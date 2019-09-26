package net.n2oapp.auth.gateway.sheduled;

import net.n2oapp.security.admin.api.service.ApplicationSystemExportService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RdmExportConfiguration {

    @Value("${rdm.export.cron}")
    private String cronExpression;

    @Autowired
    private ApplicationSystemExportService exportService;

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob().ofType(ApplicationSystemExportJob.class)
                .storeDurably()
                .withIdentity("app_sys_export_job")
                .withDescription("Export Applications and Systems")
                .usingJobData(new JobDataMap())
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("app_sys_export_trigger")
                .withDescription("Trigger for app_sys_export_job")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }

    @Bean
    public SchedulerFactoryBeanCustomizer schedulerContextCustomizer() {
        return (schedulerFactoryBean) ->
                schedulerFactoryBean.setSchedulerContextAsMap(Map.of(ApplicationSystemExportService.class.getPackageName(), exportService));
    }
}
