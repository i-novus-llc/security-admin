package net.n2oapp.security.admin.service;


import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.impl.service.ClientServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.NoSuchElementException;
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
public class ClientServiceImplTest {

    @Autowired
    private ClientServiceImpl service;

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
        Set<String> stringSet = new HashSet<>();
        stringSet.add("new.uri.1");
        stringSet.add("new.uri.2");
        clientExample.setRegisteredRedirectUri(stringSet);
        stringSet = new HashSet<>();
        stringSet.add("newGrantTypes1");
        stringSet.add("newGrantTypes2");
        clientExample.setAuthorizedGrantTypes(stringSet);

        service.update(clientExample);
        clientFromDB = service.findById(clientExample.getClientId());

        compareClient(clientExample, clientFromDB);

        service.delete(clientFromDB.getClientId());
        String clientId = clientFromDB.getClientId();
        Throwable thrown = catchThrowable(() -> {
            service.findById(clientId);
        });
        assertThat(thrown).isInstanceOf(NoSuchElementException.class);
    }


    @Test
    public void testListClientDetails() {
        Client client2 = client();
        client2.setClientId("testId2");
        service.create(client());
        service.create(client2);
        assertEquals(service.findAll().size(), 2);

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
