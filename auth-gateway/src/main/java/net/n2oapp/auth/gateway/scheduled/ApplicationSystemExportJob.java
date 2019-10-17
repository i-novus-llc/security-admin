package net.n2oapp.auth.gateway.scheduled;

import net.n2oapp.security.admin.api.service.ApplicationSystemExportService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Задача экспорта Систем и Приложений в НСИ
 */
public class ApplicationSystemExportJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationSystemExportJob.class);

    @Autowired
    private ApplicationSystemExportService service;

    @Override
    public void execute(JobExecutionContext context) {
        logger.info("Systems export is started");
        service.exportSystems();
        logger.info("Systems export is completed");
        logger.info("Applications export is started");
        service.exportApplications();
        logger.info("Applications export is completed");
    }
}
