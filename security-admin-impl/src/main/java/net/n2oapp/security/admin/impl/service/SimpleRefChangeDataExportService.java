package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.service.RefChangeDataExportService;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
public class SimpleRefChangeDataExportService implements RefChangeDataExportService {
    @Override
    public <T extends Serializable> void changeSystemData(List<? extends T> addUpdate, List<? extends T> delete) {

    }

    @Override
    public <T extends Serializable> void changeApplicationData(List<? extends T> addUpdate, List<? extends T> delete) {

    }
}
