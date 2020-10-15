package net.n2oapp.security.admin.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.service.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.*;

/**
 * Тест сервиса управления ролями пользователя
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
public class RoleServiceImplTest {


    @Autowired
    private RoleService service;


    @Test
    public void testUp() throws Exception {
        assertNotNull(service);
    }

    @Test
    public void checkRoleValidations() {
        Role role = service.create(newRole());
        checkValidationRoleName(role);
        checkValidationRoleCode(role);
        checkValidationRoleAssociationExists();
    }

    @Test
    public void testCountUsersWithRole() {
        assertEquals(Integer.valueOf(0), service.countUsersWithRole(0));
        assertEquals(Integer.valueOf(2), service.countUsersWithRole(1));
        assertEquals(Integer.valueOf(1), service.countUsersWithRole(2));
        assertEquals(Integer.valueOf(0), service.countUsersWithRole(3));
    }

    private void checkValidationRoleName(Role role) {
        Throwable thrown = catchThrowable(() -> {
            RoleForm roleForm = newRole();
            roleForm.setCode("newCode");
            service.create(newRole());
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueRole", thrown.getMessage());

        thrown = catchThrowable(() -> {
            role.setName("user");
            service.update(form(role));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueRole", thrown.getMessage());

        role.setName("test-name");
    }

    private void checkValidationRoleCode(Role role) {
        Throwable thrown = catchThrowable(() -> {
            RoleForm roleForm = newRole();
            roleForm.setName("newName");
            service.create(roleForm);
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueRole", thrown.getMessage());

        thrown = catchThrowable(() -> {
            role.setCode("test");
            service.update(form(role));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueRole", thrown.getMessage());

        role.setCode("test-code");
    }

    private void checkValidationRoleAssociationExists() {
        Throwable thrown = catchThrowable(() -> service.delete(1));
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.usernameWithSuchRoleExists", thrown.getMessage());
        thrown = catchThrowable(() -> service.delete(100));
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.accountTypeWithSuchRoleExists", thrown.getMessage());
        thrown = catchThrowable(() -> service.delete(103));
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.organizationWithSuchRoleExists", thrown.getMessage());
        thrown = catchThrowable(() -> service.delete(105));
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.clientWithSuchRoleExists", thrown.getMessage());
    }

    /**
     * Проверка, что получение роли по несуществующему идентификатору приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getRoleByNotExistsId() {
        service.getById(-1);
    }

    /**
     * Проверка, что удаление роли по несуществующему идентификатору приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteRoleByNotExistsId() {
        service.delete(-1);
    }

    /**
     * Проверка, что при некорректно введеном уровне пользователя в критерии, не последует ошибки
     * и при этом возвращаемый список ролей будет пуст
     */
    @Test
    public void findAllRolesWithBadUserLevelTest() {
        RoleCriteria criteria = new RoleCriteria();
        criteria.setUserLevel("wrong");
        assertTrue(service.findAll(criteria).isEmpty());
    }

    /**
     * Проверка, что при заданном критерии по уровню пользователя, будет возвращено корректное число ролей
     * Также проверяется, что поиск не чувствителен к регистру
     */
    @Test
    public void findAllRolesByUserLevel() {
        RoleCriteria criteria = new RoleCriteria();
        criteria.setUserLevel("federal");
        assertThat(service.findAll(criteria).getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllGroupBySystem() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("testRoleCode");
        roleForm.setName("testRoleName");
        roleForm.setDescription("testRoleDes");
        roleForm.setSystemCode("system1");
        Role role = service.create(roleForm);

        RoleCriteria criteria = new RoleCriteria();
        criteria.setName("testRoleName");
        criteria.setUserLevel("NOT_SET");
        criteria.setGroupBySystem(true);

        Page<Role> roles = service.findAll(criteria);

        assertEquals(2, roles.getContent().size());
        assertTrue(roles.getContent().stream().anyMatch(r -> r.getName().equals("system1")));
        assertTrue(roles.getContent().stream().anyMatch(r -> r.getName().equals("testRoleName")));

        service.delete(role.getId());
    }

    @Test
    public void findAllForForm() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("testRoleCode");
        roleForm.setName("testRoleName");
        roleForm.setDescription("testRoleDes");
        roleForm.setSystemCode("system1");
        Role role = service.create(roleForm);

        RoleCriteria criteria = new RoleCriteria();
        criteria.setName("testRoleName");
        criteria.setUserLevel("NOT_SET");
        criteria.setForForm(true);
        Page<Role> roles = service.findAll(criteria);

        assertEquals(1, roles.getContent().size());
        assertTrue(roles.getContent().stream().anyMatch(r -> r.getName().equals("testRoleName")));

        criteria = new RoleCriteria();
        criteria.setName("testRoleName");
        criteria.setForForm(true);

        roles = service.findAll(criteria);

        assertEquals(1, roles.getContent().size());
        assertTrue(roles.getContent().stream().anyMatch(r -> r.getName().equals("testRoleName")));

        service.delete(role.getId());
    }

    @Test
    public void findAllByOrgDependency() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("testRoleCode");
        roleForm.setName("testRoleName");
        roleForm.setDescription("testRoleDes");
        roleForm.setSystemCode("system1");
        Role role = service.create(roleForm);

        RoleCriteria criteria = new RoleCriteria();
        criteria.setOnOrgDependency(true);
        List<Integer> orgRoles = new ArrayList<>();
        orgRoles.add(role.getId());
        criteria.setOrgRoles(orgRoles);
        Page<Role> roles = service.findAll(criteria);

        assertEquals(1, roles.getContent().size());
        assertTrue(roles.getContent().stream().anyMatch(r -> r.getName().equals("testRoleName")));

        service.delete(role.getId());
    }

    @Test
    public void findAllBySystem() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("testRoleCode");
        roleForm.setName("testRoleName");
        roleForm.setDescription("testRoleDes");
        roleForm.setSystemCode("system1");
        Role role = service.create(roleForm);

        RoleCriteria criteria = new RoleCriteria();
        List<String> systemCodes = new ArrayList<>();
        systemCodes.add("system1");
        criteria.setSystemCodes(systemCodes);
        Page<Role> roles = service.findAll(criteria);

        assertEquals(1, roles.getContent().size());
        assertTrue(roles.getContent().stream().anyMatch(r -> r.getName().equals("testRoleName")));

        service.delete(role.getId());
    }

    private static RoleForm newRole() {
        RoleForm role = new RoleForm();
        role.setName("test-name");
        role.setCode("test-code");
        role.setDescription("test-desc");
        List<String> permissions = new ArrayList<>();
        permissions.add("test");
        role.setPermissions(permissions);
        return role;
    }

    private RoleForm form(Role role) {
        RoleForm form = new RoleForm();
        form.setId(role.getId());
        form.setName(role.getName());
        form.setCode(role.getCode());
        form.setDescription(role.getDescription());
        form.setPermissions(role.getPermissions().stream().map(Permission::getCode).collect(Collectors.toList()));
        return form;
    }

}
