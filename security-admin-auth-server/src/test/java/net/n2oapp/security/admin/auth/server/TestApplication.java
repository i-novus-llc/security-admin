package net.n2oapp.security.admin.auth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.inovus.ms.rdm.sync.RdmClientSyncAutoConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootApplication(exclude = {
        LiquibaseAutoConfiguration.class,
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
    public Map<String, Object> tokenEndpoint(HttpServletResponse response, HttpServletRequest request) throws IOException {
        if (request.getParameter("code") != null && request.getParameter("code").equals("needError"))
            response.setStatus(401);
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

    @PostMapping("/public/esia_mock/token")
    public Map<String, Object> esiaTokenEndpoint(HttpServletResponse response, HttpServletRequest request) throws IOException {
        return Map.of(
                "access_token", "eyJ2ZXIiOjEsInR5cCI6IkpXVCIsInNidCI6ImFjY2VzcyIsImFsZyI6IlJTMjU2In0.eyJuYmYiOjE2MTM1NjA0MjMsInNjb3BlIjoic25pbHM_b2lkPTEwMDAyOTk2NTYgZnVsbG5hbWU_b2lkPTEwMDAyOTk2NTYgdXNyX29yZz9vaWQ9MTAwMDI5OTY1NiBvcGVuaWQiLCJpc3MiOiJodHRwOlwvXC9lc2lhLXBvcnRhbDEudGVzdC5nb3N1c2x1Z2kucnVcLyIsInVybjplc2lhOnNpZCI6ImY3MDVhNDFkLTRkNzEtNDhlMC04ZTJjLTQyNTgxZTdmNjYwMiIsInVybjplc2lhOnNial9pZCI6MTAwMDI5OTY1NiwiZXhwIjoxNjEzNTY0MDIzLCJpYXQiOjE2MTM1NjA0MjMsImNsaWVudF9pZCI6IkVQTVBTX0VQQkEifQ.nkg6JerduBjGCE-ZNCCqDX-KHcAMPHCVdLSsFWhb_Qkb83cqbCL4LCrIAqYd-gnIees5JL9jveTLPpo-xS8i0qCDyShNGYQZg8LUS3RW7ic7vMG6UH8IyDE7AuhTi9ab7lfD3VqkwFJ2cvAo4TCgms6_ivC6B-Y49pwi3-R0B8uBOz5UrEYjrTCqYeL9NqwIyMMCOkAdS3U7Kj2SmgdMTwRV82mqbhMCQjCT3GzdkqOM8w9FHaP_pEC2xjimmvA0sugZ8eUrQD8hwzed5fYpsBiSr7eSx8_I4R-6Vh4W-RO71hVOrb_fBCYQNfmpe3Trs9zQkzBJw2Nmg7sOVFMEfw",
                "expires_in", "99999",
                "scope", "read write"
        );
    }

    @GetMapping(value = "/public/esia_mock/userinfo/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> userInfoEndpoint(@PathVariable String id) {
        return Map.of(
                "snils", "000-000-600 04",
                "username", "testUser",
                "email", "testEmail",
                "name", "testName",
                "surname", "testSurname",
                "patronymic", "testPatronymic",
                "roles", List.of("testRoleCode1", "testRoleCode2")
        );
    }
}
