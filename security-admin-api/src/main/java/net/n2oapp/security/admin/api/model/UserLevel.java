package net.n2oapp.security.admin.api.model;

/**
 * Уровень пользователей системы (подсистемы, модуля)
 */
public enum UserLevel {
    NONE("Без ограничений"),
    REGIONAL("Региональный уровень"),
    ORGANIZATION("Уровень организации"),
    FEDERAL("Федеральный уровень");

    private String desc;

    UserLevel(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
