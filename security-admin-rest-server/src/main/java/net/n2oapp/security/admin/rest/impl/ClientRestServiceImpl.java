package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.rest.api.ClientRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

/**
 * Реализация REST сервиса управления клиентами
 */
@Controller
public class ClientRestServiceImpl implements ClientRestService {

    @Autowired
    private ClientService service;

    @Override
    public Page<Client> findAll() {
        return service.findAll();
    }

    @Override
    public Client getById(String clientId) {
        return service.findById(clientId);
    }

    @Override
    public Client create(Client clientForm) {
        return service.create(clientForm);
    }

    @Override
    public Client update(Client clientForm) {
        return service.update(clientForm);
    }

    @Override
    public void delete(String clientId) {
        service.delete(clientId);
    }
}
