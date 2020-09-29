package net.n2oapp.security.admin.commons.util;

import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.service.MailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MailServiceImpl.class})
@PropertySource("classpath:mail.properties")
@EnableAutoConfiguration
public class MailServiceTest {

    @Autowired
    private MailService mailService;

    @MockBean
    private JavaMailSender emailSender;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor;

    @Before
    public void before() {
        Mockito.doNothing().when(emailSender).send(mimeMessageArgumentCaptor.capture());
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
            Object content = mimeMessageArgumentCaptor.getValue().getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>surname</span> <span>name</span>!</p>"));
            assertTrue(content.toString().contains("<p>Вы зарегистрированы в системе.</p>"));
            assertTrue(content.toString().contains("<p>Логин для входа: <span>username</span></p>"));
        } catch (IOException | MessagingException e) {
            fail();
        }

        user.setPassword(null);
        user.setTemporaryPassword("12345");

        mailService.sendWelcomeMail(user);
        try {
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
            Object content = mimeMessageArgumentCaptor.getValue().getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>surname</span> <span>name</span> <span>patronymic</span>!</p>"));
            assertTrue(content.toString().contains("<p>Ваша учетная запись удалена.</p>"));
        } catch (IOException | MessagingException e) {
            fail();
        }
    }
}