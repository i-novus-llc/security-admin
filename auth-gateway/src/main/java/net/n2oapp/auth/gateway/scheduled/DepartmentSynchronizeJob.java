package net.n2oapp.auth.gateway.scheduled;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.inovus.ms.rdm.sync.rest.RdmSyncRest;

@Component
public class DepartmentSynchronizeJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentSynchronizeJob.class);

    @Value("${rdm.department.sync.job.code}")
    private String departmentSyncJobCode;

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
        logger.info("Department sync is started");
        getRdmSyncRest(context).update(departmentSyncJobCode);
        logger.info("Department sync is completed");
    }
}
