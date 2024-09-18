package net.n2oapp.security.admin.rdm;

import net.n2oapp.security.admin.api.service.RefChangeDataExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.i_novus.ms.rdm.sync.service.change_data.RdmChangeDataClient;

import java.io.Serializable;
import java.util.List;

public class RdmRefChangeDataExportService implements RefChangeDataExportService {

    @Autowired(required = false)
    private RdmChangeDataClient changeDataClient;

    @Value("${rdm.sync.ref-book-code.system}")
    private String systemRefBookCode;

    @Value("${rdm.sync.ref-book-code.application}")
    private String applicationRefBookCode;

    @Override
    public <T extends Serializable> void changeSystemData(List<? extends T> addUpdate, List<? extends T> delete) {
        if (changeDataClient != null)
            changeDataClient.changeData(systemRefBookCode, addUpdate, delete);
    }

    @Override
    public <T extends Serializable> void changeApplicationData(List<? extends T> addUpdate, List<? extends T> delete) {
        if (changeDataClient != null)
            changeDataClient.changeData(applicationRefBookCode, addUpdate, delete);
    }
}
