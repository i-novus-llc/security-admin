package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Уровень пользователей системы (подсистемы, модуля)
 */
public enum UserLevel {
    NONE("Не задан"),
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

    public String getName() {
        return name();
    }
}
