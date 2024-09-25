package net.n2oapp.security.admin.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
import net.n2oapp.security.admin.api.criteria.AccountCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Тест сервиса управления аккаунтами
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
@EnableTestcontainersPg
public class AccountServiceImplTest {

    @Autowired
    private AccountService service;

    @Test
    public void testUp() {
        assertNotNull(service);
    }

    @Test
    public void findAllTest() {
        AccountCriteria criteria = new AccountCriteria();

        List<Account> accounts = service.findAll(criteria).getContent();
        assertEquals(2, accounts.size());
        assertEquals(1, (int) accounts.get(0).getId());
        assertEquals(2, (int) accounts.get(1).getId());

        criteria.setUserId(2);
        accounts = service.findAll(criteria).getContent();
        assertEquals(1, accounts.size());
        assertEquals(2, (int) accounts.get(0).getId());
        assertEquals(2, (int) accounts.get(0).getUserId());
    }

    @Test
    public void crudTest() {
        Integer accountId = create();
        update(accountId);
        delete(accountId);
    }

    @Test
    public void getAccountByNotExistsIdTest() {
        assertThrows(UserException.class, () -> service.getById(-1));
    }

    @Test
    public void updateAccountByNotExistsIdTest() {
        Account account = new Account();
        account.setId(-1);
        assertThrows(UserException.class, () -> service.update(account));
    }

    @Test
    public void deleteAccountByNotExistsIdTest() {
        assertThrows(UserException.class, () -> service.delete(-1));
    }

    private Integer create() {
        Account newAccount = createAccount();
        Integer createdAccountId = service.create(newAccount).getId();
        Account account = service.getById(createdAccountId);

        assertEquals(newAccount.getName(), account.getName());
        assertEquals(newAccount.getIsActive(), account.getIsActive());
        assertEquals(newAccount.getUserId(), account.getUserId());
        assertEquals(newAccount.getUserLevel(), account.getUserLevel());

        assertNotNull(account.getDepartment());
        assertEquals("test_name", account.getDepartment().getName());
        assertEquals("test_code", account.getDepartment().getCode());

        assertNotNull(account.getOrganization());
        assertEquals("test_code2", account.getOrganization().getCode());
        assertEquals("test_short_name2", account.getOrganization().getShortName());
        assertEquals("test_full_name2", account.getOrganization().getFullName());
        assertEquals("test_inn2", account.getOrganization().getInn());

        assertNotNull(account.getRegion());
        assertEquals("test_name", account.getRegion().getName());
        assertEquals("test_code", account.getRegion().getCode());
        assertEquals("test_okato", account.getRegion().getOkato());

        assertEquals(1, account.getRoles().size());
        assertEquals("admin", account.getRoles().get(0).getName());
        assertEquals("code2", account.getRoles().get(0).getCode());
        assertEquals("description2", account.getRoles().get(0).getDescription());

        return createdAccountId;
    }

    private void update(Integer accountId) {
        Account updatedAccount = new Account();
        updatedAccount.setId(accountId);
        updatedAccount.setName("new_name");
        updatedAccount.setIsActive(true);
        updatedAccount.setUserLevel(UserLevel.ORGANIZATION);
        Organization org = new Organization();
        org.setId(3);
        updatedAccount.setOrganization(org);
        Role role = new Role();
        role.setId(1);
        updatedAccount.setRoles(Collections.singletonList(role));

        service.update(updatedAccount);
        Account account = service.getById(accountId);

        assertEquals(updatedAccount.getName(), account.getName());
        assertEquals(updatedAccount.getIsActive(), account.getIsActive());
        assertEquals(updatedAccount.getUserLevel(), account.getUserLevel());

        assertNull(account.getDepartment());

        assertNotNull(account.getOrganization());
        assertEquals("test_code3", account.getOrganization().getCode());
        assertEquals("test_short_name3", account.getOrganization().getShortName());
        assertEquals("test_full_name3", account.getOrganization().getFullName());
        assertEquals("test_inn3", account.getOrganization().getInn());

        assertNull(account.getRegion());

        assertEquals(1, account.getRoles().size());
        assertEquals("user", account.getRoles().get(0).getName());
        assertEquals("code1", account.getRoles().get(0).getCode());
        assertEquals("description1", account.getRoles().get(0).getDescription());
    }

    private void delete(Integer accountId) {
        assertNotNull(service.getById(accountId));
        service.delete(accountId);
        assertThrows(UserException.class, () -> service.getById(accountId));
    }


    private Account createAccount() {
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
