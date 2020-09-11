package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.loader.builder.RoleFormBuilder;
import net.n2oapp.security.admin.loader.builder.SystemEntityBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Тесты лоадера ролей
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
public class RoleServerLoaderTest {

    @Autowired
    private ServerLoader<RoleForm> serverLoader;

    @Autowired
    private RoleRepository repository;

    @MockBean
    private SsoUserRoleProvider provider;

    /**
     * Тест {@link ServerLoader}
     */
    @Test
    public void repositoryLoader() {
        Mockito.doReturn(new Role()).when(provider).createRole(Mockito.any());
        Mockito.doReturn(new ArrayList<>()).when(provider).getAllRoles();
        BiConsumer<List<RoleForm>, String> loader = serverLoader::load;
        repository.removeBySystemCode(SystemEntityBuilder.buildSystemEntity1());
        repository.removeBySystemCode(SystemEntityBuilder.buildSystemEntity2());
        case1(loader);
        case3(loader);
        case6(loader);
    }

    /**
     * Вставка двух новых записей, в БД нет записей
     */
    private void case1(BiConsumer<List<RoleForm>, String> loader) {
        RoleForm roleForm1 = RoleFormBuilder.buildRoleForm1();
        RoleForm roleForm2 = RoleFormBuilder.buildRoleForm2();
        List<RoleForm> data = Arrays.asList(roleForm1, roleForm2);

        loader.accept(data, "system1");

        assertThat(repository.findBySystemCode(SystemEntityBuilder.buildSystemEntity1()).size(), is(2));
        roleAssertEquals(roleForm1, repository.findOneByCode("rcode1"));
        roleAssertEquals(roleForm2, repository.findOneByCode("rcode2"));
    }

    /**
     * Вставка трех записей, две есть в БД, третья изменена
     */
    private void case3(BiConsumer<List<RoleForm>, String> loader) {
        RoleForm roleForm1 = RoleFormBuilder.buildRoleForm1();
        RoleForm roleForm2 = RoleFormBuilder.buildRoleForm2Updated();
        RoleForm roleForm3 = RoleFormBuilder.buildRoleForm3();
        List<RoleForm> data = Arrays.asList(roleForm1, roleForm2, roleForm3);

        loader.accept(data, "system1");

        assertThat(repository.findBySystemCode(SystemEntityBuilder.buildSystemEntity1()).size(), is(3));
        roleAssertEquals(roleForm1, repository.findOneByCode("rcode1"));
        roleAssertEquals(RoleFormBuilder.buildRoleForm2(), repository.findOneByCode("rcode2"));
        roleAssertEquals(roleForm3, repository.findOneByCode("rcode3"));
    }

    /**
     * Вставка двух новых записей клиента system2, в БД 3 записи клиента "system1"
     */
    private void case6(BiConsumer<List<RoleForm>, String> loader) {
        RoleForm roleForm4 = RoleFormBuilder.buildRoleForm4();
        RoleForm roleForm5 = RoleFormBuilder.buildRoleForm5();
        List<RoleForm> data = Arrays.asList(roleForm4, roleForm5);

        loader.accept(data, "system2");

        assertThat(repository.findBySystemCode(SystemEntityBuilder.buildSystemEntity2()).size(), is(2));
        roleAssertEquals(roleForm4, repository.findOneByCode("rcode4"));
        roleAssertEquals(roleForm5, repository.findOneByCode("rcode5"));
    }

    private void roleAssertEquals(RoleForm expected, RoleEntity actual) {
        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(
                expected.getPermissions(),
                actual.getPermissionList().stream().map(PermissionEntity::getCode).collect(Collectors.toList())
        );
        assertEquals(expected.getUserLevel(), actual.getUserLevel().toString());
    }
}
