package net.n2oapp.security.admin.auth.server.oauth;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.UserEntity;

import java.util.Map;

import static java.util.Objects.nonNull;

public class SimpleUserDataEnricher implements UserInfoEnricher<UserEntity> {

    public static final String NAME = "name";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String SURNAME = "surname";
    public static final String PATRONYMIC = "patronymic";

    @Override
    public void enrich(Map<String, Object> userInfo, UserEntity source) {
        if (nonNull(source.getUsername())) {
            userInfo.put(USERNAME, source.getUsername());
        }

        if (nonNull(source.getEmail())) {
            userInfo.put(EMAIL, source.getEmail());
        }

        if (nonNull(source.getName())) {
            userInfo.put(NAME, source.getName());
        }

        if (nonNull(source.getName())) {
            userInfo.put(SURNAME, source.getSurname());
        }

        if (nonNull(source.getPatronymic())) {
            userInfo.put(PATRONYMIC, source.getPatronymic());
        }
    }

    @Override
    public Object buildValue(UserEntity source) {
        // unused
        return null;
    }

    @Override
    public String getAlias() {
        return "simple";
    }
}
