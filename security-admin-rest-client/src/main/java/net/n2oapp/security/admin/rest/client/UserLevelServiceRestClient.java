package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.service.UserLevelService;
import net.n2oapp.security.admin.rest.api.UserLevelRestService;

import java.util.List;

/**
 * Прокси реализация сервиса управления уровнями пользователя, для вызова rest
 */
public class UserLevelServiceRestClient implements UserLevelService {

    private UserLevelRestService client;

    public UserLevelServiceRestClient(UserLevelRestService client) {
        this.client = client;
    }

    @Override
    public List<UserLevel> getAll() {
        return client.getAll().getContent();
    }

    @Override
    public List<UserLevel> getAllForFilter() {
        return client.getAllForFilter().getContent();
    }
}
