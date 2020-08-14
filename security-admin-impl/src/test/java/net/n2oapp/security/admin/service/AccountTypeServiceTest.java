package net.n2oapp.security.admin.service;

import net.n2oapp.security.admin.api.criteria.AccountTypeCriteria;
import net.n2oapp.security.admin.api.model.AccountType;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.model.UserStatus;
import net.n2oapp.security.admin.api.service.AccountTypeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Тест сервиса типов аккаунтов
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
public class AccountTypeServiceTest {

    @Autowired
    private AccountTypeService service;

    @Test
    public void testFindAll() {
        AccountTypeCriteria criteria = new AccountTypeCriteria();
        Page<AccountType> accountTypes = service.findAll(criteria);
        assertThat(accountTypes.getTotalElements(), is(1L));
        assertThat(accountTypes.getContent().get(0).getId(), is(1));
        assertThat(accountTypes.getContent().get(0).getCode(), is("testAccountTypeCode"));
        assertThat(accountTypes.getContent().get(0).getName(), is("testAccountTypeName"));
        assertThat(accountTypes.getContent().get(0).getDescription(), is("testDescription"));
        assertThat(accountTypes.getContent().get(0).getUserLevel(), is(UserLevel.PERSONAL));
        assertThat(accountTypes.getContent().get(0).getStatus(), is(UserStatus.REGISTERED));
    }

    @Test
    public void testFindById() {
        AccountType accountType = service.findById(1);
        assertThat(accountType.getId(), is(1));
        assertThat(accountType.getCode(), is("testAccountTypeCode"));
        assertThat(accountType.getName(), is("testAccountTypeName"));
        assertThat(accountType.getDescription(), is("testDescription"));
        assertThat(accountType.getUserLevel(), is(UserLevel.PERSONAL));
        assertThat(accountType.getStatus(), is(UserStatus.REGISTERED));
    }

    @Test
    public void testCreate() {
        assertThat(service.findAll(new AccountTypeCriteria()).getTotalElements(), is(1L));

        AccountType newAccountType = new AccountType();
        newAccountType.setCode("testCode22");
        newAccountType.setName("testName2");
        newAccountType.setStatus(UserStatus.REGISTERED);
        newAccountType.setUserLevel(UserLevel.REGIONAL);
        newAccountType.setDescription("testDescription2");
        newAccountType.setRoleIds(Arrays.asList(100));
        newAccountType.setOrgRoleIds(Arrays.asList(100, 101));
        service.create(newAccountType);

        assertThat(service.findAll(new AccountTypeCriteria()).getTotalElements(), is(2L));

        AccountType result = service.findById(2);
        assertThat(result.getId(), is(2));
        assertThat(result.getName(), is("testName2"));
        assertThat(result.getCode(), is("testCode22"));
        assertThat(result.getDescription(), is("testDescription2"));
        assertThat(result.getStatus(), is(UserStatus.REGISTERED));
        assertThat(result.getUserLevel(), is(UserLevel.REGIONAL));
        assertThat(result.getRoles().get(0).getId(), is(100));
        assertThat(result.getOrgRoles().get(0).getId(), is(100));
        assertThat(result.getOrgRoles().get(1).getId(), is(101));

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

        assertThat(result.getId(), is(id));
        assertThat(result.getCode(), is("testCode22"));
        assertThat(result.getName(), is("testName22"));
        assertThat(result.getStatus(), is(UserStatus.AWAITING_MODERATION));
        assertThat(result.getUserLevel(), is(UserLevel.ORGANIZATION));
        assertThat(result.getDescription(), is("testDescription22"));

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
        assertThat(before - after, is(1L));
    }
}
