package net.n2oapp.security.admin;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.rest.api.*;
import net.n2oapp.security.admin.rest.impl.UserDetailsRestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import ru.inovus.ms.rdm.api.service.RefBookService;
import ru.inovus.ms.rdm.api.service.VersionService;
import ru.inovus.ms.rdm.sync.RdmClientSyncAutoConfiguration;
import ru.inovus.ms.rdm.sync.service.change_data.RdmChangeDataClient;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication(exclude = RdmClientSyncAutoConfiguration.class)
@EnableJaxRsProxyClient(
        classes = {RefBookService.class, VersionService.class, UserRestService.class, RoleRestService.class,
                PermissionRestService.class, ClientRestService.class, UserDetailsRestService.class,
                AccountTypeRestService.class, DepartmentRestService.class, UserLevelRestService.class},
        address = "http://localhost:${server.port}/api")
@EnableEmbeddedPg
public class TestApplication {

    @Autowired
    UserDetailsService userDetailsService;
    @MockBean
    private RdmChangeDataClient rdmChangeDataClient;

    @Bean
    public UserDetailsRestService UserDetailsRestService() {
        return new UserDetailsRestServiceImpl(userDetailsService);
    }

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}


