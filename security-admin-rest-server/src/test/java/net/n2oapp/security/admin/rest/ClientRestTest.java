package net.n2oapp.security.admin.rest;


import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.impl.service.ApplicationSystemServiceImpl;
import net.n2oapp.security.admin.rest.api.ClientRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestClientCriteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Тест Rest сервиса управления клиентами
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
public class ClientRestTest {

    @Autowired
    @Qualifier("clientRestServiceJaxRsProxyClient")
    private ClientRestService clientService;

    @Autowired
    private ApplicationSystemServiceImpl applicationSystemService;

    @Test
    public void persistAndGet() {
        Client client = clientService.getDefaultClient("notExists");
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

        Client clientFromCreateMethod = clientService.persist(newClient());
        client = clientService.getByClientId(newClient().getClientId());
        compareClient(client, newClient());
        compareClient(client, clientFromCreateMethod);

        client.setClientSecret("newSecret");
        clientFromCreateMethod = clientService.persist(client);
        compareClient(client, clientFromCreateMethod);
        client = clientService.getByClientId(newClient().getClientId());
        compareClient(client, clientFromCreateMethod);

        client.setEnabled(false);
        clientService.persist(client);
        assertNull(clientService.getByClientId(client.getClientId()));
    }

    @Test
    public void crud() {
        String clientId = create();
        clientId = update(clientId);
        delete(clientId);
    }

    @Test
    public void findAll() {
        clientService.create(newClient());
        RestClientCriteria criteria = new RestClientCriteria();
        assertEquals(1, clientService.findAll(criteria).getTotalElements());
        clientService.delete(newClient().getClientId());
    }

    private String create() {
        Client client = clientService.create(newClient());
        compareClient(client, newClient());
        return client.getClientId();
    }

    private String update(String id) {
        Client client = clientService.getByClientId(id);

        client.setClientSecret("newSecret");
        client.setAccessTokenValidityMinutes(69);
        client.setRefreshTokenLifetime(88);
        client.setRedirectUris("new.uri.1 new.uri.2");
        client.setIsResourceOwnerPass(false);
        client.setIsClientGrant(true);
        client.setIsAuthorizationCode(false);
        client.setLogoutUrl("newLogout");

        Client clientExample = client;
        client = clientService.update(client);
        compareClient(client, clientExample);
        return client.getClientId();
    }

    private void delete(String id) {
        clientService.delete(id);
        Client client = clientService.getByClientId(id);
        assertNull(client);
    }

    private Client newClient() {
        Client client = new Client();
        client.setClientId("testId");
        client.setClientSecret("testSecret");
        client.setAccessTokenValidityMinutes(666);
        client.setRefreshTokenLifetime(667);
        client.setRedirectUris("test.uri.1 test.uri.2");
        client.setIsResourceOwnerPass(true);
        client.setIsClientGrant(false);
        client.setIsAuthorizationCode(true);
        client.setLogoutUrl("testLogout");
        List<Integer> roles = new ArrayList<>();
        roles.add(1);
        client.setRolesIds(roles);
        client.setEnabled(true);
        return client;
    }

    private void compareClient(Client clientFirst, Client clientSecond) {
        assertEquals(clientFirst.getClientId(), clientSecond.getClientId());
        assertEquals(clientFirst.getAccessTokenValidityMinutes(), clientSecond.getAccessTokenValidityMinutes());
        assertEquals(clientFirst.getIsAuthorizationCode(), clientSecond.getIsAuthorizationCode());
        assertEquals(clientFirst.getIsResourceOwnerPass(), clientSecond.getIsResourceOwnerPass());
        assertEquals(clientFirst.getIsClientGrant(), clientSecond.getIsClientGrant());
        assertEquals(clientFirst.getRedirectUris(), clientSecond.getRedirectUris());
        assertEquals(clientFirst.getClientSecret(), clientSecond.getClientSecret());
        assertEquals(clientFirst.getRefreshTokenLifetime(), clientSecond.getRefreshTokenLifetime());
        assertEquals(clientFirst.getLogoutUrl(), clientSecond.getLogoutUrl());
        assertEquals(clientFirst.getRolesIds(), clientSecond.getRolesIds());
    }
}
