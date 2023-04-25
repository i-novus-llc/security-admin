package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.service.UserLevelService;
import net.n2oapp.security.admin.rest.client.feign.UserLevelServiceFeignClient;

import java.util.List;

/**
 * Прокси реализация сервиса управления уровнями пользователя, для вызова rest
 */
public class UserLevelServiceRestClient implements UserLevelService {

    private UserLevelServiceFeignClient client;

    public UserLevelServiceRestClient(UserLevelServiceFeignClient client) {
        this.client = client;
    }

    @Override
    public List<UserLevel> getAll() {
        return client.getAll().getContent();
    }

    @Override
    public List<UserLevel> getAllForFilter(String name) {
        return client.getAllForFilter(name).getContent();
    }
}
