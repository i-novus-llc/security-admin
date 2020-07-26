package net.n2oapp.security.admin.api.model;

public enum UserStatus {
    AWAITING_MODERATION("Ожидает модерации"), REGISTERED("Зарегистрирован");

    private String description;

    UserStatus(String description) {
        this.description = description;
    }
}
