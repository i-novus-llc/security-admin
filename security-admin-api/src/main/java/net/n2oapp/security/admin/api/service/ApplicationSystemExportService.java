package net.n2oapp.security.admin.api.service;


/**
 * Сервис экспорта Приложений и Систем в сервис НСИ
 */
public interface ApplicationSystemExportService {

    void exportApplications();

    void exportSystems();

}
