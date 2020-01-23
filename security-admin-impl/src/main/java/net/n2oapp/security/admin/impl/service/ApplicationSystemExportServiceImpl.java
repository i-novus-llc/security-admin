package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.ApplicationCriteria;
import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.service.ApplicationSystemExportService;
import net.n2oapp.security.admin.api.service.ApplicationSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.i_novus.platform.datastorage.temporal.model.Reference;
import ru.inovus.ms.rdm.api.model.draft.Draft;
import ru.inovus.ms.rdm.api.model.refbook.RefBook;
import ru.inovus.ms.rdm.api.model.refbook.RefBookCriteria;
import ru.inovus.ms.rdm.api.model.refdata.RefBookRowValue;
import ru.inovus.ms.rdm.api.model.refdata.Row;
import ru.inovus.ms.rdm.api.model.refdata.SearchDataCriteria;
import ru.inovus.ms.rdm.api.service.DraftService;
import ru.inovus.ms.rdm.api.service.PublishService;
import ru.inovus.ms.rdm.api.service.RefBookService;
import ru.inovus.ms.rdm.api.service.VersionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;

@Service
public class ApplicationSystemExportServiceImpl implements ApplicationSystemExportService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationSystemExportServiceImpl.class);

    static final String SYSTEM_REF_BOOK_CODE = "SYS001";
    static final String APPLICATION_REF_BOOK_CODE = "APP001";

    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String OAUTH = "oauth";
    private static final String SYSTEM_CODE = "system";

    private RefBookService refBookService;
    private ApplicationSystemService applicationSystemService;
    private DraftService draftService;
    private PublishService publishService;
    private VersionService versionService;

    @Override
    public void exportApplications() {
        Map<String, Application> apps = applicationSystemService
                .findAllApplications(new ApplicationCriteria(0, Integer.MAX_VALUE))
                .get().collect(Collectors.toMap(Application::getCode, application -> application));

        update(apps, APPLICATION_REF_BOOK_CODE);
    }

    @Override
    public void exportSystems() {
        Map<String, AppSystem> systems = applicationSystemService
                .findAllSystems(new SystemCriteria(0, Integer.MAX_VALUE))
                .get().collect(Collectors.toMap(AppSystem::getCode, system -> system));

        update(systems, SYSTEM_REF_BOOK_CODE);
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
        if (draftService == null || publishService == null || refBookService == null || versionService == null) {
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
                if (source.get(code) instanceof AppSystem && !checkSystemEquivalence(refBookRowValue, (AppSystem) source.get(code))
                        || source.get(code) instanceof Application && !checkAppEquivalence(refBookRowValue, (Application) source.get(code))) {
                    forUpdate.put(refBookRowValue.getSystemId(), source.get(code));
                }
            }
            forCreate.remove(code);
        }

        publish(refBook, forCreate, forRemove, forUpdate);
    }

    private void publish(RefBook refBook, Map<String, Object> forCreate, List<RefBookRowValue> forRemove, Map<Long, Object> forUpdate) {
        if (!forCreate.isEmpty() || !forUpdate.isEmpty() || !forRemove.isEmpty()) {
            Draft draft = draftService.createFromVersion(refBook.getId());
            forCreate.values().forEach(s -> draftService.updateData(draft.getId(), createRow(s)));
            forUpdate.forEach((k, v) -> {
                Row row = createRow(v);
                row.setSystemId(k);
                draftService.updateData(draft.getId(), row);
            });
            forRemove.forEach(rowValue -> draftService.deleteRow(draft.getId(), new Row(rowValue.getSystemId(), emptyMap())));
            publishService.publish(draft.getId(), null, null, null, false);
        }
    }

    private List<RefBookRowValue> pullRdmData(String refBookCode) {
        Page<RefBookRowValue> page = versionService.search(refBookCode, new SearchDataCriteria(0, 10, null));
        List<RefBookRowValue> target = new ArrayList<>(page.getContent());
        for (int i = 0; i < page.getTotalElements() / 10; i++) {
            target.addAll(versionService.search(refBookCode, new SearchDataCriteria(i + 1, 10, null)).getContent());
        }

        return target;
    }

    private boolean checkSystemEquivalence(RefBookRowValue refBookRowValue, AppSystem system) {
        return checkEquivalence(system::getDescription, refBookRowValue.getFieldValue(DESCRIPTION)::getValue)
                && checkEquivalence(system::getName, refBookRowValue.getFieldValue(NAME)::getValue);
    }

    private boolean checkAppEquivalence(RefBookRowValue refBookRowValue, Application app) {
        return checkEquivalence(app::getOAuth, refBookRowValue.getFieldValue(OAUTH)::getValue)
                && checkEquivalence(app::getName, refBookRowValue.getFieldValue(NAME)::getValue)
                && checkEquivalence(app::getSystemCode, ((Reference) refBookRowValue.getFieldValue(SYSTEM_CODE).getValue())::getValue);
    }

    private boolean checkEquivalence(Supplier val1, Supplier val2) {
        return (val1.get() == null || val2.get() != null)
                && (val1.get() != null || val2.get() == null)
                && (val1.get() == null || val1.get().equals(val2.get()));
    }

    private Row createRow(Object obj) {
        Map<String, Object> data = new HashMap<>();
        if (obj instanceof Application) {
            Application app = (Application) obj;
            data.put(CODE, app.getCode());
            data.put(NAME, app.getName());
            data.put(OAUTH, app.getOAuth());
            data.put(SYSTEM_CODE, app.getSystemCode());
        } else if (obj instanceof AppSystem) {
            AppSystem system = (AppSystem) obj;
            data.put(CODE, system.getCode());
            data.put(NAME, system.getName());
            data.put(DESCRIPTION, system.getDescription());
        }
        return new Row(data);
    }

    @Autowired(required = false)
    public void setRefBookService(RefBookService refBookService) {
        this.refBookService = refBookService;
    }

    @Autowired
    public void setApplicationSystemService(ApplicationSystemService applicationSystemService) {
        this.applicationSystemService = applicationSystemService;
    }

    @Autowired(required = false)
    public void setDraftService(DraftService draftService) {
        this.draftService = draftService;
    }

    @Autowired(required = false)
    public void setPublishService(PublishService publishService) {
        this.publishService = publishService;
    }

    @Autowired(required = false)
    public void setVersionService(VersionService versionService) {
        this.versionService = versionService;
    }
}
