package net.n2oapp.security.admin.util;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.service.MailService;
import net.n2oapp.security.admin.impl.util.MailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MailServiceImpl.class})
@PropertySource(value = {"classpath:mail.properties", "classpath:test.properties"})
@Import(ThymeleafAutoConfiguration.class)
public class MailServiceTest {

    @Autowired
    private MailService mailService;

    @MockBean
    private JavaMailSender emailSender;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor;

    @BeforeEach
    public void before() {
        Mockito.doNothing().when(emailSender).send(Mockito.any(MimeMessage.class));
        Mockito.doReturn(new MimeMessage(Session.getDefaultInstance(new Properties()))).when(emailSender).createMimeMessage();
    }

    @Test
    public void testSendWelcomeMail() {
        UserForm user = new UserForm();
        user.setUsername("username");
        user.setPassword("password");
        user.setSurname("surname");
        user.setName("name");
        user.setPatronymic("patronymic");
        user.setEmail("email");

        mailService.sendWelcomeMail(user);

        try {
            Mockito.verify(emailSender, Mockito.timeout(10000).atLeastOnce()).send(mimeMessageArgumentCaptor.capture());
            Object content = mimeMessageArgumentCaptor.getValue().getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>surname</span> <span>name</span>!</p>"));
            assertTrue(content.toString().contains("<p>Вы зарегистрированы в системе.</p>"));
            assertTrue(content.toString().contains("<p>Логин для входа: <span>username</span></p>"));
        } catch (IOException | MessagingException e) {
            fail();
        }


    }

    @Test
    public void testSendWelcomeMailWithoutPassword() {
        UserForm user = new UserForm();
        user.setUsername("username");
        user.setSurname("surname");
        user.setName("name");
        user.setPatronymic("patronymic");
        user.setEmail("email");
        user.setPassword(null);
        user.setTemporaryPassword("12345");

        mailService.sendWelcomeMail(user);
        try {
            Mockito.verify(emailSender, Mockito.timeout(10000).atLeastOnce()).send(mimeMessageArgumentCaptor.capture());
            Object content = mimeMessageArgumentCaptor.getValue().getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>surname</span> <span>name</span>!</p>"));
            assertTrue(content.toString().contains("<p>Логин для входа: <span>username</span></p>"));
            assertTrue(content.toString().contains("<p>Временный пароль:"));
            assertTrue(content.toString().contains("<p>Пожалуйста, измените пароль при следующем входе.</p>"));
        } catch (IOException | MessagingException e) {
            fail();
        }
    }

    @Test
    public void testSendResetPasswordMail() {
        UserForm user = new UserForm();
        user.setUsername("username");
        user.setSurname("surname");
        user.setName("name");
        user.setPatronymic("patronymic");
        user.setEmail("email");

        mailService.sendResetPasswordMail(user);

        try {
            Mockito.verify(emailSender, Mockito.timeout(10000).atLeastOnce()).send(mimeMessageArgumentCaptor.capture());
            Object content = mimeMessageArgumentCaptor.getValue().getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>username</span>!</p>"));
            assertTrue(content.toString().contains("<p>Ваш пароль был сброшен.</p>"));
            assertTrue(content.toString().contains("<p>Временный пароль:"));
            assertTrue(content.toString().contains("<p>Пожалуйста измените пароль при следующем входе.</p>"));
        } catch (IOException | MessagingException e) {
            fail();
        }
    }

    @Test
    public void testSendChangeActivateMail() {
        User user = new User();
        user.setUsername("username");
        user.setSurname("surname");
        user.setName("name");
        user.setPatronymic("patronymic");
        user.setEmail("email");
        user.setIsActive(true);

        mailService.sendChangeActivateMail(user);

        try {
            Mockito.verify(emailSender, Mockito.timeout(10000).atLeastOnce()).send(mimeMessageArgumentCaptor.capture());
            Object content = mimeMessageArgumentCaptor.getValue().getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>surname</span> <span>name</span> <span>patronymic</span>!</p>"));
            assertTrue(content.toString().contains("Признак активности Вашей учетной записи изменен на \"<span>Да</span>\"."));
        } catch (IOException | MessagingException e) {
            fail();
        }
    }

    @Test
    public void testSendUserDeletedMail() {
        User user = new User();
        user.setUsername("username");
        user.setSurname("surname");
        user.setName("name");
        user.setPatronymic("patronymic");
        user.setEmail("email");

        mailService.sendUserDeletedMail(user);

        try {
            Mockito.verify(emailSender, Mockito.timeout(10000).atLeastOnce()).send(mimeMessageArgumentCaptor.capture());
            Object content = mimeMessageArgumentCaptor.getValue().getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>surname</span> <span>name</span> <span>patronymic</span>!</p>"));
            assertTrue(content.toString().contains("<p>Ваша учетная запись удалена.</p>"));
        } catch (IOException | MessagingException e) {
            fail();
        }
    }
}
