package net.n2oapp.security.admin.rdm.scheduled;

import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DepartmentSynchronizeJob extends SynchronizeJob {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentSynchronizeJob.class);

    @Value("${rdm.sync.ref-book-code.department}")
    private String departmentRefBookCode;

    @Override
    public void execute(JobExecutionContext context) {
        logger.info("Department sync is started");
        try {
            getRdmSyncRest(context).update(departmentRefBookCode);
        } catch (SchedulerException e) {
            logger.error("cannot get " + KEY + "property", e);
            throw new RuntimeException(e);
        }
        logger.info("Department sync is completed");
    }
}
