package net.n2oapp.security.auth.common;

public enum UserAttributeKeysEnum {

    PRINCIPAL(new String[]{"username", "preferred_username", "login", "sub"}),
    SURNAME(new String[]{"surname", "second_name", "family_name", "lastName"}),
    NAME(new String[]{"first_name", "given_name", "name", "firstName"}),
    PATRONYMIC(new String[]{"middleName", "middle_name"}),
    EMAIL(new String[]{"email", "e-mail", "mail"}),
    GUID(new String[]{"sub", "oid"}),
    AUTHORITIES(new String[]{"roles", "authorities", "realm_access.roles", "resource_access.roles"}),
    DEPARTMENT(new String[]{"department"}),
    ORGANIZATION(new String[]{"organization"}),
    REGION(new String[]{"region"}),
    USER_LEVEL(new String[]{"userLevel"});

    public final String[] keys;

    UserAttributeKeysEnum(String[] keys) {
        this.keys = keys;
    }
}
