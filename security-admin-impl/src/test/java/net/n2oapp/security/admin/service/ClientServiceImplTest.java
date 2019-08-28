package net.n2oapp.security.admin.service;


import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.impl.service.ClientServiceImpl;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

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
    public void testCRUD() {
        service.create(client());

        Client clientExample = client();
        Client clientFromDB = service.findById(clientExample.getClientId());

        compareClient(clientFromDB, clientExample);
        clientExample.setClientSecret("newSecret");
        clientExample.setAccessTokenValiditySeconds(69);
        clientExample.setRefreshTokenValiditySeconds(88);
        Set<String> stringSet = new HashSet<>();
        stringSet.add("new.uri.1");
        stringSet.add("new.uri.2");
        clientExample.setRegisteredRedirectUri(stringSet);
        stringSet = new HashSet<>();
        stringSet.add("newGrantTypes1");
        stringSet.add("newGrantTypes2");
        clientExample.setAuthorizedGrantTypes(stringSet);
        clientExample.setLogoutUrl("newLogout");

        service.update(clientExample);
        clientFromDB = service.findById(clientExample.getClientId());

        Throwable thrown = catchThrowable(() -> {
            clientExample.setClientId("newClientId");
            service.update(clientExample);
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        clientExample.setClientId(client().getClientId());


        compareClient(clientExample, clientFromDB);

        service.delete(clientFromDB.getClientId());
        String clientId = clientFromDB.getClientId();
        assertNull(service.findById(clientId));

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
        client.setAccessTokenValiditySeconds(666);
        client.setRefreshTokenValiditySeconds(667);
        Set<String> stringSet = new HashSet<>();
        stringSet.add("test.uri.1");
        stringSet.add("test.uri.2");
        client.setRegisteredRedirectUri(stringSet);
        stringSet = new HashSet<>();
        stringSet.add("testGrantTypes1");
        stringSet.add("testGrantTypes2");
        client.setAuthorizedGrantTypes(stringSet);
        client.setLogoutUrl("testLogout");
        return client;
    }

    private void compareClient(Client clientFirst, Client clientSecond) {
        assertEquals(clientFirst.getClientId(), clientSecond.getClientId());
        assertEquals(clientFirst.getAccessTokenValiditySeconds(), clientSecond.getAccessTokenValiditySeconds());
        assertEquals(clientFirst.getAuthorizedGrantTypes(), clientSecond.getAuthorizedGrantTypes());
        assertEquals(clientFirst.getRegisteredRedirectUri(), clientSecond.getRegisteredRedirectUri());
        assertEquals(clientFirst.getClientSecret(), clientSecond.getClientSecret());
        assertEquals(clientFirst.getRefreshTokenValiditySeconds(), clientSecond.getRefreshTokenValiditySeconds());
        assertEquals(clientFirst.getLogoutUrl(), clientSecond.getLogoutUrl());
    }

}
