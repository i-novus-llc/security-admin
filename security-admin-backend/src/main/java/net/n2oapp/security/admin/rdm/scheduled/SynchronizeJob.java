package net.n2oapp.security.admin.rdm.scheduled;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import ru.i_novus.ms.rdm.sync.api.service.RdmSyncService;

public abstract class SynchronizeJob implements Job {
    public static final String KEY = RdmSyncService.class.getSimpleName();

    protected RdmSyncService getRdmSyncRest(JobExecutionContext context) throws SchedulerException {
        return (RdmSyncService) context.getScheduler().getContext().get(KEY);
    }
}
