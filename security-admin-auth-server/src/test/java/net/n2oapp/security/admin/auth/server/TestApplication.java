package net.n2oapp.security.admin.auth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.i_novus.ms.audit.client.autoconfigure.AuditClientAutoConfiguration;
import ru.i_novus.ms.audit.client.autoconfigure.AuditSimpleClientAutoConfiguration;
import ru.inovus.ms.rdm.sync.RdmClientSyncAutoConfiguration;

import java.util.List;
import java.util.Map;

@SpringBootApplication(exclude = {
        AuditClientAutoConfiguration.class,
        LiquibaseAutoConfiguration.class,
        AuditSimpleClientAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        RdmClientSyncAutoConfiguration.class})
@RestController
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @PostMapping("/public/keycloak_mock/token")
    public Map<String, Object> tokenEndpoint() {
        return Map.of(
                "access_token", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImhZeWZ4VkRrY2hOOXdYdUxMalNMZTVrUTJFVXJXNE1yIn0.eyJyb2xlcyI6WyJhY2Nlc3MuYWRtaW4iLCJhZG1pbl9jb25maWciLCJhcmNoaXZlIiwiYWRtaW5faW50ZWdyYXRpb24iLCJuc2lfYWRtaW4iLCJtb25pdG9yaW5nX2FkbSIsIlJPTEVfMTYwIiwiUk9MRV8xNjQiLCJhZG1pbl9hdWRpdCIsIlJPTEVfMTY5IiwiVEVTVF9ST0xFIiwiTUZBIDExMTUiLCLQnNCk0JAgNCIsItCc0KTQkCAzMyIsImFkbWluX2FzZHBlIiwiUk9MRV8xODgiXSwiY2xpZW50X2lkIjoiYWRtaW4td2ViIiwic2lkIjoiMTFBMTJENkFGMTE3REE4Rjg2QUU0MDk4NUVGRTE3OTgiLCJwYXRyb255bWljIjoi0JLQsNGB0LjQu9GM0LXQstC40YciLCJ1c2VyTGV2ZWwiOm51bGwsInN5c3RlbXMiOlsiY29uZmlnIiwiYXJjaGl2ZSIsImludGVncmF0aW9uIiwicmRtIiwibW9uaXRvcmluZyIsInNhZmVraWRzIiwibWZhIiwiYXVkaXQiLCJhY2Nlc3MiLCJhc2RwZSJdLCJzdXJuYW1lIjoi0KHQuNC70LXRiNC40L0iLCJvcmdhbml6YXRpb24iOm51bGwsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdLCJuYW1lIjoi0JDQvdC00YDQtdC5IiwiZGVwYXJ0bWVudCI6bnVsbCwicmVnaW9uIjpudWxsLCJlbWFpbCI6InJnYWxpbmFAaS1ub3Z1cy5ydSIsImp0aSI6Ijk2ODZjMTQ5LTY0M2MtNDg2NS1hNDBjLTZiYmM4NTgwNjYwYiIsInVzZXJuYW1lIjoiYWRtaW4ifQ",
                "expires_in", "99999",
                "scope", "read write"
        );
    }

    @GetMapping(value = "/public/keycloak_mock/userinfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> userInfoEndpoint() {
        return Map.of(
                "username", "testUser",
                "email", "testEmail",
                "name", "testName",
                "surname", "testSurname",
                "patronymic", "testPatronymic",
                "roles", List.of("testRoleCode1", "testRoleCode2")
        );
    }
}
