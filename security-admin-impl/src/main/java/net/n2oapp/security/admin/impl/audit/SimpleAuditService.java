package net.n2oapp.security.admin.impl.audit;


import lombok.extern.slf4j.Slf4j;
import net.n2oapp.security.admin.api.audit.AuditService;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SimpleAuditService implements AuditService {
    @Override
    public void audit(String action, Object object, String objectId, String objectName) {
        log.info(String.format("Действие '%s' было произведено над объектом '%s' с идентификатором '%s'", action, objectName, objectId));
    }
}