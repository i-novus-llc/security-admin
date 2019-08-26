package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.rest.api.ClientRestService;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления клиентами, для вызова rest
 */
public class ClientServiceRestClient implements ClientService {

    private ClientRestService clientService;

    public ClientServiceRestClient(ClientRestService clientService) {
        this.clientService = clientService;
    }

    @Override
    public Client create(Client client) {
        return clientService.create(client);
    }

    @Override
    public Client update(Client client) {
        return clientService.update(client);
    }

    @Override
    public void delete(String clientId) {
        clientService.delete(clientId);
    }

    @Override
    public Client findById(String clientId) {
        return clientService.getById(clientId);
    }

    @Override
    public Page<Client> findAll() {
        return clientService.findAll();
    }

    @Override
    public boolean existsById(String clientId) {
        return clientService.getById(clientId) != null;
    }
}
