package net.n2oapp.security.admin.api.model;

import lombok.Data;

import java.util.List;

/**
 * Модель данных пользователя для интеграции с sso сервером
 */
@Data
public class UserDetailsToken {
    private String guid;
    private String username;
    private String name;
    private String surname;
    private String email;
    private List<String> roleNames;
}
