package net.n2oapp.security.admin.rest;

import net.n2oapp.platform.jaxrs.RestException;
import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.rest.api.SystemRestService;
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
    @Qualifier("systemRestServiceJaxRsProxyClient")
    private SystemRestService client;

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
