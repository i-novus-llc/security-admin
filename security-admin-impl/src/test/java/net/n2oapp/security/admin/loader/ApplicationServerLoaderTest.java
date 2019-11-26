package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.impl.entity.ApplicationEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.ApplicationRepository;
import net.n2oapp.security.admin.impl.repository.SystemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
public class ApplicationServerLoaderTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private RepositoryServerLoader<Application, ApplicationEntity, String> appLoader;

    @Test
    public void test() {
        SystemEntity system = new SystemEntity("forAppTest");
        system.setName("name");
        systemRepository.save(system);

        assertThat(applicationRepository.findAll().size(), is(0));

        Application app1 = new Application();
        app1.setCode("testApp1");
        app1.setName("testAppName1");
        List<Application> apps = new ArrayList<>();
        apps.add(app1);

        appLoader.load(apps, "forAppTest");

        assertThat(applicationRepository.findAll().size(), is(1));
        assertThat(applicationRepository.getOne(app1.getCode()).getName(), is("testAppName1"));

        Application app2 = new Application();
        app2.setCode("testApp2");
        app2.setName("testAppName2");
        apps.add(app2);

        appLoader.load(apps, "forAppTest");
        assertThat(applicationRepository.findAll().size(), is(2));
        assertThat(applicationRepository.getOne(app1.getCode()).getName(), is("testAppName1"));
        assertThat(applicationRepository.getOne(app2.getCode()).getName(), is("testAppName2"));

        apps.remove(0);
        app2.setName("newTestName");

        appLoader.load(apps, "forAppTest");
        assertThat(applicationRepository.findAll().size(), is(1));
        assertThat(applicationRepository.getOne(app2.getCode()).getName(), is("newTestName"));
    }
}
