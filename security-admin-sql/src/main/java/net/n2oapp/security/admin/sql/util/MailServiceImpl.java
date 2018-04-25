package net.n2oapp.security.admin.sql.util;


import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.MailService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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

    protected static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private JavaMailSender emailSender;
    @Value("mail/welcomeUser.html")
    private String welcomeUserMail;
    @Value("${sec.password.mail.subject}")
    private String mailSubject;



    public void sendWelcomeMail(User user) {

        Map<String, String> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("surname", user.getSurname());
        data.put("name", user.getName());
        data.put("patronymic", user.getPatronymic() == null ? "" : user.getPatronymic());
        data.put("password",user.getPassword());
        data.put("email", user.getEmail());
        String subjectTemplate = mailSubject;
        MimeMessage message = emailSender.createMimeMessage();
        String body = null;
        try (InputStream inputStream = MailServiceImpl.class.getClassLoader().getResourceAsStream(welcomeUserMail);) {
            body = StrSubstitutor.replace(IOUtils.toString(inputStream, "UTF-8"), data);
        } catch (IOException e) {
            throw new IllegalStateException("Exception while opening resource " + welcomeUserMail , e);
        }
        String subject = StrSubstitutor.replace(subjectTemplate, data);
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(body,true);
            emailSender.send(message);
        } catch (MailException exception) {
            throw new IllegalStateException("Exception while sending mail notification to "  + user.getUsername() + "\n",exception);
        } catch (MessagingException e) {
            throw new IllegalStateException("Exception while sending mail notification to "  + user.getUsername() + "\n", e);
        }

    }
}
