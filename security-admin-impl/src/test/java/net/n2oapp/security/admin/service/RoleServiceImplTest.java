package net.n2oapp.security.admin.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.base.UserRoleServiceTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.*;

/**
 * Тест сервиса управления ролями пользователя
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
public class RoleServiceImplTest extends UserRoleServiceTestBase {

    @Test
    public void testUp() throws Exception {
        assertNotNull(roleService);
    }

    @Test
    @Transactional
    @Rollback
    public void checkRoleValidations() {
        Role role = roleService.create(newRole());
        checkValidationRoleName(role);
        checkValidationRoleCode(role);
        checkValidationRoleAssociationExists();
        checkSystemCodeValidation();
    }

    @Test
    public void testCountUsersWithRole() {
        assertEquals(Integer.valueOf(0), roleService.countUsersWithRole(0));
        assertEquals(Integer.valueOf(2), roleService.countUsersWithRole(1));
        assertEquals(Integer.valueOf(1), roleService.countUsersWithRole(2));
        assertEquals(Integer.valueOf(0), roleService.countUsersWithRole(3));
    }

    private void checkValidationRoleName(Role role) {
        Throwable thrown = catchThrowable(() -> {
            RoleForm roleForm = newRole();
            roleForm.setCode("newCode");
            roleService.create(newRole());
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueRole", thrown.getMessage());

        thrown = catchThrowable(() -> {
            role.setName("user");
            roleService.update(form(role));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueRole", thrown.getMessage());

        role.setName("test-name");
    }

    private void checkValidationRoleCode(Role role) {
        Throwable thrown = catchThrowable(() -> {
            RoleForm roleForm = newRole();
            roleForm.setName("newName");
            roleService.create(roleForm);
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueRole", thrown.getMessage());

        thrown = catchThrowable(() -> {
            role.setCode("test");
            roleService.update(form(role));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueRole", thrown.getMessage());

        role.setCode("test-code");
    }

    private void checkSystemCodeValidation() {
        Throwable thrown = catchThrowable(() -> {
            RoleForm roleForm = newRole();
            roleForm.setName("systemCodeTest");
            roleForm.setSystemCode("");
            service.create(roleForm);
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.systemNotExists", thrown.getMessage());

        thrown = catchThrowable(() -> {
            RoleForm roleForm = newRole();
            roleForm.setName("systemCodeTest");
            roleForm.setSystemCode("   ");
            service.create(roleForm);
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.systemNotExists", thrown.getMessage());

        thrown = catchThrowable(() -> {
            RoleForm roleForm = newRole();
            roleForm.setName("systemCodeTest");
            roleForm.setSystemCode("unexistingSystemCode");
            service.create(roleForm);
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.systemNotExists", thrown.getMessage());
    }

    private void checkValidationRoleAssociationExists() {
        Throwable thrown = catchThrowable(() -> roleService.delete(1));
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.usernameWithSuchRoleExists", thrown.getMessage());
        thrown = catchThrowable(() -> roleService.delete(100));
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.accountTypeWithSuchRoleExists", thrown.getMessage());
        thrown = catchThrowable(() -> roleService.delete(103));
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.organizationWithSuchRoleExists", thrown.getMessage());
        thrown = catchThrowable(() -> roleService.delete(105));
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.clientWithSuchRoleExists", thrown.getMessage());
    }

    /**
     * Проверка, что получение роли по несуществующему идентификатору приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getRoleByNotExistsId() {
        roleService.getById(-1);
    }

    /**
     * Проверка, что удаление роли по несуществующему идентификатору приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteRoleByNotExistsId() {
        roleService.delete(-1);
    }

    /**
     * Проверка, что при некорректно введеном уровне пользователя в критерии, не последует ошибки
     * и при этом возвращаемый список ролей будет пуст
     */
    @Test
    public void findAllRolesWithBadUserLevelTest() {
        RoleCriteria criteria = new RoleCriteria();
        criteria.setUserLevel("wrong");
        assertTrue(roleService.findAll(criteria).isEmpty());
    }

    /**
     * Проверка, что при заданном критерии по уровню пользователя, будет возвращено корректное число ролей
     * Также проверяется, что поиск не чувствителен к регистру
     */
    @Test
    public void findAllRolesByUserLevel() {
        RoleCriteria criteria = new RoleCriteria();
        criteria.setUserLevel("federal");
        assertThat(roleService.findAll(criteria).getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllGroupBySystem() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("testRoleCode");
        roleForm.setName("testRoleName");
        roleForm.setDescription("testRoleDes");
        roleForm.setSystemCode("system1");
        Role role = roleService.create(roleForm);

        RoleCriteria criteria = new RoleCriteria();
        criteria.setName("testRoleName");
        criteria.setUserLevel("NOT_SET");
        criteria.setGroupBySystem(true);

        Page<Role> roles = roleService.findAll(criteria);

        assertEquals(2, roles.getContent().size());
        assertTrue(roles.getContent().stream().anyMatch(r -> r.getName().equals("system1")));
        assertTrue(roles.getContent().stream().anyMatch(r -> r.getName().equals("testRoleName")));

        roleService.delete(role.getId());
    }

    @Test
    public void findAllForForm() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("testRoleCode");
        roleForm.setName("testRoleName");
        roleForm.setDescription("testRoleDes");
        roleForm.setSystemCode("system1");
        Role role = roleService.create(roleForm);

        RoleCriteria criteria = new RoleCriteria();
        criteria.setName("testRoleName");
        criteria.setUserLevel("NOT_SET");
        criteria.setForForm(true);
        Page<Role> roles = roleService.findAll(criteria);

        assertEquals(1, roles.getContent().size());
        assertTrue(roles.getContent().stream().anyMatch(r -> r.getName().equals("testRoleName")));

        criteria = new RoleCriteria();
        criteria.setName("testRoleName");
        criteria.setForForm(true);

        roles = roleService.findAll(criteria);

        assertEquals(1, roles.getContent().size());
        assertTrue(roles.getContent().stream().anyMatch(r -> r.getName().equals("testRoleName")));

        roleService.delete(role.getId());
    }

    @Test
    public void findAllByOrgDependency() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("testRoleCode");
        roleForm.setName("testRoleName");
        roleForm.setDescription("testRoleDes");
        roleForm.setSystemCode("system1");
        Role role = roleService.create(roleForm);

        RoleCriteria criteria = new RoleCriteria();
        criteria.setFilterByOrgRoles(true);
        List<Integer> orgRoles = new ArrayList<>();
        orgRoles.add(role.getId());
        criteria.setOrgRoles(orgRoles);
        Page<Role> roles = roleService.findAll(criteria);

        assertEquals(1, roles.getContent().size());
        assertTrue(roles.getContent().stream().anyMatch(r -> r.getName().equals("testRoleName")));

        roleService.delete(role.getId());
    }

    @Test
    public void findAllBySystem() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("testRoleCode");
        roleForm.setName("testRoleName");
        roleForm.setDescription("testRoleDes");
        roleForm.setSystemCode("system1");
        Role role = roleService.create(roleForm);

        RoleCriteria criteria = new RoleCriteria();
        List<String> systemCodes = new ArrayList<>();
        systemCodes.add("system1");
        criteria.setSystemCodes(systemCodes);
        Page<Role> roles = roleService.findAll(criteria);

        assertEquals(1, roles.getContent().size());
        assertTrue(roles.getContent().stream().anyMatch(r -> r.getName().equals("testRoleName")));

        roleService.delete(role.getId());
    }

}
