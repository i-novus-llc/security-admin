package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
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


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@EnableTestcontainersPg
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

        assertEquals(n + 1, systemRepository.findAll().size());
        assertEquals("testName", systemRepository.findOneByCode("testCode").getName());

        AppSystem system2 = new AppSystem();
        system2.setCode("testCode2");
        system2.setName("testName2");
        systems.add(system2);

        systemLoader.load(systems, "systems");

        assertEquals(n + 2, systemRepository.findAll().size());
        assertEquals("testName2", systemRepository.findOneByCode("testCode2").getName());
    }
}
