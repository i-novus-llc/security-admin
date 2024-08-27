package net.n2oapp.security.admin;

import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
import net.n2oapp.platform.test.autoconfigure.pg.TestcontainersPgAutoConfiguration;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication()
@EnableTestcontainersPg
@Import(TestcontainersPgAutoConfiguration.class)
public class TestApplication {

//    @MockBean
//    private VersionService versionService;
//    @MockBean
//    private RefBookService refBookService;
//    @MockBean
//    private DraftService draftService;
//    @MockBean
//    private PublishService publishService;
//    @MockBean
//    private RdmChangeDataClient rdmChangeDataClient;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public SpringBus cxf() {
        return new SpringBus();
    }
}
