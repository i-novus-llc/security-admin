package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserDetailsToken;
import net.n2oapp.security.admin.rest.client.feign.UserDetailsServiceFeignClient;

/**
 * Прокси реализация сервиса получения детальной информации о пользователе, для вызова rest
 */
public class UserDetailsServiceRestClient implements UserDetailsService {

    private UserDetailsServiceFeignClient client;

    public UserDetailsServiceRestClient(UserDetailsServiceFeignClient client) {
        this.client = client;
    }

    @Override
    public User loadUserDetails(UserDetailsToken userDetails) {
        RestUserDetailsToken token = new RestUserDetailsToken(userDetails);
        return client.loadDetails(token);
    }

}
