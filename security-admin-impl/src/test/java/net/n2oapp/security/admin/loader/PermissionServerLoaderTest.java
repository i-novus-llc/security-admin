package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.loader.PermissionServerLoader;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import net.n2oapp.security.admin.impl.repository.SystemRepository;
import net.n2oapp.security.admin.loader.builder.PermissionBuilder;
import net.n2oapp.security.admin.loader.builder.SystemEntityBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Тесты лоадера прав доступа
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
public class PermissionServerLoaderTest {

    @Autowired
    private PermissionServerLoader permissionServerLoader;

    @Autowired
    private RepositoryServerLoader<Permission, PermissionEntity, String> repositoryServerLoader;

    @Autowired
    private PermissionRepository repository;

    @LocalServerPort
    private int port;

    @Autowired
    private SystemRepository systemRepository;
    

    /**
     * Тест {@link PermissionServerLoader}
     */
    @Test
    public void simpleLoader() {
        BiConsumer<List<Permission>, String> loader = permissionServerLoader::load;
        case1(loader);
        case2(loader);
        case3(loader);
        case4(loader);
        case5(loader);
    }

    /**
     * Тест {@link RepositoryServerLoader}
     */
    @Test
    public void repositoryLoader() {
        BiConsumer<List<Permission>, String> loader = repositoryServerLoader::load;
        case1(loader);
        case2(loader);
        case3(loader);
        case4(loader);
        case5(loader);
    }

    /**
     * Вставка двух новых записей, в БД нет записей
     */
    private void case1(BiConsumer<List<Permission>, String> loader) {
        Permission permission1 = PermissionBuilder.buildPermission1();
        Permission permission2 = PermissionBuilder.buildPermission2();
        List<Permission> data = Arrays.asList(permission1, permission2);

        loader.accept(data, "system1");

        assertThat(repository.findBySystemCodeOrderByCodeDesc(SystemEntityBuilder.buildSystemEntity1()).size(), is(2));
        permissionAssertEquals(permission1, repository.findById("code1").get());
        permissionAssertEquals(permission2, repository.findById("code2").get());
    }

    /**
     * Вставка двух записей, обе есть в БД, но одна будет обновлена
     */
    private void case2(BiConsumer<List<Permission>, String> loader) {
        Permission permission1 = PermissionBuilder.buildPermission1();
        Permission permission2 = PermissionBuilder.buildPermission2Updated();
        permission2.setParentCode(permission1.getCode());
        List<Permission> data = Arrays.asList(permission1, permission2);

        loader.accept(data, "system1");

        assertThat(repository.findBySystemCodeOrderByCodeDesc(SystemEntityBuilder.buildSystemEntity1()).size(), is(2));
        permissionAssertEquals(permission1, repository.findById("code1").get());
        permissionAssertEquals(permission2, repository.findById("code2").get());
    }

    /**
     * Вставка трех записей, две есть в БД, третьей нет, родительский код второй будет обновлен
     */
    private void case3(BiConsumer<List<Permission>, String> loader) {
        Permission permission1 = PermissionBuilder.buildPermission1();
        Permission permission2 = PermissionBuilder.buildPermission2Updated();
        Permission permission3 = PermissionBuilder.buildPermission3();
        List<Permission> data = Arrays.asList(permission1, permission3, permission2);

        loader.accept(data, "system1");

        assertThat(repository.findBySystemCodeOrderByCodeDesc(SystemEntityBuilder.buildSystemEntity1()).size(), is(3));
        permissionAssertEquals(permission1, repository.findById("code1").get());
        permissionAssertEquals(permission2, repository.findById("code2").get());
        permissionAssertEquals(permission3, repository.findById("code3").get());
    }

    /**
     * Вставка двух записей, в БД три записи, третья будет удалена, вторая будет удалена каскадно
     */
    private void case4(BiConsumer<List<Permission>, String> loader) {
        Permission permission1 = PermissionBuilder.buildPermission1();
        Permission permission2 = PermissionBuilder.buildPermission2Updated();
        List<Permission> data = Arrays.asList(permission1, permission2);

        loader.accept(data, "system1");

        assertThat(repository.findBySystemCodeOrderByCodeDesc(SystemEntityBuilder.buildSystemEntity1()).size(), is(1));
        permissionAssertEquals(permission1, repository.findById("code1").get());
    }

    /**
     * Вставка двух новых записей клиента system2, в БД 1 запись клиента "system1"
     */
    private void case5(BiConsumer<List<Permission>, String> loader) {
        Permission permission4 = PermissionBuilder.buildPermission4();
        Permission permission5 = PermissionBuilder.buildPermission5();
        List<Permission> data = Arrays.asList(permission4, permission5);

        loader.accept(data, "system2");

        assertThat(repository.findBySystemCodeOrderByCodeDesc(SystemEntityBuilder.buildSystemEntity2()).size(), is(2));
        permissionAssertEquals(permission4, repository.findById("code4").get());
        permissionAssertEquals(permission5, repository.findById("code5").get());
    }


    private void permissionAssertEquals(Permission expected, PermissionEntity actual) {
        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getParentCode(), actual.getParentCode());
        assertEquals(expected.getUserLevel(), actual.getUserLevel());
    }
}
