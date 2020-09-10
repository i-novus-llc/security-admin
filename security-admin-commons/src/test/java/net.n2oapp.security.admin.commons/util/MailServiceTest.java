package net.n2oapp.security.admin.commons.util;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetup;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.service.MailService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MailServiceImpl.class})
@Import(MailConfiguration.class)
@PropertySource("classpath:mail.properties")
@EnableAutoConfiguration
public class MailServiceTest {

    @Autowired
    private MailService mailService;

    @Rule
    public final GreenMailRule greenMail = new GreenMailRule(new ServerSetup(2525, null, "smtp"));

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
        assertTrue(greenMail.waitForIncomingEmail(1000, 1));
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);
        try {
            Object content = receivedMessages[0].getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>surname</span> <span>name</span>!</p>"));
            assertTrue(content.toString().contains("<p>Вы зарегистрированы в системе.</p>"));
            assertTrue(content.toString().contains("<p>Логин для входа: <span>username</span></p>"));
        } catch (IOException | MessagingException e) {
            fail();
        }
    }
}
