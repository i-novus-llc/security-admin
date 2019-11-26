package net.n2oapp.security.admin.api.service;


import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;

/**
 * Сервис отправки писем
 */
public interface MailService {

    /**
     * Отправление письма
     *
     * @param user Пользователь
     */

    void sendWelcomeMail(UserForm user);

    void sendResetPasswordMail(UserForm user);

    void sendChangeActivateMail(User user);

    void sendUserDeletedMail(User user);

}

