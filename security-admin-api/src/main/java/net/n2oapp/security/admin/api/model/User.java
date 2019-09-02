package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Модель пользователя для показа на UI
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Integer id;
    private String guid;
    private String username;
    private String fio;
    private String email;
    private String surname;
    private String name;
    private String patronymic;
    private String password;
    private String passwordHash;
    private String passwordCheck;
    private Boolean isActive;
    private List<Role> roles;
}
