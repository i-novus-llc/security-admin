package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.impl.audit.AuditHelper;
import net.n2oapp.security.admin.impl.entity.ClientEntity;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.repository.ApplicationRepository;
import net.n2oapp.security.admin.impl.repository.ClientRepository;
import net.n2oapp.security.admin.impl.service.specification.ClientSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private AuditHelper audit;

    @Value("${access.permission.enabled}")
    private Boolean permissionEnabled;

    @Override
    public Client create(Client client) {
        if (clientRepository.findByClientId(client.getClientId()).isPresent())
            throw new UserException("exception.uniqueClient");
        Client result = model(clientRepository.save(entity(client)), permissionEnabled);
        return audit("audit.clientCreate", result);
    }

    @Override
    public Client update(Client client) {
        clientNotExists(client.getClientId());
        Client result = model(clientRepository.save(entity(client)), permissionEnabled);
        return audit("audit.clientUpdate", result);
    }

    @Override
    public void delete(String clientId) {
        ClientEntity client = clientRepository.findByClientId(clientId).orElse(null);
        if (isNull(client)) throw new UserException("exception.clientNotFound");
        clientRepository.deleteById(clientId);
        audit("audit.clientDelete", model(client, permissionEnabled));
    }

    @Override
    public Client findByClientId(String clientId) {
        return model(clientRepository.findByClientId(clientId).orElse(null), permissionEnabled);
    }

    @Override
    public Page<Client> findAll(ClientCriteria criteria) {
        Specification<ClientEntity> specification = new ClientSpecifications(criteria);
        return clientRepository.findAll(specification, criteria).map(c -> ClientServiceImpl.model(c, permissionEnabled));
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
        if (clientRepository.existsById(clientForm.getClientId()))
            delete(clientForm.getClientId());
        return null;
    }

    @Override
    public Client getDefaultClient(String id) {
        Client client = new Client();
        client.setSystemCode(id);
        client.setClientId(id);
        client.setClientSecret(UUID.randomUUID().toString());
        client.setIsAuthorizationCode(true);
        client.setEnabled(false);
        return client;
    }

    public static Client model(ClientEntity clientEntity, boolean permissionEnabled) {
        if (isNull(clientEntity)) return null;
        Client client = new Client();
        client.setEnabled(true);
        client.setClientId(clientEntity.getClientId());
        client.setClientSecret(clientEntity.getClientSecret());
        if (nonNull(clientEntity.getGrantTypes())) {
            client.setIsClientGrant(clientEntity.getGrantTypes().contains("client_credentials"));
            client.setIsResourceOwnerPass(clientEntity.getGrantTypes().contains("password"));
            client.setIsAuthorizationCode(clientEntity.getGrantTypes().contains("authorization_code"));
        }
        if (nonNull(clientEntity.getRedirectUris()))
            client.setRedirectUris(clientEntity.getRedirectUris().replace(",", " "));
        if (nonNull(clientEntity.getAccessTokenValiditySeconds()))
            client.setAccessTokenValidityMinutes(clientEntity.getAccessTokenValiditySeconds() / 60);
        if (nonNull(clientEntity.getRefreshTokenValiditySeconds()))
            client.setRefreshTokenLifetime(clientEntity.getRefreshTokenValiditySeconds() / 60);
        client.setLogoutUrl(clientEntity.getLogoutUrl());
        if (nonNull(clientEntity.getRoleList())) {
            client.setRoles(clientEntity.getRoleList().stream().map(r -> ClientServiceImpl.model(r, permissionEnabled)).collect(Collectors.toList()));
            client.setRolesIds(clientEntity.getRoleList().stream().map(RoleEntity::getId).collect(Collectors.toList()));
        }
        return client;
    }

    public static ClientEntity entity(Client client) {
        if (isNull(client)) return null;
        ClientEntity entity = new ClientEntity();
        entity.setClientId(client.getClientId());
        entity.setClientSecret(client.getClientSecret());
        String[] redirectUris = StringUtils.tokenizeToStringArray(client.getRedirectUris(), " ", true, true);
        entity.setRedirectUris(StringUtils.arrayToCommaDelimitedString(redirectUris));
        if (nonNull(client.getAccessTokenValidityMinutes()))
            entity.setAccessTokenValiditySeconds(client.getAccessTokenValidityMinutes() * 60);
        if (nonNull(client.getRefreshTokenLifetime()))
            entity.setRefreshTokenValiditySeconds(client.getRefreshTokenLifetime() * 60);
        entity.setLogoutUrl(client.getLogoutUrl());
        ArrayList<String> authorizedGrantTypes = new ArrayList<>();
        if (Boolean.TRUE.equals(client.getIsClientGrant()))
            authorizedGrantTypes.add("client_credentials");
        if (Boolean.TRUE.equals(client.getIsAuthorizationCode()))
            authorizedGrantTypes.add("authorization_code");
        if (Boolean.TRUE.equals(client.getIsResourceOwnerPass()))
            authorizedGrantTypes.add("password");
        entity.setGrantTypes(StringUtils.collectionToCommaDelimitedString(authorizedGrantTypes));
        if (nonNull(client.getRolesIds()))
            entity.setRoleList(client.getRolesIds().stream().filter(roleId -> roleId > 0).map(RoleEntity::new).collect(Collectors.toList()));
        return entity;
    }

    private static Permission permissionModel(PermissionEntity entity) {
        if (isNull(entity)) return null;
        Permission model = new Permission();
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        if (nonNull(entity.getParentPermission()))
            model.setParent(permissionModel(entity.getParentPermission()));
        model.setHasChildren(entity.getHasChildren());
        return model;
    }

    private void clientNotExists(String id) {
        if (clientRepository.findByClientId(id).isEmpty())
            throw new UserException("exception.clientNotFound");
    }

    private static Role model(RoleEntity entity, boolean permissionEnabled) {
        if (isNull(entity)) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        if (permissionEnabled && nonNull(entity.getPermissionList())) {
            model.setPermissions(entity.getPermissionList().stream().map(ClientServiceImpl::permissionModel).collect(Collectors.toList()));
        }
        return model;
    }

    private Client audit(String action, Client client) {
        audit.audit(action, client, client.getClientId(), client.getClientId());
        return client;
    }
}
