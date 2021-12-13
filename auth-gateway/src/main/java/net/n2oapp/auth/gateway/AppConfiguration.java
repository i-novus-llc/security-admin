package net.n2oapp.auth.gateway;

import net.n2oapp.auth.gateway.audit.AuditServiceImpl;
import net.n2oapp.auth.gateway.rdm.RdmRefChangeDataExportService;
import net.n2oapp.security.admin.api.audit.AuditService;
import net.n2oapp.security.admin.api.service.RefChangeDataExportService;
import net.n2oapp.security.auth.common.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.i_novus.ms.audit.client.UserAccessor;

@Configuration
public class AppConfiguration {

    @Bean
    AuditService auditService() {
        return new AuditServiceImpl();
    }

    @Bean
    public UserAccessor userAccessor() {
        return () -> {
            String userId, userName;
            userId = userName = "-";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() != null) {
                if (auth.getPrincipal() instanceof User) {
                    User user = (User) auth.getPrincipal();
                    userId = user.getEmail();
                    userName = user.getUsername();
                } else {
                    userId = "" + auth.getPrincipal();
                }
            }
            return new ru.i_novus.ms.audit.client.model.User(userId, userName);
        };
    }

    @Bean
    @Primary
    public RefChangeDataExportService refChangeDataExportService() {
        return new RdmRefChangeDataExportService();
    }
}
