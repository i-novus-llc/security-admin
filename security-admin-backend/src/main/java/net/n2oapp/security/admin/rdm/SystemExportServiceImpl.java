package net.n2oapp.security.admin.rdm;

import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.service.SystemExportService;
import net.n2oapp.security.admin.api.service.SystemService;
import net.n2oapp.security.admin.rdm.feign.RdmDraftRestFeignClient;
import net.n2oapp.security.admin.rdm.feign.RdmPublishRestFeignClient;
import net.n2oapp.security.admin.rdm.feign.RdmRefBookRestFeignClient;
import net.n2oapp.security.admin.rdm.feign.RdmVersionRestFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.i_novus.ms.rdm.api.model.draft.Draft;
import ru.i_novus.ms.rdm.api.model.draft.PublishRequest;
import ru.i_novus.ms.rdm.api.model.refbook.RefBook;
import ru.i_novus.ms.rdm.api.model.refbook.RefBookCriteria;
import ru.i_novus.ms.rdm.api.model.refdata.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;

@Service
public class SystemExportServiceImpl implements SystemExportService {

    private static final Logger log = LoggerFactory.getLogger(SystemExportServiceImpl.class);

    @Value("${rdm.sync.ref-book-code.system}")
    private String systemRefBookCode;

    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    private RdmRefBookRestFeignClient refBookService;
    private SystemService systemService;
    private RdmDraftRestFeignClient draftRestService;
    private RdmPublishRestFeignClient publishService;
    private RdmVersionRestFeignClient versionService;

    @Override
    public void exportSystems() {
        Map<String, AppSystem> systems = systemService
                .findAllSystems(new SystemCriteria(0, Integer.MAX_VALUE))
                .get().collect(Collectors.toMap(AppSystem::getCode, system -> system));

        update(systems, systemRefBookCode);
    }

    private RefBook findRefBook(String refBookCode) {
        RefBookCriteria refBookCriteria = new RefBookCriteria();
        refBookCriteria.setCode(refBookCode);
        refBookCriteria.setHasPublished(true);
        Page<RefBook> search = refBookService.search(refBookCriteria);
        if (search.getContent().isEmpty()) {
            log.warn("published refBook {} not found", refBookCode);
            return null;
        }
        return search.getContent().get(0);
    }

    private void update(Map<String, ?> source, String refBookCode) {
        if (draftRestService == null || publishService == null || refBookService == null || versionService == null) {
            log.warn("Export to the RDM disabled, please set 'rdm.client.export.url' property.");
            return;
        }
        RefBook refBook = findRefBook(refBookCode);
        if (refBook == null)
            return;

        List<RefBookRowValue> rdmData = pullRdmData(refBookCode);

        Map<String, Object> forCreate = new HashMap<>(source);
        List<RefBookRowValue> forRemove = new ArrayList<>();
        Map<Long, Object> forUpdate = new HashMap<>();

        for (RefBookRowValue refBookRowValue : rdmData) {
            String code = (String) refBookRowValue.getFieldValue(CODE).getValue();
            if (!source.containsKey(code)) {
                forRemove.add(refBookRowValue);
            } else {
                if (source.get(code) instanceof AppSystem && !checkSystemEquivalence(refBookRowValue, (AppSystem) source.get(code))) {
                    forUpdate.put(refBookRowValue.getSystemId(), source.get(code));
                }
            }
            forCreate.remove(code);
        }

        publish(refBook, forCreate, forRemove, forUpdate);
    }

    private void publish(RefBook refBook, Map<String, Object> forCreate, List<RefBookRowValue> forRemove, Map<Long, Object> forUpdate) {
        if (!forCreate.isEmpty() || !forUpdate.isEmpty() || !forRemove.isEmpty()) {
            Draft draft = draftRestService.createFromVersion(refBook.getId());
            forCreate.values().forEach(s -> draftRestService.updateData(draft.getId(), new UpdateDataRequest(draft.getOptLockValue(), createRow(s))));
            forUpdate.forEach((k, v) -> {
                Row row = createRow(v);
                row.setSystemId(k);
                draftRestService.updateData(draft.getId(), new UpdateDataRequest(draft.getOptLockValue(), row));
            });
            forRemove.forEach(rowValue -> draftRestService.deleteData(draft.getId(), new DeleteDataRequest(draft.getOptLockValue(), new Row(rowValue.getSystemId(), emptyMap()))));
            publishService.publish(draft.getId(), new PublishRequest(draft.getOptLockValue()));
        }
    }

    private List<RefBookRowValue> pullRdmData(String refBookCode) {
        Page<RefBookRowValue> page = versionService.search(refBookCode, new SearchDataCriteria(0, 10));
        List<RefBookRowValue> target = new ArrayList<>(page.getContent());
        for (int i = 0; i < page.getTotalElements() / 10; i++) {
            target.addAll(versionService.search(refBookCode, new SearchDataCriteria(i + 1, 10)).getContent());
        }

        return target;
    }

    private boolean checkSystemEquivalence(RefBookRowValue refBookRowValue, AppSystem system) {
        return checkEquivalence(system::getDescription, refBookRowValue.getFieldValue(DESCRIPTION)::getValue)
                && checkEquivalence(system::getName, refBookRowValue.getFieldValue(NAME)::getValue);
    }

    private boolean checkEquivalence(Supplier val1, Supplier val2) {
        return (val1.get() == null || val2.get() != null)
                && (val1.get() != null || val2.get() == null)
                && (val1.get() == null || val1.get().equals(val2.get()));
    }

    private Row createRow(Object obj) {
        Map<String, Object> data = new HashMap<>();
        if (obj instanceof AppSystem) {
            AppSystem system = (AppSystem) obj;
            data.put(CODE, system.getCode());
            data.put(NAME, system.getName());
            data.put(DESCRIPTION, system.getDescription());
        }
        return new Row(data);
    }

    @Autowired(required = false)
    public void setRefBookService(RdmRefBookRestFeignClient refBookService) {
        this.refBookService = refBookService;
    }

    @Autowired
    public void setApplicationSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @Autowired(required = false)
    public void setDraftRestService(RdmDraftRestFeignClient draftRestService) {
        this.draftRestService = draftRestService;
    }

    @Autowired(required = false)
    public void setPublishService(RdmPublishRestFeignClient publishService) {
        this.publishService = publishService;
    }

    @Autowired(required = false)
    public void setVersionService(RdmVersionRestFeignClient versionService) {
        this.versionService = versionService;
    }
}
