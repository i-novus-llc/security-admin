package net.n2oapp.security.admin.rdm;

import net.n2oapp.security.admin.api.service.SystemExportService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Задача экспорта Систем и Приложений в НСИ
 */
public class SystemExportJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(SystemExportJob.class);

    @Autowired
    private SystemExportService service;

    @Override
    public void execute(JobExecutionContext context) {
        logger.info("Systems export is started");
        service.exportSystems();
        logger.info("Systems export is completed");
    }
}
