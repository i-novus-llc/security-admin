package net.n2oapp.security.admin.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Тест сервиса управления правами доступа
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
public class AccountServiceImplTest {

    @Autowired
    private AccountService service;

    @Test
    public void testUp() throws Exception {
        assertNotNull(service);
    }


    @Test
    public void createTest() {

    }

    @Test
    public void updateTest() {

    }

    @Test
    public void deleteTest() {
        Integer accountId = service.create(getAccount()).getId();
        assertNotNull(service.getById(accountId));
        service.delete(accountId);
        assertThrows(UserException.class, () -> service.getById(accountId));
    }

    @Test(expected = UserException.class)
    public void getAccountByNotExistsIdTest() {
        service.getById(-1);
    }

    @Test(expected = UserException.class)
    public void updateAccountByNotExistsIdTest() {
        Account account = new Account();
        account.setId(-1);
        service.update(account);
    }

    @Test(expected = UserException.class)
    public void deleteAccountByNotExistsIdTest() {
        service.delete(-1);
    }

    private Account getAccount() {
        Account account = new Account();
        account.setName("testAccount");
        account.setIsActive(false);
        account.setUserId(1);
        account.setUserLevel(UserLevel.REGIONAL);

        Region region = new Region();
        region.setId(1);
        account.setRegion(region);

        Department department = new Department();
        department.setId(1);
        account.setDepartment(department);

        Organization org = new Organization();
        org.setId(2);
        account.setOrganization(org);

        Role role = new Role();
        role.setId(2);
        account.setRoles(Collections.singletonList(role));

        return account;
    }
}
