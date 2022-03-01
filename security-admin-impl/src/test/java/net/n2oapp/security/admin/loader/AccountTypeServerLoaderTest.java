package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.criteria.AccountTypeCriteria;
import net.n2oapp.security.admin.api.model.AccountType;
import net.n2oapp.security.admin.api.model.UserStatus;
import net.n2oapp.security.admin.impl.loader.AccountTypeServerLoader;
import net.n2oapp.security.admin.impl.service.AccountTypeServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static net.n2oapp.security.admin.loader.builder.AccountTypeBuilder.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        assertThat(accountTypes.size(), is(7));
        AccountType accountTypeResponse = accountTypes.stream().filter(accountType -> accountType.getCode().equals("code1")).findFirst().get();
        assertThat(accountTypeResponse.getCode(), is("code1"));
        assertThat(accountTypeResponse.getName(), is("name1"));
        assertThat(accountTypeResponse.getDescription(), is("description1"));
        assertThat(accountTypeResponse.getStatus(), is(UserStatus.AWAITING_MODERATION));
        assertThat(accountTypeResponse.getRoles().get(0).getCode(), is("code1"));
        assertThat(accountTypeResponse.getRoles().size(), is(1));
        assertThat(accountTypeResponse.getOrgRoles().get(0).getCode(), is("102"));
        assertThat(accountTypeResponse.getOrgRoles().size(), is(1));

        accountTypeResponse = accountTypes.stream().filter(accountType -> accountType.getCode().equals("code3")).findFirst().get();
        assertThat(accountTypeResponse.getRoles().get(0).getCode(), is("code1"));
        assertThat(accountTypeResponse.getRoles().size(), is(1));
        assertThat(accountTypeResponse.getOrgRoles().size(), is(0));

        accountTypeResponse = accountTypes.stream().filter(accountType -> accountType.getCode().equals("code5")).findFirst().get();
        assertThat(accountTypeResponse.getRoles().size(), is(0));
        assertThat(accountTypeResponse.getOrgRoles().get(0).getCode(), is("102"));
        assertThat(accountTypeResponse.getOrgRoles().size(), is(1));

        accountTypeResponse = accountTypes.stream().filter(accountType -> accountType.getCode().equals("code4")).findFirst().get();
        assertThat(accountTypeResponse.getRoles().size(), is(0));
        assertThat(accountTypeResponse.getOrgRoles().size(), is(0));

        accountTypeResponse = accountTypes.stream().filter(accountType -> accountType.getCode().equals("code6")).findFirst().get();
        assertThat(accountTypeResponse.getRoles().get(0).getCode(), is("code1"));
        assertThat(accountTypeResponse.getRoles().size(), is(1));
        assertThat(accountTypeResponse.getOrgRoles().get(0).getCode(), is("code1"));
        assertThat(accountTypeResponse.getOrgRoles().get(1).getCode(), is("102"));
        assertThat(accountTypeResponse.getOrgRoles().size(), is(2));

        accountTypeResponse = accountTypes.stream().filter(accountType -> accountType.getCode().equals("testAccountTypeCode")).findFirst().get();
        assertThat(accountTypeResponse.getCode(), is("testAccountTypeCode"));
        assertThat(accountTypeResponse.getName(), is("testAccountTypeName"));
        assertThat(accountTypeResponse.getStatus(), is(UserStatus.REGISTERED));
    }
}
