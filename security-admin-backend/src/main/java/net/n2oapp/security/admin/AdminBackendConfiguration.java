package net.n2oapp.security.admin;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.i_novus.ms.audit.client.AuditClient;
import ru.i_novus.ms.audit.client.impl.SimpleAuditClientImpl;
import ru.i_novus.ms.audit.service.api.AuditRest;

@Configuration
public class AdminBackendConfiguration {

    @Configuration
    @EnableJaxRsProxyClient(
            classes = {AuditRest.class},
            address = "${audit.client.apiUrl}")
    static class AuditClientConfiguration {
        @Bean
        public AuditClient simpleAuditClient(@Qualifier("auditRestJaxRsProxyClient") AuditRest auditRest) {
            SimpleAuditClientImpl simpleAuditClient = new SimpleAuditClientImpl();
            simpleAuditClient.setAuditRest(auditRest);
            return simpleAuditClient;
        }
    }

}
