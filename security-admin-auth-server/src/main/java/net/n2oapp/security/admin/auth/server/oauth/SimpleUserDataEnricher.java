package net.n2oapp.security.admin.auth.server.oauth;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Objects.nonNull;

@Component
public class SimpleUserDataEnricher implements UserInfoEnricher<AccountEntity> {

    public static final String NAME = "name";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String SURNAME = "surname";
    public static final String PATRONYMIC = "patronymic";
    public static final String USER_LEVEL = "userLevel";

    @Override
    public void enrich(Map<String, Object> userInfo, AccountEntity source) {
        UserEntity user = source.getUser();

        if (nonNull(user.getUsername())) {
            userInfo.put(USERNAME, user.getUsername());
        }

        if (nonNull(user.getEmail())) {
            userInfo.put(EMAIL, user.getEmail());
        }

        if (nonNull(user.getName())) {
            userInfo.put(NAME, user.getName());
        }

        if (nonNull(user.getName())) {
            userInfo.put(SURNAME, user.getSurname());
        }

        if (nonNull(user.getPatronymic())) {
            userInfo.put(PATRONYMIC, user.getPatronymic());
        }

        if (nonNull(source.getUserLevel())) {
            userInfo.put(USER_LEVEL, source.getUserLevel());
        }
    }

    @Override
    public Object buildValue(AccountEntity source) {
        // unused
        return null;
    }

    @Override
    public String getAlias() {
        return "simple";
    }
}
