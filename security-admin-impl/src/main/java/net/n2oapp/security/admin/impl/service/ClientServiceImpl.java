package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.impl.entity.ClientEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.repository.ApplicationRepository;
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

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ApplicationRepository applicationRepository;


    @Override
    public Client create(Client client) {
        if (clientRepository.findByClientId(client.getClientId()).isPresent())
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
    public Client findByClientId(String clientId) {
        return model(clientRepository.findByClientId(clientId).orElse(null));
    }

    @Override
    public Page<Client> findAll(ClientCriteria criteria) {
        Specification<ClientEntity> specification = new ClientSpecifications(criteria);
        return clientRepository.findAll(specification, criteria).map(this::model);
    }


    private Client model(ClientEntity clientEntity) {
        if (clientEntity == null) return null;
        Client client = new Client();
        client.setEnabled(true);
        client.setClientId(clientEntity.getClientId());
        client.setClientSecret(clientEntity.getClientSecret());
        if (clientEntity.getGrantTypes() != null) {
            client.setIsClientGrant(clientEntity.getGrantTypes().contains("client_credentials"));
            client.setIsResourceOwnerPass(clientEntity.getGrantTypes().contains("password"));
            client.setIsAuthorizationCode(clientEntity.getGrantTypes().contains("authorization_code"));
        }
        if (clientEntity.getRedirectUris() != null)
            client.setRedirectUris(clientEntity.getRedirectUris().replace(",", " "));
        if (clientEntity.getAccessTokenLifetime() != null)
            client.setAccessTokenLifetime(clientEntity.getAccessTokenLifetime() / 60);
        if (clientEntity.getRefreshTokenLifetime() != null)
            client.setRefreshTokenLifetime(clientEntity.getRefreshTokenLifetime() / 60);
        client.setLogoutUrl(clientEntity.getLogoutUrl());
        if (clientEntity.getRoleList() != null) {
            client.setRoles(clientEntity.getRoleList().stream().map(this::model).collect(Collectors.toList()));
            client.setRolesIds(clientEntity.getRoleList().stream().map(RoleEntity::getId).collect(Collectors.toList()));
        }
        return client;

    }

    private ClientEntity entity(Client client) {
        if (client == null) return null;
        ClientEntity entity = new ClientEntity();
        entity.setClientId(client.getClientId());
        entity.setClientSecret(client.getClientSecret());
        String[] redirectUris = StringUtils.tokenizeToStringArray(client.getRedirectUris(), " ", true, true);
        entity.setRedirectUris(StringUtils.arrayToCommaDelimitedString(redirectUris));
        entity.setAccessTokenLifetime(client.getAccessTokenLifetime() * 60);
        entity.setRefreshTokenLifetime(client.getRefreshTokenLifetime() * 60);
        entity.setLogoutUrl(client.getLogoutUrl());
        ArrayList<String> authorizedGrantTypes = new ArrayList<>();
        if (client.getIsClientGrant() == Boolean.TRUE)
            authorizedGrantTypes.add("client_credentials");
        if (client.getIsAuthorizationCode() == Boolean.TRUE)
            authorizedGrantTypes.add("authorization_code");
        if (client.getIsResourceOwnerPass() == Boolean.TRUE)
            authorizedGrantTypes.add("password");
        entity.setGrantTypes(StringUtils.collectionToCommaDelimitedString(authorizedGrantTypes));
        if (client.getRolesIds() != null)
            entity.setRoleList(client.getRolesIds().stream().map(RoleEntity::new).collect(Collectors.toList()));
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
        if (clientRepository.findByClientId(id).isEmpty())
            throw new UserException("exception.clientNotFound");
    }

    private Role model(RoleEntity entity) {
        if (entity == null) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        return model;
    }

    @Override
    public Client persist(Client clientForm) {
        if (clientForm.getEnabled()) {
            if (clientRepository.existsById(clientForm.getClientId())) {
                return update(clientForm);
            }
            if (applicationRepository.existsById(clientForm.getClientId())) {
                return create(clientForm);
            } else throw new UserException("exception.applicationNotFound");
        }
        delete(clientForm.getClientId());
        return null;
    }

    @Override
    public Client getOrCreate(String id) {
        Client client = findByClientId(id);
        if (client == null) {
            client = new Client();
            client.setClientId(id);
            client.setClientSecret(UUID.randomUUID().toString());
            client.setIsAuthorizationCode(true);
            client.setEnabled(false);
            client.setAccessTokenLifetime(1440);
            client.setRefreshTokenLifetime(43200);
            return client;
        }

        client.setEnabled(true);
        return client;
    }

}
