package net.n2oapp.auth.gateway.rdm.scheduled;

import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "access.organization-persist-mode", havingValue = "sync")
public class OrganizationSynchronizeJob extends SynchronizeJob {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationSynchronizeJob.class);

    @Value("${rdm.sync.ref-book-code.organization}")
    private String organizationRefBookCode;

    @Override
    public void execute(JobExecutionContext context) {
        logger.info("Organization sync is started");
        try {
            getRdmSyncRest(context).update(organizationRefBookCode);
        } catch (SchedulerException e) {
            logger.error("cannot get " + KEY + "property", e);
            throw new RuntimeException(e);
        }
        logger.info("Organization sync is completed");
    }
}
