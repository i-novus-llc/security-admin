package net.n2oapp.security.admin.impl.scheduled;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegionSynchronizeJob extends SynchronizeJob {

    private static final Logger logger = LoggerFactory.getLogger(RegionSynchronizeJob.class);

    @Value("${rdm.sync.ref-book-code.region}")
    private String regionRefBookCode;

    @Override
    public void execute(JobExecutionContext context) {
        logger.info("Region sync is started");
        getRdmSyncRest(context, logger).update(regionRefBookCode);
        logger.info("Region sync is completed");
    }
}
