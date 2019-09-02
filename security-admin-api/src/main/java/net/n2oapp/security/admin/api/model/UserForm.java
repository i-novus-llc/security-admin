package net.n2oapp.security.admin.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Модель пользователя для actions
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserForm {
    private Integer id;
    private String guid;
    private String username;
    private String email;
    private String surname;
    private String name;
    private String patronymic;
    private String newPassword;
    private String password;
    private String passwordCheck;
    private Boolean isActive;
    private List<Integer> roles;
}
