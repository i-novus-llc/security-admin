package net.n2oapp.framework.security.auth.oauth2.gateway;

import net.n2oapp.security.auth.common.User;
import net.n2oapp.security.auth.common.UserParamsUtil;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Создание объекта пользователя из информации в SSO сервере
 */
@Component
public class GatewayPrincipalExtractor implements PrincipalExtractor, AuthoritiesExtractor {

    private static final String USERNAME = "username";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String SURNAME = "surname";
    private static final String PATRONYMIC = "patronymic";
    private static final String DEPARTMENT = "department";
    private static final String CODE_KEY = "code";
    private static final String NAME_KEY = "name";

    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        return UserParamsUtil.extractAuthorities(map);
    }

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        //todo учесть что параметры в кейклок могут быть названы по разному, смотри AuthoritiesPrincipalExtractor
        User user = new User((String) map.get(USERNAME), "N/A", extractAuthorities(map), (String) map.get(SURNAME), (String) map.get(NAME),
                (String) map.get(PATRONYMIC), (String) map.get(EMAIL));

        LinkedHashMap department = (LinkedHashMap) map.get(DEPARTMENT);

        if (department != null) {
            user.setDepartment((String) department.get(CODE_KEY));
            user.setDepartmentName((String) department.get(NAME_KEY));
        }

        return user;
    }
}
