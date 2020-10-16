package net.n2oapp.security.admin.rest;

import net.n2oapp.platform.jaxrs.RestException;
import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.rest.api.ApplicationSystemRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestApplicationCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestSystemCriteria;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Тест Rest сервиса управления системами и приложениями
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationSystemRestTest {
    @Autowired
    @Qualifier("applicationSystemRestServiceJaxRsProxyClient")
    private ApplicationSystemRestService client;

    @Test
    @Order(1)
    public void findAllSystems() {
        RestSystemCriteria criteria = new RestSystemCriteria();
        List<AppSystem> systems = client.findAllSystems(criteria).getContent();
        assertThat(systems.size(), is(2));
        assertThat(systems.get(0).getCode(), is("testSystemCode1"));
        assertThat(systems.get(0).getName(), is("testSystemName1"));
        assertThat(systems.get(0).getDescription(), is("testSystemDesc1"));
    }


    @Test
    @Order(2)
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
    @Order(3)
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

        Client clientApp = new Client();
        clientApp.setClientId("123");
        clientApp.setEnabled(true);

        application = new Application();
        application.setCode("testApplicationCode4");
        application.setName("testApplicationName4");
        application.setSystemCode("testSystemCode2");
        application.setClient(clientApp);
        application.setOAuth(true);

        result = client.createApplication(application);
        assertThat(result.getCode(), is(application.getCode()));
        assertThat(result.getName(), is(application.getName()));
        assertThat(result.getSystemCode(), is(application.getSystemCode()));
        assertThat(result.getSystemName(), is("testSystemName2"));
        assertThat(result.getClient().getClientId(), is("testApplicationCode4"));
        assertThat(result.getOAuth(), is(true));

        client.deleteApplication(result.getCode());
    }

    @Test
    @Order(4)
    public void testUpdateApplication() {
        Application application = new Application();
        application.setCode("testApplicationUpdateCode1");
        application.setName("testApplicationUpdateName1");
        application.setSystemCode("testSystemCode1");

        Application created = client.createApplication(application);
        assertThat(created.getCode(), is(application.getCode()));

        created.setName("testApplicationUpdateName2");
        created.setSystemCode("testSystemCode2");
        created.setClient(new Client());

        Application updated = client.updateApplication(created);
        assertThat(updated.getName(), is(created.getName()));
        assertThat(updated.getSystemCode(), is(created.getSystemCode()));
        assertThat(updated.getSystemName(), is("testSystemName2"));
        client.deleteApplication(updated.getCode());
    }

    @Test
    @Order(5)
    public void testDeleteApplication() {
        Application application = new Application();
        application.setCode("testApplicationDeleteCode1");
        application.setName("testApplicationDeleteName1");
        application.setSystemCode("testSystemCode1");

        Application created = client.createApplication(application);
        assertThat(created != null, is(true));

        assertThat(client.getApplication(application.getCode()), notNullValue());

        client.deleteApplication(created.getCode());

        try {
            client.deleteApplication(created.getCode());
            assert false;
        } catch (RestException ex) {
            assertThat(ex.getMessage(), is("Приложение с таким идентификатором не существует"));
        }
    }

    @Test
    @Order(6)
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

        Throwable thrown = catchThrowable(() -> client.createSystem(system));
        assertThat(thrown.getMessage(), equalTo("Система с указанным кодом уже существует"));

        client.deleteSystem(result.getCode());
    }

    @Test
    @Order(7)
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

        assertThat(client.getSystem(system.getCode()), is(notNullValue()));

        client.deleteSystem(result.getCode());
        Throwable thrown = catchThrowable(() -> client.getSystem(system.getCode()));
        assertThat(thrown.getMessage(), equalTo("HTTP 404 Not Found"));

        try {
            client.deleteSystem(result.getCode());
            assert false;
        } catch (RestException ex) {
            assert true;
        }
    }

    @Test
    @Order(8)
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
