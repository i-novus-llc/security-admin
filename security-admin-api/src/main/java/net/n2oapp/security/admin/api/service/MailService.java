package net.n2oapp.security.admin.api.service;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import net.n2oapp.security.admin.api.model.User;

/**
 * Сервис отправки писем
 */
public interface MailService {

    /**
     * Отправление письма
     *
     * @param user Пользователь
     */

    void sendWelcomeMail(User user);
}

