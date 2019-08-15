package net.n2oapp.security.admin.service;

import net.n2oapp.security.admin.model.Client;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Тест сервиса управления клиентами
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
public class JpaClientDetailsServiceTest {

    @Autowired
    JpaClientDetailsService service;

    @After
    public void cleanDB() {
        List<ClientDetails> clientDetails = service.listClientDetails();
        clientDetails.forEach(clientDetail -> service.removeClientDetails(clientDetail.getClientId()));
    }

    @Test
    public void testUp() throws Exception {
        assertNotNull(service);
    }

    @Test
    public void testUpdateClientSecret() {
        Client client = client();
        service.addClientDetails(client());
        client.setClientSecret("new Secret");
        service.updateClientSecret(client.getClientId(), client.getClientSecret());
        Client clientFromDB = (Client) service.loadClientByClientId(client.getClientId());
        compareClient(client, clientFromDB);

        Throwable thrown = catchThrowable(() -> {
            service.updateClientSecret("NOT exists", "Secret");
        });
        assertThat(thrown).isInstanceOf(NoSuchClientException.class);
        assertEquals("Client with id: NOT exists does not exists", thrown.getMessage());

    }

    @Test
    public void testCRUD() {
        service.addClientDetails(client());

        Throwable thrown = catchThrowable(() -> {
            service.addClientDetails(client());
        });
        assertThat(thrown).isInstanceOf(ClientAlreadyExistsException.class);
        assertEquals("Client with id: testId already exists", thrown.getMessage());

        thrown = catchThrowable(() -> {
            service.loadClientByClientId("NOT exists");
        });
        assertThat(thrown).isInstanceOf(NoSuchClientException.class);
        assertEquals("Client with id: NOT exists does not exists", thrown.getMessage());

        thrown = catchThrowable(() -> {
            Client client = new Client();
            client.setClientId("NOT exists");
            service.updateClientDetails(client);
        });
        assertThat(thrown).isInstanceOf(NoSuchClientException.class);
        assertEquals("Client with id: NOT exists does not exists", thrown.getMessage());


        Client clientFromDB = (Client) service.loadClientByClientId(client().getClientId());
        Client clientExample = client();

        compareClient(clientExample, clientFromDB);

        clientExample.setClientSecret("newSecret");
        clientExample.setAccessTokenValiditySeconds(69);
        Set<String> stringSet = new HashSet<>();
        stringSet.add("new.uri.1");
        stringSet.add("new.uri.2");
        clientExample.setRegisteredRedirectUri(stringSet);
        stringSet = new HashSet<>();
        stringSet.add("newGrantTypes1");
        stringSet.add("newGrantTypes2");
        clientExample.setAuthorizedGrantTypes(stringSet);

        service.updateClientDetails(clientExample);
        clientFromDB = (Client) service.loadClientByClientId(clientExample.getClientId());

        compareClient(clientExample, clientFromDB);

        service.removeClientDetails(clientFromDB.getClientId());
        String clientId = clientFromDB.getClientId();
        thrown = catchThrowable(() -> {
            service.loadClientByClientId(clientId);
        });
        assertThat(thrown).isInstanceOf(NoSuchClientException.class);
        assertEquals("Client with id: " + clientId + " does not exists", thrown.getMessage());
    }

    @Test
    public void testlistClientDetails() {
        Client client2 = client();
        client2.setClientId("testId2");
        service.addClientDetails(client());
        service.addClientDetails(client2);
        assertEquals(service.listClientDetails().size(), 2);

    }

    private Client client() {
        Client client = new Client();
        client.setClientId("testId");
        client.setClientSecret("testSecret");
        client.setAccessTokenValiditySeconds(666);
        Set<String> stringSet = new HashSet<>();
        stringSet.add("test.uri.1");
        stringSet.add("test.uri.2");
        client.setRegisteredRedirectUri(stringSet);
        stringSet = new HashSet<>();
        stringSet.add("testGrantTypes1");
        stringSet.add("testGrantTypes2");
        client.setAuthorizedGrantTypes(stringSet);
        return client;
    }

    private void compareClient(Client clientFirst, Client clientSecond) {
        assertEquals(clientFirst.getClientId(), clientSecond.getClientId());
        assertEquals(clientFirst.getAccessTokenValiditySeconds(), clientSecond.getAccessTokenValiditySeconds());
        assertEquals(clientFirst.getAuthorizedGrantTypes(), clientSecond.getAuthorizedGrantTypes());
        assertEquals(clientFirst.getRegisteredRedirectUri(), clientSecond.getRegisteredRedirectUri());
        assertEquals(clientFirst.getClientSecret(), clientSecond.getClientSecret());
    }

}
