package net.n2oapp.auth.gateway.sheduled;

import net.n2oapp.security.admin.api.service.ApplicationSystemExportService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Задача экспорта Систем и Приложений в НСИ
 */
public class ApplicationSystemExportJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationSystemExportJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        ApplicationSystemExportService service = extractService(context);
        logger.info("Systems export is started");
        service.exportSystems();
        logger.info("Systems export is completed");
        logger.info("Applications export is started");
        service.exportApplications();
        logger.info("Applications export is completed");
    }

    private ApplicationSystemExportService extractService(JobExecutionContext context) {
        try {
            return (ApplicationSystemExportService) context.getScheduler().getContext()
                    .get(ApplicationSystemExportService.class.getPackageName());
        } catch (SchedulerException e) {
            logger.info("Context does not contains a " + ApplicationSystemExportService.class.getPackageName());
            throw new IllegalStateException(e);
        }
    }
}
