package net.n2oapp.security.admin.api.model;

/**
 * Уровень пользователей системы (подсистемы, модуля)
 */
public enum UserLevel {
    NONE("Без ограничений"),
    FEDERAL("Федеральный уровень"),
    REGIONAL("Региональный уровень"),
    ORGANIZATION("Уровень организации");

    private String desc;

    UserLevel(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
