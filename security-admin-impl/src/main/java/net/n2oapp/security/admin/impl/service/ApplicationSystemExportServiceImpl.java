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
import ru.inovus.ms.rdm.model.draft.Draft;
import ru.inovus.ms.rdm.model.refbook.RefBook;
import ru.inovus.ms.rdm.model.refbook.RefBookCriteria;
import ru.inovus.ms.rdm.model.refdata.RefBookRowValue;
import ru.inovus.ms.rdm.model.refdata.Row;
import ru.inovus.ms.rdm.model.refdata.SearchDataCriteria;
import ru.inovus.ms.rdm.service.api.DraftService;
import ru.inovus.ms.rdm.service.api.PublishService;
import ru.inovus.ms.rdm.service.api.RefBookService;
import ru.inovus.ms.rdm.service.api.VersionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
public class ApplicationSystemExportServiceImpl implements ApplicationSystemExportService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationSystemExportServiceImpl.class);

    private static final String SYSTEM_REF_BOOK_CODE = "SYS001";
    private static final String APPLICATION_REF_BOOK_CODE = "APP001";

    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String OAUTH = "oauth";
    private static final String SYSTEM_CODE = "system";

    @Autowired
    private RefBookService refBookService;
    @Autowired
    private ApplicationSystemService applicationSystemService;
    @Autowired
    private DraftService draftService;
    @Autowired
    private PublishService publishService;
    @Autowired
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

    private void update(Map<String, ?> map, String refBookCode) {
        RefBook refBook = findRefBook(refBookCode);
        if (refBook == null || !isDataDifferent(map, refBookCode))
            return;

        Draft draft = draftService.createFromVersion(refBook.getId());
        map.forEach((k, v) -> {
            if (v instanceof AppSystem)
                draftService.updateData(draft.getId(), createRow((AppSystem) v));
            else if (v instanceof Application)
                draftService.updateData(draft.getId(), createRow((Application) v));
        });
        publishService.publish(draft.getId(), null, null, null, false);
    }

    private boolean isDataDifferent(Map<String, ?> source, String code) {
        Page<RefBookRowValue> page = versionService.search(code, new SearchDataCriteria(0, 10, null));
        List<RefBookRowValue> target = new ArrayList<>(page.getContent());
        for (int i = 0; i < page.getTotalElements() / 10; i++) {
            target.addAll(versionService.search(code, new SearchDataCriteria(i + 1, 10, null)).getContent());
        }

        if (target.size() != source.size())
            return true;

        for (RefBookRowValue refBookRowValue : target) {
            if (!checkEquivalence(refBookRowValue, source))
                return true;
        }
        return false;
    }

    private boolean checkEquivalence(RefBookRowValue refBookRowValue, Map<String, ?> source) {
        String code = refBookRowValue.getFieldValue(CODE).getValue().toString();
        if (!source.containsKey(code))
            return false;
        Object target = null;
        if (source.get(code) instanceof Application) {
            target = createApplication(refBookRowValue);
        } else if (source.get(code) instanceof AppSystem) {
            target = createAppSystem(refBookRowValue);
        }

        return source.containsKey(code) && source.get(code).equals(target);
    }

    private Application createApplication(RefBookRowValue refBookRowValue) {
        Application application = new Application();
        if (nonNull(refBookRowValue.getFieldValue(CODE)))
            application.setCode((String) refBookRowValue.getFieldValue(CODE).getValue());
        if (nonNull(refBookRowValue.getFieldValue(NAME)))
            application.setName((String) refBookRowValue.getFieldValue(NAME).getValue());
        if (nonNull(refBookRowValue.getFieldValue(SYSTEM_CODE)) && refBookRowValue.getFieldValue(SYSTEM_CODE).getValue() instanceof Reference)
            application.setSystemCode(((Reference) refBookRowValue.getFieldValue(SYSTEM_CODE).getValue()).getValue());
        if (nonNull(refBookRowValue.getFieldValue(OAUTH)))
            application.setOAuth((Boolean) refBookRowValue.getFieldValue(OAUTH).getValue());
        return application;
    }

    private AppSystem createAppSystem(RefBookRowValue refBookRowValue) {
        AppSystem system = new AppSystem();
        if (nonNull(refBookRowValue.getFieldValue(CODE).getValue()))
            system.setCode(refBookRowValue.getFieldValue(CODE).getValue().toString());
        if (nonNull(refBookRowValue.getFieldValue(NAME).getValue()))
            system.setName(refBookRowValue.getFieldValue(NAME).getValue().toString());
        if (nonNull(refBookRowValue.getFieldValue(DESCRIPTION).getValue()))
            system.setDescription(refBookRowValue.getFieldValue(DESCRIPTION).getValue().toString());
        return system;
    }

    private Row createRow(AppSystem system) {
        Map<String, Object> data = new HashMap<>();
        data.put(CODE, system.getCode());
        data.put(NAME, system.getName());
        data.put(DESCRIPTION, system.getDescription());
        return new Row(data);
    }

    private Row createRow(Application app) {
        Map<String, Object> data = new HashMap<>();
        data.put(CODE, app.getCode());
        data.put(NAME, app.getName());
        data.put(OAUTH, app.getOAuth());
        data.put(SYSTEM_CODE, app.getSystemCode());
        return new Row(data);
    }

}
