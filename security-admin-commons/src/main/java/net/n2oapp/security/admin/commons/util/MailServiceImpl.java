package net.n2oapp.security.admin.commons.util;

import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для рассылки сообщений
 */
@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Имя ресурса для приветственного сообщения
     */
    @Value("${sec.password.mail.welcome.resource.name}")
    private String welcomeMailResource;

    /**
     * Тема приветственного сообщения
     */
    @Value("${sec.password.mail.welcome.subject}")
    private String welcomeMailSubject;

    /**
     * Имя ресурса при сбросе пароля
     */
    @Value("${sec.password.mail.reset-password.resource.name}")
    private String resetPasswordMailResource;

    /**
     * Тема сообщения при сбросе пароля
     */
    @Value("${sec.password.mail.reset-password.subject}")
    private String resetPasswordMailSubject;

    /**
     * Почтовый ящик отправителя
     */
    @Value("${sec.mail.message.from}")
    private String mailMessageFrom;

    /**
     * Отправка сообщения на почту при успешном создании пользователя
     * @param user Пользователь
     */
    @Override
    public void sendWelcomeMail(UserForm user) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("surname", valueOrEmpty(user.getSurname()));
        data.put("name", valueOrEmpty(user.getName()));
        data.put("patronymic", valueOrEmpty(user.getPatronymic()));
        data.put("password", user.getPassword() != null ? user.getPassword() : user.getTemporaryPassword());
        data.put("email", user.getEmail());

        sendMail(data, welcomeMailSubject, welcomeMailResource);
    }

    /**
     * Отправка сообщения на почту при сбросе пароля
     * @param user Пользователь
     */
    @Override
    public void sendResetPasswordMail(UserForm user) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("password", user.getPassword() != null ? user.getPassword() : user.getTemporaryPassword());
        data.put("email", user.getEmail());

        sendMail(data, resetPasswordMailSubject, resetPasswordMailResource);
    }

    /**
     * Отправка сообщений
     * @param data Список атрибутов, передаваемых в тело сообщения
     * @param subject Тема сообщения
     * @param resource Имя ресурса, в котором формируется тело сообщения
     */
    private void sendMail(Map<String, Object> data, String subject, String resource) {
        Context context = new Context();
        context.setVariables(data);
        String body = templateEngine.process(resource, context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setFrom(mailMessageFrom);
            helper.setTo((String) data.get("email"));
            helper.setSubject(subject);
            helper.setText(body, true);
            emailSender.send(message);
        } catch (MailException e) {
            throw new IllegalStateException("Exception while sending mail notification to " + data.get("username") + "\n", e);
        } catch (MessagingException e) {
            throw new IllegalStateException("Exception while sending mail notification to " + data.get("username") + "\n", e);
        }
    }

    /**
     * Предобработка входных атрибутов
     * @param param Атрибут
     * @return Если атрибут не равен null, то возвращается искомое значение атрибута, иначе - пустая строка
     */
    private String valueOrEmpty(String param) {
        if (param == null)
            return "";
        return param;
    }
}
