package net.n2oapp.security.admin.commons.util;

import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.service.MailService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
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
    @Value("${sec.password.mail.body.path}")
    private String welcomeUserMail;
    @Value("${sec.password.mail.subject}")
    private String mailSubject;
    @Value("${sec.password.mail.send}")
    private Boolean sendWelcomeEmail;

    // TODO - убрать строчку Пароль: ${password}
    public void sendWelcomeMail(UserForm user) {
        if (sendWelcomeEmail) {
            Map<String, String> data = new HashMap<>();
            data.put("username", user.getUsername());
            data.put("surname", valueOrEmpty(user.getSurname()));
            data.put("name", valueOrEmpty(user.getName()));
            data.put("patronymic", valueOrEmpty(user.getPatronymic()));
            if (Boolean.TRUE.equals(user.getSendOnEmail()))
                data.put("password", user.getPassword() != null ? user.getPassword() : user.getTemporaryPassword());
            data.put("email", user.getEmail());
            String subjectTemplate = mailSubject;
            MimeMessage message = emailSender.createMimeMessage();
            String body = null;
            try (InputStream inputStream = MailServiceImpl.class.getClassLoader().getResourceAsStream(welcomeUserMail);) {
                body = StrSubstitutor.replace(IOUtils.toString(inputStream, "UTF-8"), data);
            } catch (IOException e) {
                throw new IllegalStateException("Exception while opening resource " + welcomeUserMail, e);
            }
            String subject = StrSubstitutor.replace(subjectTemplate, data);
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
                helper.setTo(user.getEmail());
                helper.setSubject(subject);
                helper.setText(body, true);
                emailSender.send(message);
            } catch (MailException exception) {
                throw new IllegalStateException("Exception while sending mail notification to " + user.getUsername() + "\n", exception);
            } catch (MessagingException e) {
                throw new IllegalStateException("Exception while sending mail notification to " + user.getUsername() + "\n", e);
            }
        }
    }

    // --TODO переделать сообщение для сброса пароля
    @Override
    public void sendResetPasswordMail(UserForm user) {
        Map<String, String> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("password", user.getPassword() != null ? user.getPassword() : user.getTemporaryPassword());
        data.put("email", user.getEmail());
        String subjectTemplate = mailSubject;
        MimeMessage message = emailSender.createMimeMessage();
        String body = null;
        try (InputStream inputStream = MailServiceImpl.class.getClassLoader().getResourceAsStream(welcomeUserMail);) {
            body = StrSubstitutor.replace(IOUtils.toString(inputStream, "UTF-8"), data);
        } catch (IOException e) {
            throw new IllegalStateException("Exception while opening resource " + welcomeUserMail, e);
        }
        String subject = StrSubstitutor.replace(subjectTemplate, data);
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true);
            emailSender.send(message);
        } catch (MailException exception) {
            throw new IllegalStateException("Exception while sending mail notification to " + user.getUsername() + "\n", exception);
        } catch (MessagingException e) {
            throw new IllegalStateException("Exception while sending mail notification to " + user.getUsername() + "\n", e);
        }
    }

    private String valueOrEmpty(String param) {
        if (param == null)
            return "";
        return param;
    }
}
