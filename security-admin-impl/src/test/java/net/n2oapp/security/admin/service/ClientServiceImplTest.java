package net.n2oapp.security.admin.service;


import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.impl.service.ApplicationSystemServiceImpl;
import net.n2oapp.security.admin.impl.service.ClientServiceImpl;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.*;

/**
 * Тест сервиса управления клиентами
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
public class ClientServiceImplTest {

    @Autowired
    private ClientServiceImpl service;

    @Autowired
    private ApplicationSystemServiceImpl applicationSystemService;

    @After
    public void cleanDB() {
        ClientCriteria criteria = new ClientCriteria();
        criteria.setPage(0);
        service.findAll(criteria).getContent().forEach(client -> service.delete(client.getClientId()));
    }

    @Test
    public void testUp() throws Exception {
        assertNotNull(service);
    }

    @Test
    public void testPersistAndGet() {
        Client client = service.getDefaultClient("notExists");
        assertEquals(client.getClientId(), "notExists");
        assertEquals(client.getEnabled(), false);
        assertEquals(client.getIsAuthorizationCode(), true);
        assertNotNull(client.getClientSecret());

        AppSystemForm appSystem = new AppSystemForm();
        appSystem.setCode("test");
        appSystem.setDescription("test");
        appSystem.setName("test");
        applicationSystemService.createSystem(appSystem);

        Application application = new Application();
        application.setName("test");
        application.setCode("testId");
        application.setSystemCode("test");
        applicationSystemService.createApplication(application);

        Client clientFromCreateMethod = service.persist(client());
        client = service.findByClientId(client().getClientId());
        compareClient(client, client());
        compareClient(client, clientFromCreateMethod);

        client.setClientSecret("newSecret");
        clientFromCreateMethod = service.persist(client);
        compareClient(client, clientFromCreateMethod);
        client = service.findByClientId(client().getClientId());
        compareClient(client, clientFromCreateMethod);

        client.setEnabled(false);
        service.persist(client);
        assertNull(service.findByClientId(client.getClientId()));
    }

    @Test
    public void testCRUD() {
        service.create(client());

        Client clientExample = client();
        Client clientFromDB = service.findByClientId(clientExample.getClientId());

        compareClient(clientFromDB, clientExample);
        clientExample.setClientSecret("newSecret");
        clientExample.setAccessTokenLifetime(69);
        clientExample.setRefreshTokenLifetime(88);
        clientExample.setRedirectUris("new.uri.1 new.uri.2");
        clientExample.setIsResourceOwnerPass(false);
        clientExample.setIsClientGrant(true);
        clientExample.setIsAuthorizationCode(false);
        clientExample.setLogoutUrl("newLogout");

        service.update(clientExample);
        clientFromDB = service.findByClientId(clientExample.getClientId());

        Throwable thrown = catchThrowable(() -> {
            clientExample.setClientId("newClientId");
            service.update(clientExample);
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        clientExample.setClientId(client().getClientId());


        compareClient(clientExample, clientFromDB);

        service.delete(clientFromDB.getClientId());
        String clientId = clientFromDB.getClientId();
        assertNull(service.findByClientId(clientId));

    }


    @Test
    public void testListClientDetails() {
        Client client2 = client();
        client2.setClientId("testId2");
        service.create(client());
        service.create(client2);
        ClientCriteria clientCriteria = new ClientCriteria();
        clientCriteria.setClientId("testId2");
        assertEquals(service.findAll(clientCriteria).getContent().size(), 1);

    }

    private Client client() {
        Client client = new Client();
        client.setClientId("testId");
        client.setClientSecret("testSecret");
        client.setAccessTokenLifetime(666);
        client.setRefreshTokenLifetime(667);
        client.setRedirectUris("test.uri.1 test.uri.2");
        client.setIsResourceOwnerPass(true);
        client.setIsClientGrant(false);
        client.setIsAuthorizationCode(true);
        client.setLogoutUrl("testLogout");
        client.setEnabled(true);
        return client;
    }

    private void compareClient(Client clientFirst, Client clientSecond) {
        assertEquals(clientFirst.getClientId(), clientSecond.getClientId());
        assertEquals(clientFirst.getAccessTokenLifetime(), clientSecond.getAccessTokenLifetime());
        assertEquals(clientFirst.getIsAuthorizationCode(), clientSecond.getIsAuthorizationCode());
        assertEquals(clientFirst.getIsResourceOwnerPass(), clientSecond.getIsResourceOwnerPass());
        assertEquals(clientFirst.getIsClientGrant(), clientSecond.getIsClientGrant());
        assertEquals(clientFirst.getRedirectUris(), clientSecond.getRedirectUris());
        assertEquals(clientFirst.getClientSecret(), clientSecond.getClientSecret());
        assertEquals(clientFirst.getRefreshTokenLifetime(), clientSecond.getRefreshTokenLifetime());
        assertEquals(clientFirst.getLogoutUrl(), clientSecond.getLogoutUrl());
    }

}
