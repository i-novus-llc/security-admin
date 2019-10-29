package net.n2oapp.security.admin.api.model;


/**
 * Уровень пользователей системы (подсистемы, модуля)
 */
public enum UserLevel {
    FEDERAL("Федеральный уровень"),
    REGIONAL("Региональный уровень"),
    ORGANIZATION("Уровень организации"),
    NOT_SET("Не задан");

    private String desc;

    UserLevel(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name();
    }
}
