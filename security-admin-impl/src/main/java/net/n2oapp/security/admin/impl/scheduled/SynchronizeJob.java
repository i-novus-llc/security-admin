package net.n2oapp.security.admin.impl.scheduled;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import ru.inovus.ms.rdm.sync.rest.RdmSyncRest;

public abstract class SynchronizeJob implements Job {

    protected RdmSyncRest getRdmSyncRest(JobExecutionContext context, Logger logger) {
        String key = RdmSyncRest.class.getSimpleName();
        try {
            return (RdmSyncRest) context.getScheduler().getContext().get(key);
        } catch (SchedulerException e) {
            logger.error("cannot get " + key + "property", e);
            throw new RuntimeException(e);
        }
    }
}
