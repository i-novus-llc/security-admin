package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.platform.test.autoconfigure.pg.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import net.n2oapp.security.admin.loader.builder.PermissionBuilder;
import net.n2oapp.security.admin.loader.builder.SystemEntityBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Тесты лоадера прав доступа
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
public class PermissionServerLoaderTest {

    @Autowired
    private ServerLoader<Permission> serverLoader;

    @Autowired
    private PermissionRepository repository;

    /**
     * Тест {@link RepositoryServerLoader}
     */
    @Test
    public void repositoryLoader() {
        BiConsumer<List<Permission>, String> loader = serverLoader::load;
        repository.deleteInBatch(repository.findBySystemCodeOrderByCodeDesc(SystemEntityBuilder.buildSystemEntity1()));
        repository.deleteInBatch(repository.findBySystemCodeOrderByCodeDesc(SystemEntityBuilder.buildSystemEntity2()));
        case1(loader);
        case2(loader);
        case3(loader);
        case4(loader);
        case5(loader);
        case6(loader);
    }

    /**
     * Вставка двух новых записей, в БД нет записей
     */
    private void case1(BiConsumer<List<Permission>, String> loader) {
        Permission permission1 = PermissionBuilder.buildPermission1();
        Permission permission2 = PermissionBuilder.buildPermission2();
        List<Permission> data = Arrays.asList(permission1, permission2);

        loader.accept(data, "system1");

        assertEquals(2, repository.findBySystemCodeOrderByCodeDesc(SystemEntityBuilder.buildSystemEntity1()).size());
        permissionAssertEquals(permission1, repository.findById("pcode1").get());
        permissionAssertEquals(permission2, repository.findById("pcode2").get());
    }

    /**
     * Вставка двух записей, обе есть в БД, но одна будет обновлена
     */
    private void case2(BiConsumer<List<Permission>, String> loader) {
        Permission permission1 = PermissionBuilder.buildPermission1();
        Permission permission2 = PermissionBuilder.buildPermission2Updated();
        permission2.setParent(permission1);
        List<Permission> data = Arrays.asList(permission1, permission2);

        loader.accept(data, "system1");

        assertEquals(2, repository.findBySystemCodeOrderByCodeDesc(SystemEntityBuilder.buildSystemEntity1()).size());
        permissionAssertEquals(permission1, repository.findById("pcode1").get());
        permissionAssertEquals(permission2, repository.findById("pcode2").get());
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

        assertEquals(3, repository.findBySystemCodeOrderByCodeDesc(SystemEntityBuilder.buildSystemEntity1()).size());
        permissionAssertEquals(permission1, repository.findById("pcode1").get());
        permissionAssertEquals(permission2, repository.findById("pcode2").get());
        permissionAssertEquals(permission3, repository.findById("pcode3").get());
    }

    /**
     * Вставка двух записей, в БД три записи, третья будет удалена, вторая будет удалена каскадно
     */
    private void case4(BiConsumer<List<Permission>, String> loader) {
        Permission permission1 = PermissionBuilder.buildPermission1();
        Permission permission2 = PermissionBuilder.buildPermission2Updated();
        List<Permission> data = Arrays.asList(permission1, permission2);

        loader.accept(data, "system1");

        assertEquals(1, repository.findBySystemCodeOrderByCodeDesc(SystemEntityBuilder.buildSystemEntity1()).size());
        permissionAssertEquals(permission1, repository.findById("pcode1").get());
    }

    /**
     * Вставка двух записей, в БД одна запись
     */
    private void case5(BiConsumer<List<Permission>, String> loader) {
        Permission permission1 = PermissionBuilder.buildPermission1();
        Permission permission3 = PermissionBuilder.buildPermission3();
        List<Permission> data = Arrays.asList(permission1, permission3);

        loader.accept(data, "system1");

        assertEquals(2, repository.findBySystemCodeOrderByCodeDesc(SystemEntityBuilder.buildSystemEntity1()).size());
        permissionAssertEquals(permission1, repository.findById("pcode1").get());
        permissionAssertEquals(permission3, repository.findById("pcode3").get());
    }

    /**
     * Вставка двух новых записей клиента system2, в БД 2 записи клиента "system1"
     */
    private void case6(BiConsumer<List<Permission>, String> loader) {
        Permission permission4 = PermissionBuilder.buildPermission4();
        Permission permission5 = PermissionBuilder.buildPermission5();
        List<Permission> data = Arrays.asList(permission4, permission5);

        loader.accept(data, "system2");

        assertEquals(2, repository.findBySystemCodeOrderByCodeDesc(SystemEntityBuilder.buildSystemEntity2()).size());
        permissionAssertEquals(permission4, repository.findById("pcode4").get());
        permissionAssertEquals(permission5, repository.findById("pcode5").get());
    }

    private void permissionAssertEquals(Permission expected, PermissionEntity actual) {
        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getName(), actual.getName());
        if (expected.getParent() != null || actual.getParentPermission() != null)
            assertEquals(expected.getParent().getCode(), actual.getParentPermission().getCode());
        assertEquals(expected.getUserLevel(), actual.getUserLevel());
    }
}
