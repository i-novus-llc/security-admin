package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.test.autoconfigure.pg.EnableEmbeddedPg;
import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.criteria.AccountTypeCriteria;
import net.n2oapp.security.admin.api.model.AccountType;
import net.n2oapp.security.admin.api.model.UserStatus;
import net.n2oapp.security.admin.impl.loader.AccountTypeServerLoader;
import net.n2oapp.security.admin.impl.service.AccountTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static net.n2oapp.security.admin.loader.builder.AccountTypeBuilder.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
public class AccountTypeServerLoaderTest {

    @Autowired
    private AccountTypeServiceImpl accountTypeService;

    @Autowired
    private AccountTypeServerLoader accountTypeLoader;

    @Test
    public void test() {
        List<AccountType> data = Arrays.asList(accountType1(), accountType2(), accountType3(), accountType4(), accountType5(), accountType6());
        accountTypeLoader.load(data, "accountType");

        List<AccountType> accountTypes = accountTypeService.findAll(new AccountTypeCriteria()).getContent();
        assertEquals(accountTypes.size(), 7);
        AccountType accountTypeResponse = accountTypes.stream().filter(accountType -> accountType.getCode().equals("code1")).findFirst().get();
        assertEquals("code1", accountTypeResponse.getCode());
        assertEquals("name1", accountTypeResponse.getName());
        assertEquals("description1", accountTypeResponse.getDescription());
        assertEquals(UserStatus.AWAITING_MODERATION, accountTypeResponse.getStatus());
        assertEquals("code1", accountTypeResponse.getRoles().get(0).getCode());
        assertEquals(1, accountTypeResponse.getRoles().size());
        assertEquals("102", accountTypeResponse.getOrgRoles().get(0).getCode());
        assertEquals(1, accountTypeResponse.getOrgRoles().size());

        accountTypeResponse = accountTypes.stream().filter(accountType -> accountType.getCode().equals("code3")).findFirst().get();
        assertEquals("code1", accountTypeResponse.getRoles().get(0).getCode());
        assertEquals(1, accountTypeResponse.getRoles().size());
        assertEquals(0, accountTypeResponse.getOrgRoles().size());

        accountTypeResponse = accountTypes.stream().filter(accountType -> accountType.getCode().equals("code5")).findFirst().get();
        assertEquals(0, accountTypeResponse.getRoles().size());
        assertEquals("102", accountTypeResponse.getOrgRoles().get(0).getCode());
        assertEquals(1, accountTypeResponse.getOrgRoles().size());

        accountTypeResponse = accountTypes.stream().filter(accountType -> accountType.getCode().equals("code4")).findFirst().get();
        assertEquals(0, accountTypeResponse.getRoles().size());
        assertEquals(0, accountTypeResponse.getOrgRoles().size());

        accountTypeResponse = accountTypes.stream().filter(accountType -> accountType.getCode().equals("code6")).findFirst().get();
        assertEquals("code1", accountTypeResponse.getRoles().get(0).getCode());
        assertEquals(1, accountTypeResponse.getRoles().size());
        assertEquals("code1", accountTypeResponse.getOrgRoles().get(0).getCode());
        assertEquals("102", accountTypeResponse.getOrgRoles().get(1).getCode());
        assertEquals(2, accountTypeResponse.getOrgRoles().size());

        accountTypeResponse = accountTypes.stream().filter(accountType -> accountType.getCode().equals("testAccountTypeCode")).findFirst().get();
        assertEquals("testAccountTypeCode", accountTypeResponse.getCode());
        assertEquals("testAccountTypeName", accountTypeResponse.getName());
        assertEquals(UserStatus.REGISTERED, accountTypeResponse.getStatus());
    }
}
