package net.n2oapp.auth.gateway.scheduled;

import lombok.Getter;
import lombok.Setter;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.util.Set;

@Getter
@Setter
public class SynchronizationInfo {

    public SynchronizationInfo(JobDetail job, Set<? extends Trigger> triggers) {
        this.job = job;
        this.trigger = triggers;
    }

    private JobDetail job;
    private Set<? extends Trigger> trigger;
}
