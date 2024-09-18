package net.n2oapp.security.admin.rdm.scheduled;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "rdm.sync", name = "enabled", havingValue = "false")
public class QuartzSchedulerCustomizer implements SchedulerFactoryBeanCustomizer {

    private Scheduler quartzScheduler;

    @Autowired
    public QuartzSchedulerCustomizer(@Lazy Scheduler quartzScheduler) {
        this.quartzScheduler = quartzScheduler;
    }

    @Override
    public void customize(SchedulerFactoryBean schedulerFactoryBean) {
        schedulerFactoryBean.setGlobalTriggerListeners(new QuartzGlobalTriggerListener(quartzScheduler));
    }
}
