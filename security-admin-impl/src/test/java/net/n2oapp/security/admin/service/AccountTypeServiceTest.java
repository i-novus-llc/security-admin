package net.n2oapp.security.admin.service;

import net.n2oapp.platform.test.autoconfigure.pg.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.criteria.AccountTypeCriteria;
import net.n2oapp.security.admin.api.model.AccountType;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.model.UserStatus;
import net.n2oapp.security.admin.api.service.AccountTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Тест сервиса типов аккаунтов
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
public class AccountTypeServiceTest {

    @Autowired
    private AccountTypeService service;

    @Test
    public void testFindAll() {
        AccountTypeCriteria criteria = new AccountTypeCriteria();
        criteria.setName("testAccountTypeName");
        criteria.setUserLevel("PERSONAL");
        Page<AccountType> accountTypes = service.findAll(criteria);
        assertEquals(1L, accountTypes.getTotalElements());
        assertEquals(1, accountTypes.getContent().get(0).getId());
        assertEquals("testAccountTypeCode", accountTypes.getContent().get(0).getCode());
        assertEquals("testAccountTypeName", accountTypes.getContent().get(0).getName());
        assertEquals("testDescription", accountTypes.getContent().get(0).getDescription());
        assertEquals(UserLevel.PERSONAL, accountTypes.getContent().get(0).getUserLevel());
        assertEquals(UserStatus.REGISTERED, accountTypes.getContent().get(0).getStatus());
    }

    @Test
    public void testFindById() {
        AccountType accountType = service.findById(1);
        assertEquals(1, accountType.getId());
        assertEquals("testAccountTypeCode", accountType.getCode());
        assertEquals("testAccountTypeName", accountType.getName());
        assertEquals("testDescription", accountType.getDescription());
        assertEquals(UserLevel.PERSONAL, accountType.getUserLevel());
        assertEquals(UserStatus.REGISTERED, accountType.getStatus());
    }

    @Test
    public void testCreate() {
        assertEquals(1L, service.findAll(new AccountTypeCriteria()).getTotalElements());

        AccountType newAccountType = new AccountType();
        newAccountType.setCode("testCode22");
        newAccountType.setName("testName2");
        newAccountType.setStatus(UserStatus.REGISTERED);
        newAccountType.setUserLevel(UserLevel.REGIONAL);
        newAccountType.setDescription("testDescription2");
        newAccountType.setRoleIds(Arrays.asList(100));
        newAccountType.setOrgRoleIds(Arrays.asList(100, 101));
        service.create(newAccountType);

        assertEquals(2L, service.findAll(new AccountTypeCriteria()).getTotalElements());

        AccountType result = service.findById(2);
        assertEquals(2, result.getId());
        assertEquals("testName2", result.getName());
        assertEquals("testCode22", result.getCode());
        assertEquals("testDescription2", result.getDescription());
        assertEquals(UserStatus.REGISTERED, result.getStatus());
        assertEquals(UserLevel.REGIONAL, result.getUserLevel());
        assertEquals(100, result.getRoles().get(0).getId());
        assertEquals(100, result.getOrgRoles().get(0).getId());
        assertEquals(101, result.getOrgRoles().get(1).getId());

        service.delete(2);
    }

    @Test
    public void testUpdate() {
        AccountType newAccountType = new AccountType();
        newAccountType.setCode("testCode2");
        newAccountType.setName("testName2");
        newAccountType.setStatus(UserStatus.REGISTERED);
        newAccountType.setUserLevel(UserLevel.REGIONAL);
        newAccountType.setDescription("testDescription2");

        Integer id = service.create(newAccountType).getId();

        newAccountType.setId(id);
        newAccountType.setCode("testCode22");
        newAccountType.setName("testName22");
        newAccountType.setStatus(UserStatus.AWAITING_MODERATION);
        newAccountType.setUserLevel(UserLevel.ORGANIZATION);
        newAccountType.setDescription("testDescription22");
        AccountType result = service.update(newAccountType);

        assertEquals(id, result.getId());
        assertEquals("testCode22", result.getCode());
        assertEquals("testName22", result.getName());
        assertEquals(UserStatus.AWAITING_MODERATION, result.getStatus());
        assertEquals(UserLevel.ORGANIZATION, result.getUserLevel());
        assertEquals("testDescription22", result.getDescription());

        service.delete(id);
    }

    @Test
    public void testDelete() {
        AccountType newAccountType = new AccountType();
        newAccountType.setCode("testCode2");
        newAccountType.setName("testName2");
        newAccountType.setStatus(UserStatus.AWAITING_MODERATION);
        newAccountType.setUserLevel(UserLevel.REGIONAL);
        newAccountType.setDescription("testDescription2");

        Integer id = service.create(newAccountType).getId();
        Long before = service.findAll(new AccountTypeCriteria()).getTotalElements();
        service.delete(id);
        Long after = service.findAll(new AccountTypeCriteria()).getTotalElements();
        assertEquals(1L, before - after);
    }
}
