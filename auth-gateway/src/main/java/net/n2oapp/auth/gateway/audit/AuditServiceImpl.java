package net.n2oapp.auth.gateway.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.n2oapp.security.admin.api.audit.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import ru.i_novus.ms.audit.client.AuditClient;
import ru.i_novus.ms.audit.client.model.AuditClientRequest;

/**
 * Создание сообщений для аудит-сервиса
 */

public class AuditServiceImpl implements AuditService {

    @Autowired
    private AuditClient auditClient;

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    @Autowired
    private ObjectMapper mapper;


    @Override
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

        try {
            auditClient.add(request);
        } catch (Exception e) {
//          нужно, чтобы security-admin отдавал статус связанный с её ошибкой, а не возникшей в аудите
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
