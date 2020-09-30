package net.n2oapp.security.admin.impl.scheduled;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "access.organization-persist-mode", havingValue = "sync")
public class OrganizationSynchronizeJob extends SynchronizeJob {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationSynchronizeJob.class);

    @Value("${rdm.sync.ref_book_code.organization}")
    private String organizationRefBookCode;

    @Override
    public void execute(JobExecutionContext context) {
        logger.info("Organization sync is started");
        getRdmSyncRest(context, logger).update(organizationRefBookCode);
        logger.info("Organization sync is completed");
    }
}
