package net.n2oapp.security.admin.rdm.scheduled;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.inovus.ms.rdm.sync.service.change_data.RdmSyncExportDirtyRecordsToRdmJob;

import java.util.Arrays;

import static java.util.Objects.nonNull;

public class QuartzGlobalTriggerListener implements TriggerListener {

    public static final String APP_SYS_EXPORT_JOB_NAME = "app_sys_export_job";
    public static final String REGION_SYNC_JOB_NAME = "region_job_detail";
    public static final String DEPARTMENT_SYNC_JOB_NAME = "department_job_detail";
    public static final String ORGANIZATION_SYNC_JOB_NAME = "organization_job_detail";

    private static final Logger logger = LoggerFactory.getLogger(QuartzGlobalTriggerListener.class);

    private static final String[] IGNORE_JOBS = {RdmSyncExportDirtyRecordsToRdmJob.NAME,
            APP_SYS_EXPORT_JOB_NAME,
            REGION_SYNC_JOB_NAME,
            DEPARTMENT_SYNC_JOB_NAME,
            ORGANIZATION_SYNC_JOB_NAME};
    private Scheduler quartzScheduler;

    public QuartzGlobalTriggerListener(Scheduler quartzScheduler) {
        this.quartzScheduler = quartzScheduler;
    }

    @Override
    public String getName() {
        return QuartzGlobalTriggerListener.class.getSimpleName();
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext jobExecutionContext) {
        //operation not supported
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext jobExecutionContext) {

        boolean result = false;

        JobKey jobKey = trigger.getJobKey();

        if (isIgnoringJob(jobKey)) {

            deleteJob(jobKey);

            result = true;
        }

        return result;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        //operation not supported
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext jobExecutionContext, Trigger.CompletedExecutionInstruction completedExecutionInstruction) {
        //operation not supported
    }

    private boolean isIgnoringJob(JobKey jobKey) {
        return nonNull(jobKey)
                && nonNull(jobKey.getName())
                && Arrays.stream(IGNORE_JOBS).anyMatch(jobKey.getName()::equals);
    }

    private void deleteJob(JobKey jobKey) {
        try {
            quartzScheduler.deleteJob(jobKey);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }
}
