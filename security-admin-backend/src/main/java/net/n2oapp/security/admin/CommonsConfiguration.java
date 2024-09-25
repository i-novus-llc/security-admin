package net.n2oapp.security.admin;

import net.n2oapp.security.admin.api.audit.AuditService;
import net.n2oapp.security.admin.api.service.RefChangeDataExportService;
import net.n2oapp.security.admin.impl.audit.SimpleAuditService;
import net.n2oapp.security.admin.rdm.RdmRefChangeDataExportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.i_novus.ms.rdm.api.provider.RdmMapperConfigurer;

@Configuration
public class CommonsConfiguration {

    @Bean
    AuditService auditService() {
        return new SimpleAuditService();
    }

    @Bean
    @Primary
    public RefChangeDataExportService refChangeDataExportService() {
        return new RdmRefChangeDataExportService();
    }

    @Bean
    public RdmMapperConfigurer rdmMapperConfigurer() {
        return new RdmMapperConfigurer();
    }
}
