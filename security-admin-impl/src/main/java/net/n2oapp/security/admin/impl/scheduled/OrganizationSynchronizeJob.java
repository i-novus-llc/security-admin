package net.n2oapp.security.admin.impl.scheduled;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.inovus.ms.rdm.sync.rest.RdmSyncRest;

@Component
@ConditionalOnProperty(name = "access.organization-persist-mode", havingValue = "sync")
public class OrganizationSynchronizeJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationSynchronizeJob.class);

    @Value("${rdm.sync.ref_book_code.organization}")
    private String organizationRefBookCode;

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
        getRdmSyncRest(context).update(organizationRefBookCode);
        logger.info("Organization sync is completed");
    }
}
