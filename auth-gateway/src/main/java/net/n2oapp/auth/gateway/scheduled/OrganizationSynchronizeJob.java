package net.n2oapp.auth.gateway.scheduled;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.inovus.ms.rdm.sync.rest.RdmSyncRest;

@Component
@ConditionalOnProperty(name = "access.nsiOrganizationSync")
public class OrganizationSynchronizeJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationSynchronizeJob.class);

    private RdmSyncRest getRdmSyncRest(JobExecutionContext context) {
        String key = RdmSyncRest.class.getSimpleName();
        try {
            return (RdmSyncRest) context.getScheduler().getContext().get(key);
        } catch (SchedulerException e) {
            logger.error("cannot get " + key + "property", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(JobExecutionContext context) {
        logger.info("Organization sync is started");
        getRdmSyncRest(context).update("S003");
        logger.info("Organization sync is completed");
    }
}
