package net.n2oapp.security.admin.rdm;

import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.service.SystemService;
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
import ru.i_novus.ms.rdm.api.model.draft.Draft;
import ru.i_novus.ms.rdm.api.model.draft.PublishRequest;
import ru.i_novus.ms.rdm.api.model.refbook.RefBook;
import ru.i_novus.ms.rdm.api.model.refbook.RefBookCriteria;
import ru.i_novus.ms.rdm.api.model.refdata.RefBookRowValue;
import ru.i_novus.ms.rdm.api.model.refdata.SearchDataCriteria;
import ru.i_novus.ms.rdm.api.model.refdata.UpdateDataRequest;
import ru.i_novus.ms.rdm.api.service.DraftService;
import ru.i_novus.ms.rdm.api.service.PublishService;
import ru.i_novus.ms.rdm.api.service.RefBookService;
import ru.i_novus.ms.rdm.api.service.VersionService;
import ru.i_novus.platform.datastorage.temporal.model.value.StringFieldValue;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
    private SystemService systemService;
    @Autowired
    private ApplicationSystemExportServiceImpl exportService;

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

        mock(refBook, rowValue, appSystemPage);

        exportService.exportSystems();

        ArgumentCaptor<RefBookCriteria> refBookCriteriaCaptor = ArgumentCaptor.forClass(RefBookCriteria.class);
        ArgumentCaptor<SystemCriteria> sysCriteriaCaptor = ArgumentCaptor.forClass(SystemCriteria.class);
        ArgumentCaptor<String> versionServiceFirstArgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SearchDataCriteria> versionServiceSecondArgCaptor = ArgumentCaptor.forClass(SearchDataCriteria.class);

        verify(refBookService).search(refBookCriteriaCaptor.capture());
        verify(systemService).findAllSystems(sysCriteriaCaptor.capture());
        verify(versionService).search(versionServiceFirstArgCaptor.capture(), versionServiceSecondArgCaptor.capture());

        assertThat(refBookCriteriaCaptor.getValue().getCode(), is("SYS001"));
        assertThat(sysCriteriaCaptor.getValue().getSize(), is(Integer.MAX_VALUE));
        assertThat(versionServiceFirstArgCaptor.getValue(), is("SYS001"));
    }

    private void mock(RefBook refBook, RefBookRowValue refBookRowValue, Page<AppSystem> appSystemPage) {
        Page<RefBookRowValue> page = new PageImpl<>(Collections.singletonList(refBookRowValue));
        Draft draft = new Draft();
        draft.setId(3);
        draft.setOptLockValue(2);

        doReturn(new PageImpl<>(Collections.singletonList(refBook))).when(refBookService).search(Mockito.any(RefBookCriteria.class));
        doReturn(appSystemPage).when(systemService).findAllSystems(Mockito.any());
        doReturn(draft).when(draftService).createFromVersion(Mockito.anyInt());
        doReturn(page).when(versionService).search(Mockito.anyString(), Mockito.any());
        doNothing().when(draftService).updateData(Mockito.anyInt(), Mockito.any(UpdateDataRequest.class));
        doNothing().when(publishService).publish(Mockito.anyInt(), Mockito.any(PublishRequest.class));
    }
}
