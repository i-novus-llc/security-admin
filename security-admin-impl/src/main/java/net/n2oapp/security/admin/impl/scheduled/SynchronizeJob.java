package net.n2oapp.security.admin.impl.scheduled;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import ru.inovus.ms.rdm.sync.rest.RdmSyncRest;

public abstract class SynchronizeJob implements Job {
    public static final String KEY = RdmSyncRest.class.getSimpleName();

    protected RdmSyncRest getRdmSyncRest(JobExecutionContext context) throws SchedulerException {
        return (RdmSyncRest) context.getScheduler().getContext().get(KEY);
    }
}
