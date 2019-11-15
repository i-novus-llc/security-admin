package net.n2oapp.security.admin.impl.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private ObjectMapper mapper = new ObjectMapper();

    public void audit(String action, Object object, String objectId, String objectName) {
        AuditClientRequest request = new AuditClientRequest();
        request.setEventType(messageSourceAccessor.getMessage(action));
        request.setObjectType(object.getClass().getSimpleName());
        request.setObjectId(objectId);
        try {
            request.setContext(mapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            request.setContext(object.toString());
        }
        request.setObjectName(objectName);
        request.setAuditType((short) 1);

        auditClient.add(request);
    }

}
