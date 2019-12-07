package net.n2oapp.security.admin.impl.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import ru.i_novus.ms.audit.client.AuditClient;
import ru.i_novus.ms.audit.client.model.AuditClientRequest;

/**
 * Создание сообщений для аудит-сервиса
 */

public class AuditHelper {

    private static final Logger log = LoggerFactory.getLogger(AuditHelper.class);

    private AuditClient auditClient;
    private MessageSourceAccessor messageSourceAccessor;
    private ObjectMapper mapper;

    public void audit(String action, Object object, String objectId, String objectName) {
        if (auditClient == null) {
            log.warn("Audit service is disabled, please set 'audit.client.enabled' and 'audit.service.url' properties.");
            return;
        }
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

    @Autowired(required = false)
    public void setAuditClient(AuditClient auditClient) {
        this.auditClient = auditClient;
    }

    @Autowired
    public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
