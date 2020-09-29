package net.n2oapp.security.admin.rest;

import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.AccountType;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.model.UserStatus;
import net.n2oapp.security.admin.rest.api.AccountTypeRestService;
import net.n2oapp.security.admin.rest.api.criteria.AccountTypeRestCriteria;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.NotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Тест Rest сервиса управления типом аккаунта
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountTypeRestTest {

    @Autowired
    @Qualifier("accountTypeRestServiceJaxRsProxyClient")
    private AccountTypeRestService client;

    @Test
    @Order(1)
    public void testFindAll() {
        AccountTypeRestCriteria emptyCriteria = new AccountTypeRestCriteria();
        Page<AccountType> result = client.findAll(emptyCriteria);

        assertThat(result.getTotalElements(), is(2L));
        assertThat(result.getContent().get(0).getId(), is(1));
        assertThat(result.getContent().get(0).getCode(), is("testAccountType1"));
        assertThat(result.getContent().get(0).getName(), is("testName1"));
        assertThat(result.getContent().get(0).getDescription(), is("testDesc1"));
        assertThat(result.getContent().get(0).getUserLevel(), is(UserLevel.NOT_SET));
        assertThat(result.getContent().get(0).getStatus(), is(UserStatus.REGISTERED));

        assertThat(result.getContent().get(1).getId(), is(2));
        assertThat(result.getContent().get(1).getCode(), is("testAccountType2"));
        assertThat(result.getContent().get(1).getName(), is("testName2"));
        assertThat(result.getContent().get(1).getDescription(), is("testDesc2"));
        assertThat(result.getContent().get(1).getUserLevel(), is(UserLevel.FEDERAL));
        assertThat(result.getContent().get(1).getStatus(), is(UserStatus.AWAITING_MODERATION));

        AccountTypeRestCriteria criteria = new AccountTypeRestCriteria();
        criteria.setName("testName2");
        criteria.setUserLevel(UserLevel.FEDERAL.toString());

        result = client.findAll(criteria);

        assertThat(result.getTotalElements(), is(1L));
        assertThat(result.getContent().get(0).getId(), is(2));
        assertThat(result.getContent().get(0).getCode(), is("testAccountType2"));
        assertThat(result.getContent().get(0).getName(), is("testName2"));
        assertThat(result.getContent().get(0).getDescription(), is("testDesc2"));
        assertThat(result.getContent().get(0).getUserLevel(), is(UserLevel.FEDERAL));
        assertThat(result.getContent().get(0).getStatus(), is(UserStatus.AWAITING_MODERATION));

        criteria.setName("nonExistingName");
        result = client.findAll(criteria);
        assertThat(result.getTotalElements(), is(0L));

        criteria.setName("testName2");
        criteria.setUserLevel("nonExistingLevel");
        result = client.findAll(criteria);
        assertThat(result.getTotalElements(), is(0L));

    }

    @Test
    public void testCreate() {
        AccountType accountType = new AccountType();
        accountType.setCode("testCode3");
        accountType.setName("testName3");
        accountType.setDescription("testDesc3");
        accountType.setUserLevel(UserLevel.PERSONAL);
        accountType.setStatus(UserStatus.REGISTERED);

        AccountType result = client.create(accountType);
        assertThat(result.getCode(), is(accountType.getCode()));
        assertThat(result.getName(), is(accountType.getName()));
        assertThat(result.getDescription(), is(accountType.getDescription()));
        assertThat(result.getUserLevel(), is(accountType.getUserLevel()));
        assertThat(result.getStatus(), is(accountType.getStatus()));

        AccountTypeRestCriteria criteria = new AccountTypeRestCriteria();
        criteria.setName(accountType.getName());
        Page<AccountType> list = client.findAll(criteria);

        assertThat(list.getContent().size(), is(1));
        assertThat(list.getContent().get(0).getName(), is(accountType.getName()));
    }

    @Test
    public void testUpdateDelete() {
        AccountType accountType = new AccountType();
        accountType.setCode("testCode123");
        accountType.setName("testName123");
        accountType.setDescription("testDesc123");
        accountType.setUserLevel(UserLevel.PERSONAL);
        accountType.setStatus(UserStatus.REGISTERED);

        AccountType result = client.create(accountType);
        assertThat(result.getCode(), is(accountType.getCode()));
        assertThat(result.getName(), is(accountType.getName()));
        assertThat(result.getDescription(), is(accountType.getDescription()));
        assertThat(result.getUserLevel(), is(accountType.getUserLevel()));
        assertThat(result.getStatus(), is(accountType.getStatus()));

        accountType.setId(result.getId());
        accountType.setCode("testCode123123");
        accountType.setName("testName123123");
        accountType.setDescription("testDesc123123");
        accountType.setUserLevel(UserLevel.REGIONAL);
        accountType.setStatus(UserStatus.AWAITING_MODERATION);

        result = client.update(accountType);
        assertThat(result.getId(), is(accountType.getId()));
        assertThat(result.getCode(), is(accountType.getCode()));
        assertThat(result.getName(), is(accountType.getName()));
        assertThat(result.getDescription(), is(accountType.getDescription()));
        assertThat(result.getUserLevel(), is(accountType.getUserLevel()));
        assertThat(result.getStatus(), is(accountType.getStatus()));

        client.delete(result.getId());

        try {
            client.delete(result.getId());
            assert false;
        } catch (NotFoundException e) {
            assertThat(e.getMessage(), is("HTTP 404 Not Found"));
        } catch (Exception e) {
            assert false;
        }

    }
}
