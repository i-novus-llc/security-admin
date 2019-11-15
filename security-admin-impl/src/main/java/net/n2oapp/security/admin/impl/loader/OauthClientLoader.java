package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.impl.entity.ClientEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

/**
 * Загрузчик Oauth2 клиентов
 */
@Component
public class OauthClientLoader extends RepositoryServerLoader<Client, ClientEntity, String> {

    public OauthClientLoader(CrudRepository<ClientEntity, String> repository) {
        super(repository, (model, subject) -> {
            if (model == null) return null;
            ClientEntity client = new ClientEntity();
            client.setClientId(model.getClientId());
            client.setClientSecret(model.getClientSecret());
            client.setAccessTokenLifetime(model.getAccessTokenLifetime());
            client.setRefreshTokenLifetime(model.getRefreshTokenLifetime());
            client.setRedirectUris(model.getRedirectUris());
            client.setLogoutUrl(model.getLogoutUrl());

            ArrayList<String> authorizedGrantTypes = new ArrayList<>();
            if (Boolean.TRUE.equals(model.getIsClientGrant()))
                authorizedGrantTypes.add("client_credentials");
            if (Boolean.TRUE.equals(model.getIsAuthorizationCode()))
                authorizedGrantTypes.add("authorization_code");
            if (Boolean.TRUE.equals(model.getIsResourceOwnerPass()))
                authorizedGrantTypes.add("password");
            client.setGrantTypes(StringUtils.collectionToCommaDelimitedString(authorizedGrantTypes));

            return client;
        });
    }

    @Override
    public String getTarget() {
        return "clients";
    }

    @Override
    public Class<Client> getDataType() {
        return Client.class;
    }
}
