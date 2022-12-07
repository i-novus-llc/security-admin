package net.n2oapp.security.admin;

import net.n2oapp.security.admin.api.audit.AuditService;
import net.n2oapp.security.admin.api.service.RefChangeDataExportService;
import net.n2oapp.security.admin.audit.AuditServiceImpl;
import net.n2oapp.security.admin.rdm.RdmRefChangeDataExportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CommonsConfiguration {

    @Bean
    AuditService auditService() {
        return new AuditServiceImpl();
    }

    // получение данных пользователя для аудита
//    @Bean
//    public UserAccessor userAccessor() {
//        return () -> {
//            String userId, userName;
//            userId = userName = "-";
//            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//            if (auth != null && auth.getPrincipal() != null) {
//                if (auth.getPrincipal() instanceof User) {
//                    User user = (User) auth.getPrincipal();
//                    userId = user.getEmail();
//                    userName = user.getUsername();
//                } else {
//                    userId = "" + auth.getPrincipal();
//                }
//            }
//            return new ru.i_novus.ms.audit.client.model.User(userId, userName);
//        };
//    }

    @Bean
    @Primary
    public RefChangeDataExportService refChangeDataExportService() {
        return new RdmRefChangeDataExportService();
    }
}
