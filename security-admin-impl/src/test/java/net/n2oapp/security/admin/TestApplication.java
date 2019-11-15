package net.n2oapp.security.admin;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.platform.loader.autoconfigure.ClientLoaderConfigurer;
import net.n2oapp.platform.loader.client.ClientLoaderRunner;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.impl.loader.client.ClientLoaderImpl;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.inovus.ms.rdm.api.service.DraftService;
import ru.inovus.ms.rdm.api.service.PublishService;
import ru.inovus.ms.rdm.api.service.RefBookService;
import ru.inovus.ms.rdm.api.service.VersionService;

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

    @Configuration
    public static class LoaderConfiguration implements ClientLoaderConfigurer {
        @Override
        public void configure(ClientLoaderRunner clientLoaderRunner) {
            clientLoaderRunner
                    .add("", "sec", "clients", "data/clients.json", ClientLoaderImpl.class)
                    .add("", "sec", "systems", "data/systems.json", ClientLoaderImpl.class)
                    .add("", "sec", "applications", "data/applications.json", ClientLoaderImpl.class);
        }
    }
}


