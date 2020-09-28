package net.n2oapp.security.admin.impl.scheduled;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.inovus.ms.rdm.sync.rest.RdmSyncRest;

@Component
public class RegionSynchronizeJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(RegionSynchronizeJob.class);

    @Value("${rdm.sync.ref_book_code.region}")
    private String regionRefBookCode;

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
        logger.info("Region sync is started");
        getRdmSyncRest(context).update(regionRefBookCode);
        logger.info("Region sync is completed");
    }
}
