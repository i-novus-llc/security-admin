package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.impl.entity.ClientEntity;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
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
import java.util.stream.Collectors;

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
        if (clientEntity.getRoles() != null) {
            client.setRoles(clientEntity.getRoles().stream().map(this::roleModel).collect(Collectors.toList()));
        }
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

    private Role roleModel(RoleEntity entity) {
        if (entity == null) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        if (entity.getPermissionList() != null) {
            model.setPermissions(entity.getPermissionList().stream().map(this::permissionModel).collect(Collectors.toList()));

        }
        return model;
    }

    private Permission permissionModel(PermissionEntity entity) {
        if (entity == null) return null;
        Permission model = new Permission();
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setParentCode(entity.getParentCode());
        model.setHasChildren(entity.getHasChildren());
        return model;
    }


    private void clientNotExists(String id) {
        if (clientRepository.findById(id).isEmpty())
            throw new UserException("exception.clientNotFound");
    }

}
