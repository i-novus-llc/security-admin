package net.n2oapp.security.admin.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.service.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Тест сервиса управления ролями пользователя
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class RoleServiceTest {


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
        checkValidationRoleExists(1);
    }

    private void checkValidationRoleName(Role role) {
        Throwable thrown = catchThrowable(() -> {
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
        role.setName("adminAdmin");
    }

    private void checkValidationRoleExists(Integer id) {
        Throwable thrown = catchThrowable(() -> {
            service.delete(id);
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.usernameWithSuchRoleExists", thrown.getMessage());
    }

    private static RoleForm newRole() {
        RoleForm role = new RoleForm();
        role.setName("user1");
        role.setCode("code1");
        role.setDescription("description1");
        List<Integer> permissions = new ArrayList<>();
        permissions.add(1);
        role.setPermissions(permissions);
        return role;
    }

    private RoleForm form(Role role) {
        RoleForm form = new RoleForm();
        form.setId(role.getId());
        form.setName(role.getName());
        form.setCode(role.getCode());
        form.setDescription(role.getDescription());
        form.setPermissions(role.getPermissions().stream().map(Permission::getId).collect(Collectors.toList()));
        return form;
    }

}
