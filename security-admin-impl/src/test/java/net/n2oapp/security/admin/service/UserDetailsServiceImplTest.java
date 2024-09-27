package net.n2oapp.security.admin.service;

import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.impl.repository.AccountRepository;
import net.n2oapp.security.admin.impl.service.UserDetailsServiceImpl;
import net.n2oapp.security.admin.impl.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тест сервиса получения данных о пользователе
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
@EnableTestcontainersPg
public class UserDetailsServiceImplTest {

    @Autowired
    private UserDetailsServiceImpl detailsService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void loadUserDetailsTest() {
        User userDetails = detailsService.loadUserDetails(userDetailsToken());
        assertEquals("testUserName", userDetails.getUsername());
        assertEquals("testName", userDetails.getName());
        assertEquals("test@test.test", userDetails.getEmail());
        assertEquals("testSurname", userDetails.getSurname());
        // todo SECURITY-396 UserDetailsServiceImpl.loadUserDetails на данный момент не отдает роли
//        assertEquals(2, userDetails.getRoles().size());
        assertEquals(2, accountRepository.findByUser_Id(userDetails.getId()).get(0).getRoleList().size());
        userService.delete(userDetails.getId());

        detailsService.setCreateUser(false);
        Throwable thrown = catchThrowable(() -> detailsService.loadUserDetails(userDetailsToken()));
        assertEquals("User testName testSurname doesn't registered in system", thrown.getMessage());

        UserForm userForm = createUserForm();

        User user = userService.create(userForm);

        userDetails = detailsService.loadUserDetails(userDetailsToken());

        assertEquals("testUserName", userDetails.getUsername());
        assertEquals("testName", userDetails.getName());
        assertEquals("test@test.test", userDetails.getEmail());
        assertEquals("testSurname", userDetails.getSurname());
// todo SECURITY-396 UserDetailsServiceImpl.loadUserDetails на данный момент не отдает роли
//        assertEquals(2, userDetails.getRoles().size());
        assertEquals(2, accountRepository.findByUser_Id(userDetails.getId()).get(0).getRoleList().size());

        userService.delete(userDetails.getId());
    }

    private UserForm createUserForm() {
        UserForm userForm = new UserForm();
        userForm.setUsername("testUserName");
        userForm.setEmail("test@test.test");
        userForm.setPassword("1234ABCabc,");
        userForm.setPasswordCheck(userForm.getPassword());
        userForm.setDepartmentId(1);
        userForm.setName("name");
        userForm.setSurname("surname");
        userForm.setPatronymic("patronymic");
        userForm.setRegionId(1);
        userForm.setSnils("124-985-753 00");
        userForm.setOrganizationId(1);
        return userForm;
    }

    private UserDetailsToken userDetailsToken() {
        UserDetailsToken token = new UserDetailsToken();
        token.setExtUid("21c4d4ed-add5-4d3d-833b-9a02fe018e77");
        token.setExternalSystem("testSystem");
        token.setUsername("testUserName");
        token.setName("testName");
        token.setSurname("testSurname");
        token.setEmail("test@test.test");
        token.setRoleNames(Lists.newArrayList("code1", "code2"));
        return token;
    }
}
