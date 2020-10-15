package net.n2oapp.auth.gateway.scheduled;

import lombok.Getter;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.util.Set;

@Getter
public class SynchronizationInfo {

    private final JobDetail job;
    private final Set<? extends Trigger> trigger;

    public SynchronizationInfo(JobDetail job, Set<? extends Trigger> triggers) {
        this.job = job;
        this.trigger = triggers;
    }

}
