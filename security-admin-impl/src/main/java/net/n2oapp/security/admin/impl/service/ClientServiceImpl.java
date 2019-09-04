package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.impl.entity.ClientEntity;
import net.n2oapp.security.admin.impl.repository.ClientRepository;
import net.n2oapp.security.admin.impl.service.specification.ClientSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;


    @Override
    public Client create(Client client) {
        if (clientRepository.findById(client.getClientId()).isPresent())
            throw new UserException("exception.uniqueClient");
        return model(clientRepository.save(entity(client)));
    }

    @Override
    public Client update(Client client) {
        clientNotExists(client.getClientId());
        return model(clientRepository.save(entity(client)));
    }

    @Override
    public void delete(String clientId) {
        clientNotExists(clientId);
        clientRepository.deleteById(clientId);
    }

    @Override
    public Client findById(String clientId) {
        return model(clientRepository.findById(clientId).orElse(null));
    }

    @Override
    public Page<Client> findAll(ClientCriteria criteria) {
        Specification<ClientEntity> specification = new ClientSpecifications(criteria);
        return clientRepository.findAll(specification, criteria).map(this::model);
    }

    private Set<String> stringToSet(String string) {
        return new HashSet<>(Arrays.asList(StringUtils.tokenizeToStringArray(string, ",")));
    }

    private Client model(ClientEntity clientEntity) {
        if (clientEntity == null) return null;
        Client client = new Client();
        client.setClientId(clientEntity.getClientId());
        client.setClientSecret(clientEntity.getClientSecret());
        client.setGrantTypes(stringToSet(clientEntity.getGrantTypes()));
        client.setRedirectUris(stringToSet(clientEntity.getRedirectUris()));
        client.setAccessTokenLifetime(clientEntity.getAccessTokenLifetime());
        client.setRefreshTokenLifetime(clientEntity.getRefreshTokenLifetime());
        client.setLogoutUrl(clientEntity.getLogoutUrl());

        return client;

    }

    private ClientEntity entity(Client client) {
        if (client == null) return null;
        ClientEntity entity = new ClientEntity();
        entity.setClientId(client.getClientId());
        entity.setClientSecret(client.getClientSecret());
        entity.setGrantTypes(StringUtils.collectionToCommaDelimitedString(client.getGrantTypes()));
        entity.setRedirectUris(StringUtils.collectionToCommaDelimitedString(client.getRedirectUris()));
        entity.setAccessTokenLifetime(client.getAccessTokenLifetime());
        entity.setRefreshTokenLifetime(client.getRefreshTokenLifetime());
        entity.setLogoutUrl(client.getLogoutUrl());

        return entity;
    }

    private void clientNotExists(String id) {
        if (clientRepository.findById(id).isEmpty())
            throw new UserException("exception.clientNotFound");
    }

}
