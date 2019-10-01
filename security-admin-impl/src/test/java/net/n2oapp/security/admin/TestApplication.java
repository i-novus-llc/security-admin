package net.n2oapp.security.admin;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import ru.i_novus.ms.audit.client.AuditClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import ru.inovus.ms.rdm.service.api.DraftService;
import ru.inovus.ms.rdm.service.api.PublishService;
import ru.inovus.ms.rdm.service.api.RefBookService;
import ru.inovus.ms.rdm.service.api.VersionService;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication
@EnableJaxRsProxyClient(
        classes = {RefBookService.class, DraftService.class,
                PublishService.class, VersionService.class},
        address = "http://localhost:${server.port}/api")
@EnableEmbeddedPg
public class TestApplication {

    @MockBean
    AuditClient auditClient;

    @MockBean
    private VersionService versionService;
    @MockBean
    private RefBookService refBookService;
    @MockBean
    private DraftService draftService;
    @MockBean
    private PublishService publishService;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public SpringBus cxf() {
        return new SpringBus();
    }
}


