package net.n2oapp.security.admin.api.model;

import lombok.Data;

import java.util.List;

/**
 * Модель пользователя для SSO провайдера
 */
@Data
public class SsoUser extends User {

    private String extSys;

    private String extUid;

    private List<String> requiredActions;
}
