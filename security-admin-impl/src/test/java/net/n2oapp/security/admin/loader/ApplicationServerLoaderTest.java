package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.test.autoconfigure.pg.EnableEmbeddedPg;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.loader.ApplicationServerLoader;
import net.n2oapp.security.admin.impl.loader.model.AppModel;
import net.n2oapp.security.admin.impl.repository.ApplicationRepository;
import net.n2oapp.security.admin.impl.repository.ClientRepository;
import net.n2oapp.security.admin.impl.repository.SystemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
public class ApplicationServerLoaderTest {

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private ApplicationServerLoader appLoader;


    @Test
    public void test() {
        SystemEntity system = new SystemEntity("testSystem");
        system.setName("name");
        systemRepository.save(system);

        assertEquals(0, applicationRepository.findAll().size());

        //создание
        AppModel app1 = new AppModel();
        app1.setCode("testApp1");
        app1.setName("testName");
        app1.setClientId("testApp1");
        List<AppModel> apps = new ArrayList<>();
        apps.add(app1);

        appLoader.load(apps, "testSystem");
        assertEquals(1, applicationRepository.findAll().size());
        assertTrue(applicationRepository.findById("testApp1").isPresent());
        assertEquals(2, clientRepository.findAll().size());
        assertTrue(clientRepository.findById("testApp1").isPresent());

        //удаление ранее добавленного, создание нового
        apps.clear();
        AppModel app2 = new AppModel();
        app2.setCode("testApp2");
        app2.setName("testName");
        app2.setClientId("testApp2");
        apps.add(app2);

        appLoader.load(apps, "testSystem");
        assertEquals(1, applicationRepository.findAll().size());
        assertTrue(applicationRepository.findById("testApp2").isPresent());
        assertEquals(2, clientRepository.findAll().size());
        assertTrue(clientRepository.findById("testApp2").isPresent());

        //Приложение без клиента
        AppModel app3 = new AppModel();
        app3.setCode("testApp3");
        app3.setName("testName");
        apps.add(app3);

        appLoader.load(apps, "testSystem");
        assertEquals(2, applicationRepository.findAll().size());
        assertEquals(2, clientRepository.findAll().size());
    }
}
