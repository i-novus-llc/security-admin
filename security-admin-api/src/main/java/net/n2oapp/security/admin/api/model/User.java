package net.n2oapp.security.admin.api.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Модель пользователя для показа на UI
 */
@Data
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
    private LocalDateTime lastActionDate;

}
