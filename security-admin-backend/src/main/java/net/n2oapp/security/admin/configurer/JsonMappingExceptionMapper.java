package net.n2oapp.security.admin.configurer;

import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import net.n2oapp.platform.jaxrs.RestExceptionMapper;
import net.n2oapp.platform.jaxrs.RestMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

/**
 * Конвертация исключения {@link JsonMappingException}
 */
@Component
@Provider
public class JsonMappingExceptionMapper implements RestExceptionMapper<JsonMappingException> {
    @Override
    public RestMessage toMessage(JsonMappingException exception) {
        RestMessage message = new RestMessage(exception.getMessage());
        message.setStackTrace(ExceptionUtils.getStackFrames(exception));
        return message;
    }

    @Override
    public Response.Status getStatus() {
        return Response.Status.BAD_REQUEST;
    }
}
