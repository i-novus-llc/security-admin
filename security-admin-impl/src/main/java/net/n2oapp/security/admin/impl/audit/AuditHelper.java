package net.n2oapp.security.admin.impl.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import ru.i_novus.ms.audit.client.AuditClient;
import ru.i_novus.ms.audit.client.model.AuditClientRequest;

/**
 * Создание сообщений для аудит-сервиса
 */

public class AuditHelper {

    @Autowired
    private AuditClient auditClient;
    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    public void audit(String action, Object object, String objectId, String objectName) {
        AuditClientRequest request = new AuditClientRequest();
        request.setEventType(messageSourceAccessor.getMessage(action));
        request.setObjectType(object.getClass().getSimpleName());
        request.setObjectId(objectId);
        request.setContext(object.toString());
        request.setObjectName(objectName);
        request.setSourceApplication("Access");
        request.setAuditType((short) 1);

        auditClient.add(request);
    }

}
