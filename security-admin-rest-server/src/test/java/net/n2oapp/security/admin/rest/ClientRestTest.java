package net.n2oapp.security.admin.rest;


import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.rest.api.ClientRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

    @Test
    public void crud() {
        String clientId = create();
        clientId = update(clientId);
        delete(clientId);
    }

    @Test
    public void findAll(){
        clientService.create(newClient());
        assertEquals(1,clientService.findAll().getTotalElements());
        clientService.delete(newClient().getClientId());
    }

    private String create() {
        Client client = clientService.create(newClient());
        compareClient(client,newClient());
        return client.getClientId();
    }

    private String update(String id) {
        Client client = clientService.getById(id);

        client.setClientId("newClientId");
        client.setClientSecret("newSecret");
        client.setAccessTokenValiditySeconds(69);
        client.setRefreshTokenValiditySeconds(88);
        Set<String> stringSet = new HashSet<>();
        stringSet.add("new.uri.1");
        stringSet.add("new.uri.2");
        client.setRegisteredRedirectUri(stringSet);
        stringSet = new HashSet<>();
        stringSet.add("newGrantTypes1");
        stringSet.add("newGrantTypes2");
        client.setAuthorizedGrantTypes(stringSet);
        client.setLogoutUrl("newLogout");

        Client clientExample = client;
        client = clientService.update(client);
        compareClient(client,clientExample);
        return client.getClientId();
    }

    private void delete(String id) {
        clientService.delete(id);
        Client client = clientService.getById(id);
        assertNull(client);
    }

    private Client newClient(){
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
