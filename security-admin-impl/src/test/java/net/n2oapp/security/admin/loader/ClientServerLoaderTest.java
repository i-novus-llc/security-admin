package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.impl.entity.ClientEntity;
import net.n2oapp.security.admin.impl.repository.ClientRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
public class ClientServerLoaderTest {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RepositoryServerLoader<Client, ClientEntity, String> clientLoader;

    @Test
    public void test() {
        Client client1 = new Client();
        client1.setClientId("testClientId");
        client1.setClientSecret("testClientSecret");
        List<Client> clients = new ArrayList<>();
        clients.add(client1);

        int n = clientRepository.findAll().size();

        clientLoader.load(clients, "clients");

        assertThat(clientRepository.findAll().size(), is(n + 1));
        assertThat(clientRepository.findByClientId("testClientId"), CoreMatchers.notNullValue());

        Client client2 = new Client();
        client2.setClientId("testClientId2");
        client2.setClientSecret("testClientSecret2");
        clients.add(client2);

        clientLoader.load(clients, "clients");

        assertThat(clientRepository.findAll().size(), is(n + 2));
        assertThat(clientRepository.findByClientId("testClientId2"), CoreMatchers.notNullValue());
    }

}
