package net.n2oapp.security.admin.rdm;

import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.criteria.ApplicationCriteria;
import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.service.ApplicationSystemExportService;
import net.n2oapp.security.admin.api.service.ApplicationSystemService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.platform.datastorage.temporal.model.Reference;
import ru.i_novus.platform.datastorage.temporal.model.value.BooleanFieldValue;
import ru.i_novus.platform.datastorage.temporal.model.value.ReferenceFieldValue;
import ru.i_novus.platform.datastorage.temporal.model.value.StringFieldValue;
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

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:test.properties")
@SpringBootTest(classes = TestApplication.class)
public class ApplicationSystemExportServiceImplTest {
    @SpyBean
    private VersionService versionService;
    @SpyBean
    private RefBookService refBookService;
    @SpyBean
    private DraftService draftService;
    @SpyBean
    private PublishService publishService;

    @SpyBean
    private ApplicationSystemService applicationSystemService;
    @Autowired
    private ApplicationSystemExportService exportService;

    @Test
    public void testAppExport() {
        RefBook refBook = new RefBook();
        refBook.setId(123);

        Application application = new Application();
        application.setCode("testApp");
        application.setName("testAppName");
        application.setOAuth(false);
        application.setSystemCode("testSystem");
        Page<Application> page = new PageImpl<>(Collections.singletonList(application));

        RefBookRowValue rowValue = new RefBookRowValue();
        StringFieldValue code = new StringFieldValue("code", "testApp");
        StringFieldValue name = new StringFieldValue("name", "testAppName");
        BooleanFieldValue oauth = new BooleanFieldValue("oauth", false);
        ReferenceFieldValue systemCode = new ReferenceFieldValue("system", new Reference());
        rowValue.setFieldValues(Arrays.asList(code, name, oauth, systemCode));

        mock(refBook, rowValue, page, null);

        exportService.exportApplications();

        ArgumentCaptor<RefBookCriteria> refBookCriteriaCaptor = ArgumentCaptor.forClass(RefBookCriteria.class);
        ArgumentCaptor<ApplicationCriteria> appCriteriaCaptor = ArgumentCaptor.forClass(ApplicationCriteria.class);
        ArgumentCaptor<String> versionServiceFirstArgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SearchDataCriteria> versionServiceSecondArgCaptor = ArgumentCaptor.forClass(SearchDataCriteria.class);
        ArgumentCaptor<Integer> versionArgCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> updateDataFirstArgCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Row> updateDataSecondArgCaptor = ArgumentCaptor.forClass(Row.class);
        ArgumentCaptor<Integer> publishFirstArgCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Boolean> publishSecondArgCaptor = ArgumentCaptor.forClass(Boolean.class);

        verify(refBookService).search(refBookCriteriaCaptor.capture());
        verify(applicationSystemService).findAllApplications(appCriteriaCaptor.capture());
        verify(versionService).search(versionServiceFirstArgCaptor.capture(), versionServiceSecondArgCaptor.capture());
        verify(draftService).createFromVersion(versionArgCaptor.capture());
        verify(draftService).updateData(updateDataFirstArgCaptor.capture(), updateDataSecondArgCaptor.capture());
        verify(publishService).publish(publishFirstArgCaptor.capture(), any(), any(), any(), publishSecondArgCaptor.capture());

        assertThat(refBookCriteriaCaptor.getValue().getCode(), is("APP001"));
        assertThat(appCriteriaCaptor.getValue().getSize(), is(Integer.MAX_VALUE));
        assertThat(appCriteriaCaptor.getValue().getSystemCode(), Matchers.nullValue());
        assertThat(versionServiceFirstArgCaptor.getValue(), is("APP001"));
        assertThat(versionArgCaptor.getValue(), is(refBook.getId()));
        assertThat(updateDataFirstArgCaptor.getValue(), is(3));
        assertThat(updateDataSecondArgCaptor.getValue().getData().get("code"), is(application.getCode()));
        assertThat(updateDataSecondArgCaptor.getValue().getData().get("system"), is(application.getSystemCode()));
        assertThat(updateDataSecondArgCaptor.getValue().getData().get("name"), is(application.getName()));
        assertThat(updateDataSecondArgCaptor.getValue().getData().get("oauth"), is(application.getOAuth()));
        assertThat(publishFirstArgCaptor.getValue(), is(3));
        assertThat(publishSecondArgCaptor.getValue(), is(false));
    }

    @Test
    public void testSysExport() {
        RefBook refBook = new RefBook();
        refBook.setId(321);

        AppSystem appSystem = new AppSystem();
        appSystem.setCode("testApp");
        appSystem.setName("testAppName");
        appSystem.setDescription("testDescription");
        Page<AppSystem> appSystemPage = new PageImpl<>(Collections.singletonList(appSystem));

        RefBookRowValue rowValue = new RefBookRowValue();
        StringFieldValue code = new StringFieldValue("code", "testApp");
        StringFieldValue name = new StringFieldValue("name", "testAppName");
        StringFieldValue description = new StringFieldValue("description", "testDescription");
        rowValue.setFieldValues(Arrays.asList(code, name, description));

        mock(refBook, rowValue, null, appSystemPage);

        exportService.exportSystems();

        ArgumentCaptor<RefBookCriteria> refBookCriteriaCaptor = ArgumentCaptor.forClass(RefBookCriteria.class);
        ArgumentCaptor<SystemCriteria> sysCriteriaCaptor = ArgumentCaptor.forClass(SystemCriteria.class);
        ArgumentCaptor<String> versionServiceFirstArgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SearchDataCriteria> versionServiceSecondArgCaptor = ArgumentCaptor.forClass(SearchDataCriteria.class);

        verify(refBookService).search(refBookCriteriaCaptor.capture());
        verify(applicationSystemService).findAllSystems(sysCriteriaCaptor.capture());
        verify(versionService).search(versionServiceFirstArgCaptor.capture(), versionServiceSecondArgCaptor.capture());

        assertThat(refBookCriteriaCaptor.getValue().getCode(), is("SYS001"));
        assertThat(sysCriteriaCaptor.getValue().getSize(), is(Integer.MAX_VALUE));
        assertThat(versionServiceFirstArgCaptor.getValue(), is("SYS001"));
    }

    private void mock(RefBook refBook, RefBookRowValue refBookRowValue, Page<Application> appPage, Page<AppSystem> appSystemPage) {
        Page<RefBookRowValue> page = new PageImpl<>(Collections.singletonList(refBookRowValue));
        Draft draft = new Draft();
        draft.setId(3);

        doReturn(new PageImpl<>(Collections.singletonList(refBook))).when(refBookService).search(Mockito.any(RefBookCriteria.class));
        doReturn(appPage).when(applicationSystemService).findAllApplications(Mockito.any());
        doReturn(appSystemPage).when(applicationSystemService).findAllSystems(Mockito.any());
        doReturn(draft).when(draftService).createFromVersion(Mockito.anyInt());
        doReturn(page).when(versionService).search(Mockito.anyString(), Mockito.any());
        doNothing().when(draftService).updateData(Mockito.anyInt(), Mockito.any(Row.class));
        doNothing().when(publishService).publish(Mockito.anyInt(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyBoolean());
    }
}
