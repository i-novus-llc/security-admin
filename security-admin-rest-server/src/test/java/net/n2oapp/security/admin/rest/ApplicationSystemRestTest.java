package net.n2oapp.security.admin.rest;

import net.n2oapp.platform.jaxrs.RestException;
import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.rest.api.ApplicationSystemRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestApplicationCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestSystemCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Тест Rest сервиса управления системами и приложениями
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
public class ApplicationSystemRestTest {
    @Autowired
    @Qualifier("applicationSystemRestServiceJaxRsProxyClient")
    private ApplicationSystemRestService client;

    @Test
    public void findAllSystems() {
        RestSystemCriteria criteria = new RestSystemCriteria();
        List<AppSystem> systems = client.findAllSystems(criteria).getContent();
        assertThat(systems.size(), is(2));
        assertThat(systems.get(0).getCode(), is("testSystemCode1"));
        assertThat(systems.get(0).getName(), is("testSystemName1"));
        assertThat(systems.get(0).getDescription(), is("testSystemDesc1"));
    }


    @Test
    public void findAllApplications() {
        RestApplicationCriteria criteria = new RestApplicationCriteria();
        List<Application> apps = client.findAllApplications(criteria).getContent();
        assertThat(apps.size(), is(2));

        assertThat(apps.get(0).getCode(), is("testApplicationCode1"));
        assertThat(apps.get(0).getName(), is("testApplicationName1"));
        assertThat(apps.get(0).getSystemCode(), is("testSystemCode1"));
        assertThat(apps.get(0).getSystemName(), is("testSystemName1"));
    }

    @Test
    public void testCreateApplication() {
        Application application = new Application();
        application.setCode("testApplicationCode3");
        application.setName("testApplicationName3");
        application.setSystemCode("testSystemCode1");

        Application result = client.createApplication(application);
        assertThat(result.getCode(), is(application.getCode()));
        assertThat(result.getName(), is(application.getName()));
        assertThat(result.getSystemCode(), is(application.getSystemCode()));
        assertThat(result.getSystemName(), is("testSystemName1"));
        assertThat(result.getOAuth(), is(false));
        client.deleteApplication(result.getCode());
    }

    @Test
    public void testUpdateApplication() {
        Application application = new Application();
        application.setCode("testApplicationUpdateCode1");
        application.setName("testApplicationUpdateName1");
        application.setSystemCode("testSystemCode1");

        Application created = client.createApplication(application);
        assertThat(created.getCode(), is(application.getCode()));

        created.setName("testApplicationUpdateName2");
        created.setSystemCode("testSystemCode2");

        Application updated = client.updateApplication(created);
        assertThat(updated.getName(), is(created.getName()));
        assertThat(updated.getSystemCode(), is(created.getSystemCode()));
        assertThat(updated.getSystemName(), is("testSystemName2"));
        client.deleteApplication(updated.getCode());
    }

    @Test
    public void testDeleteApplication() {
        Application application = new Application();
        application.setCode("testApplicationDeleteCode1");
        application.setName("testApplicationDeleteName1");
        application.setSystemCode("testSystemCode1");

        Application created = client.createApplication(application);
        assertThat(created != null, is(true));

        client.deleteApplication(created.getCode());

        try {
            client.deleteApplication(created.getCode());
            assert false;
        } catch (RestException ex) {
            assertThat(ex.getMessage(), is("Приложение с таким идентификатором не существует"));
        }
    }

    @Test
    public void testCreateSystem() {
        AppSystem system = new AppSystem();
        system.setCode("testSystemCode3");
        system.setName("testSystemName3");
        system.setDescription("testSystemDescription3");
        system.setIcon("testSystemIcon3");
        system.setPublicAccess(false);
        system.setShowOnInterface(true);
        system.setUrl("testSystemUrl1");
        system.setViewOrder(10);

        AppSystem result = client.createSystem(system);
        assertThat(result.getCode(), is(system.getCode()));
        assertThat(result.getName(), is(system.getName()));
        assertThat(result.getDescription(), is(system.getDescription()));
        assertThat(result.getIcon(), is(system.getIcon()));
        assertThat(result.getPublicAccess(), is(system.getPublicAccess()));
        assertThat(result.getShowOnInterface(), is(system.getShowOnInterface()));
        assertThat(result.getUrl(), is(system.getUrl()));
        assertThat(result.getViewOrder(), is(system.getViewOrder()));
        client.deleteSystem(result.getCode());
    }

    @Test
    public void testDeleteSystem() {
        AppSystem system = new AppSystem();
        system.setCode("testSystemCode3");
        system.setName("testSystemName3");
        system.setDescription("testSystemDescription3");
        system.setIcon("testSystemIcon3");
        system.setPublicAccess(false);
        system.setShowOnInterface(true);
        system.setUrl("testSystemUrl3");
        system.setViewOrder(10);

        AppSystem result = client.createSystem(system);
        assertThat(result != null, is(true));

        client.deleteSystem(result.getCode());

        try {
            client.deleteSystem(result.getCode());
            assert false;
        } catch (RestException ex) {
            assert true;
        }
    }

    @Test
    public void testUpdateSystem() {
        AppSystem system = new AppSystem();
        system.setCode("testSystemCodeUpdate1");
        system.setName("testSystemNameUpdate1");
        system.setDescription("testSystemDescriptionUpdate1");
        system.setIcon("testSystemIconUpdate1");
        system.setPublicAccess(false);
        system.setShowOnInterface(true);
        system.setUrl("testSystemUrlUpdate1");
        system.setViewOrder(10);

        AppSystem created = client.createSystem(system);

        created.setName("testSystemNameUpdate2");
        created.setDescription("testSystemDescriptionUpdate2");
        created.setIcon("testSystemIconUpdate2");
        created.setPublicAccess(true);
        created.setShowOnInterface(true);
        created.setUrl("testSystemUrlUpdate2");

        AppSystem updated = client.updateSystem(created);
        assertThat(updated.getCode(), is(created.getCode()));
        assertThat(updated.getName(), is(created.getName()));
        assertThat(updated.getDescription(), is(created.getDescription()));
        assertThat(updated.getIcon(), is(created.getIcon()));
        assertThat(updated.getPublicAccess(), is(created.getPublicAccess()));
        assertThat(updated.getShowOnInterface(), is(created.getShowOnInterface()));
        assertThat(updated.getUrl(), is(created.getUrl()));
        client.deleteSystem(updated.getCode());
    }
}
