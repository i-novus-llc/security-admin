package net.n2oapp.auth.gateway.configurer;

import com.fasterxml.jackson.databind.JsonMappingException;
import net.n2oapp.platform.jaxrs.RestExceptionMapper;
import net.n2oapp.platform.jaxrs.RestMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

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
