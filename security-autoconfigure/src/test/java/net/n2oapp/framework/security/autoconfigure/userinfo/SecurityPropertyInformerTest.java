package net.n2oapp.framework.security.autoconfigure.userinfo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import net.n2oapp.framework.security.autoconfigure.access.SecurityPropertyInformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;

public class SecurityPropertyInformerTest {

    @Test
    public void userinfoRestClientTest() {
        Logger fooLogger = (Logger) LoggerFactory.getLogger(SecurityPropertyInformer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        fooLogger.addAppender(listAppender);
        SecurityPropertyInformer propertyInformer = new SecurityPropertyInformer();
        StandardEnvironment standardEnvironment = new StandardEnvironment() {
            @Override
            public String getProperty(String key) {
                return null;
            }
        };
        GenericApplicationContext context = new GenericApplicationContext();
        context.setEnvironment(standardEnvironment);
        ApplicationStartedEvent event = new ApplicationStartedEvent(new SpringApplication(), null, context, null);
        propertyInformer.onApplicationEvent(event);

        Assertions.assertEquals(SecurityPropertyInformer.PROPERTY_WARN_MESSAGE, listAppender.list.get(0).getMessage());
        Assertions.assertEquals(Level.WARN , listAppender.list.get(0).getLevel());

        Assertions.assertEquals(SecurityPropertyInformer.CONFIG_DISABLING_ADVICE_MESSAGE, listAppender.list.get(1).getMessage());
        Assertions.assertEquals(Level.INFO , listAppender.list.get(1).getLevel());
    }
}
