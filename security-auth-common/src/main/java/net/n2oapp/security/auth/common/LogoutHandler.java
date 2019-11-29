package net.n2oapp.security.auth.common;

import org.springframework.security.core.Authentication;

/**
 * Обработчик Logout действия
 */
public interface LogoutHandler {

    /**
     * Произвести выход
     *
     * @param authentication - Токен аутентификации пользователя
     */
    void doLogout(Authentication authentication);

}
