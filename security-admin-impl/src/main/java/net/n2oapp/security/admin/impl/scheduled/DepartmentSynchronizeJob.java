package net.n2oapp.security.admin.impl.scheduled;

import org.quartz.JobExecutionContext;
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
        getRdmSyncRest(context, logger).update(departmentRefBookCode);
        logger.info("Department sync is completed");
    }
}
