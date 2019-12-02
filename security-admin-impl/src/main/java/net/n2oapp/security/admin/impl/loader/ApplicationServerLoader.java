package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.security.admin.impl.entity.ApplicationEntity;
import net.n2oapp.security.admin.impl.entity.ClientEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.loader.model.AppModel;
import net.n2oapp.security.admin.impl.repository.ApplicationRepository;
import net.n2oapp.security.admin.impl.repository.ClientRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Загружает приложения и их клиентов
 */
@Component
public class ApplicationServerLoader implements ServerLoader<AppModel> {

    private ClientRepository clientRepository;
    private ApplicationRepository appRepository;

    public ApplicationServerLoader(ClientRepository clientRepository, ApplicationRepository appRepository) {
        this.clientRepository = clientRepository;
        this.appRepository = appRepository;
    }

    @Override
    public void load(List<AppModel> data, String subject) {
        List<ClientEntity> clients = new ArrayList<>();
        List<ApplicationEntity> apps = new ArrayList<>();

        data.forEach(m -> {
            ApplicationEntity app = new ApplicationEntity();
            app.setCode(m.getCode());
            app.setName(m.getName());
            app.setSystemCode(new SystemEntity(subject));
            apps.add(app);
            if (m.getClientId() != null) {
                ClientEntity client = new ClientEntity();
                client.setClientId(m.getClientId());
                client.setClientSecret(m.getClientSecret());
                client.setAccessTokenLifetime(m.getAccessTokenLifetime());
                client.setRefreshTokenLifetime(m.getRefreshTokenLifetime());
                client.setGrantTypes(m.getGrantTypes());
                client.setRedirectUris(m.getRedirectUris());
                client.setLogoutUrl(m.getLogoutUrl());
                clients.add(client);
            }
        });
        appRepository.saveAll(apps);
        clientRepository.saveAll(clients);

        Set<String> fresh = data.stream().map(AppModel::getCode).collect(Collectors.toSet());
        List<ApplicationEntity> old = appRepository.findBySystemCode(new SystemEntity(subject));
        List<ClientEntity> unusedClients = new ArrayList<>();
        List<ApplicationEntity> unusedApps = new ArrayList<>();
        for (ApplicationEntity entity : old) {
            if (!fresh.contains(entity.getCode())) {
                unusedApps.add(entity);
                ClientEntity client = new ClientEntity();
                client.setClientId(entity.getCode());
                unusedClients.add(client);
            }
        }
        appRepository.deleteAll(unusedApps);
        clientRepository.deleteAll(unusedClients);
    }

    @Override
    public String getTarget() {
        return "applications";
    }

    @Override
    public Class<AppModel> getDataType() {
        return AppModel.class;
    }
}
