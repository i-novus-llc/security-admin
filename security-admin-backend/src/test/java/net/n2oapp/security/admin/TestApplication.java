package net.n2oapp.security.admin;

import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.i_novus.ms.rdm.api.service.DraftService;
import ru.i_novus.ms.rdm.api.service.PublishService;
import ru.i_novus.ms.rdm.api.service.RefBookService;
import ru.i_novus.ms.rdm.api.service.VersionService;
import ru.i_novus.ms.rdm.sync.RdmClientSyncAutoConfiguration;
import ru.i_novus.ms.rdm.sync.service.change_data.RdmChangeDataClient;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication(exclude = RdmClientSyncAutoConfiguration.class)
@EnableTestcontainersPg
public class TestApplication {

    @MockBean
    private VersionService versionService;
    @MockBean
    private RefBookService refBookService;
    @MockBean
    private DraftService draftService;
    @MockBean
    private PublishService publishService;
    @MockBean
    private RdmChangeDataClient rdmChangeDataClient;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
