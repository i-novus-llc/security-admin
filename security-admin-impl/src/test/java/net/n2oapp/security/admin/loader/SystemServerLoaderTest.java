package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.SystemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
public class SystemServerLoaderTest {
    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private RepositoryServerLoader<AppSystem, SystemEntity, String> systemLoader;

    @Test
    public void test() {
        AppSystem system1 = new AppSystem();
        system1.setCode("testCode");
        system1.setName("testName");
        List<AppSystem> systems = new ArrayList<>();
        systems.add(system1);

        int n = systemRepository.findAll().size();
        systemLoader.load(systems, "systems");

        assertThat(systemRepository.findAll().size(), is(n + 1));
        assertThat(systemRepository.findOneByCode("testCode").getName(), is("testName"));

        AppSystem system2 = new AppSystem();
        system2.setCode("testCode2");
        system2.setName("testName2");
        systems.add(system2);

        systemLoader.load(systems, "systems");

        assertThat(systemRepository.findAll().size(), is(n + 2));
        assertThat(systemRepository.findOneByCode("testCode2").getName(), is("testName2"));
    }
}
