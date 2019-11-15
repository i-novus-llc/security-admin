package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.loader.server.ServerLoaderRunner;
import net.n2oapp.security.admin.impl.repository.ApplicationRepository;
import net.n2oapp.security.admin.impl.repository.ClientRepository;
import net.n2oapp.security.admin.impl.repository.SystemRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoadersTest {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private SystemRepository systemRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ServerLoaderRunner loaderRunner;

    /**
     * Проверяет накат в пустые таблицы
     */
    @Test
    public void testEmptyRepository() {
        assertThat(clientRepository.findAll().size(), is(1));
        assertThat(clientRepository.findAll().get(0).getClientSecret(), is("test_secret"));
        assertThat(clientRepository.findAll().get(0).getGrantTypes(), is("client_credentials"));
        assertThat(clientRepository.findAll().get(0).getRedirectUris(), is("a,b"));
        assertThat(clientRepository.findAll().get(0).getAccessTokenLifetime(), is(10));

        assertThat(systemRepository.findAll().size(), is(1));
        assertThat(systemRepository.findAll().get(0).getCode(), is("testSystem"));
        assertThat(systemRepository.findAll().get(0).getName(), is("testName"));

        assertThat(applicationRepository.findAll().size(), is(1));
        assertThat(applicationRepository.findAll().get(0).getCode(), is("test"));
        assertThat(applicationRepository.findAll().get(0).getName(), is("testName"));
        assertThat(applicationRepository.findAll().get(0).getSystemCode().getCode(), is("testSystem"));
        assertThat(applicationRepository.findAll().get(0).getSystemCode().getName(), is("testName"));
    }

    /**
     * Проверяет накат в таблицы с содержимым
     * Устаревшие приложения удаляются
     * Устаревшие системы не удаляются
     */
    @Test
    public void testNotEmptyRepository() throws IOException {
        assertThat(applicationRepository.findAll().get(0).getCode(), is("test"));

        loaderRunner.run("", "systems", new ClassPathResource("data/systems2.json").getInputStream());
        loaderRunner.run("", "applications", new ClassPathResource("data/applications2.json").getInputStream());

        assertThat(applicationRepository.findAll().size(), is(2));
        assertThat(applicationRepository.findAll().get(0).getCode(), is("insteadTest"));
        assertThat(applicationRepository.findAll().get(1).getCode(), is("test2"));
        assertThat(systemRepository.findAll().get(0).getCode(), is("testSystem"));
        assertThat(systemRepository.findAll().get(1).getCode(), is("testSystem2"));

    }
}
