//package net.n2oapp.security.service;
//
//import net.n2oapp.security.TestApplication;
//import net.n2oapp.security.admin.api.model.Permission;
//import net.n2oapp.security.admin.api.model.Role;
//import net.n2oapp.security.admin.api.service.RoleService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.catchThrowable;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
///**
// * Тест сервиса управления ролями пользователя
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@TestPropertySource("classpath:test.properties")
//@AutoConfigureTestDatabase
//public class RoleServiceTest {
//
//
//    @Autowired
//    private RoleService service;
//
//
//    @Test
//    public void testUp() throws Exception {
//        assertNotNull(service);
//    }
//
//    @Test
//    public void checkRoleValidations() {
//        Role role = service.create(newRole());
//        checkValidationRoleName(role);
//        checkValidationRoleExists(1);
//    }
//
//    private void checkValidationRoleName(Role role) {
//        Throwable thrown = catchThrowable(() -> {
//            service.create(newRole());
//        });
//        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
//        assertEquals("Role with such name already exists", thrown.getMessage());
//        thrown = catchThrowable(() -> {
//            role.setName("user");
//            service.update(role);
//        });
//        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
//        assertEquals("Role with such name already exists", thrown.getMessage());
//        role.setName("adminAdmin");
//    }
//
//    private void checkValidationRoleExists(Integer id) {
//        Throwable thrown = catchThrowable(() -> {
//            service.delete(id);
//        });
//        assertThat(thrown).isInstanceOf(IllegalAccessError.class);
//        assertEquals("Deleting is not possible, since there are users with such role", thrown.getMessage());
//    }
//
//    private static Role newRole() {
//        Role role = new Role();
//        role.setName("userUser");
//        role.setCode("code1");
//        role.setDescription("description1");
//        List<Permission> permissions = new ArrayList<>();
//        Permission permission = new Permission();
//        permission.setId(1);
//        permission.setName("permission");
//        permissions.add(permission);
//        role.setPermissions(permissions);
//        return role;
//    }
//
//}
