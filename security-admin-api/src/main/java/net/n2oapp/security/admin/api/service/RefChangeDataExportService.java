package net.n2oapp.security.admin.api.service;

import java.io.Serializable;
import java.util.List;

/**
 * Сервис для экспорта измененных данных справочников
 */
public interface RefChangeDataExportService {

    public <T extends Serializable> void changeSystemData(List<? extends T> addUpdate, List<? extends T> delete);

    public <T extends Serializable> void changeApplicationData(List<? extends T> addUpdate, List<? extends T> delete);
}
