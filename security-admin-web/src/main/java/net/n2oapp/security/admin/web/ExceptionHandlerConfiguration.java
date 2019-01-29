package net.n2oapp.security.admin.web;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.data.OperationExceptionHandler;
import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.exception.N2oUserException;
import net.n2oapp.framework.api.metadata.local.CompiledObject;
import net.n2oapp.framework.engine.data.N2oOperationExceptionHandler;
import net.n2oapp.platform.jaxrs.RestException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.lang.reflect.InvocationTargetException;

/**
 * Получение пользовательских сообщений от REST сервисов
 */
@ConditionalOnClass(RestException.class)
@Configuration
public class ExceptionHandlerConfiguration {

    @Bean
    @Primary
    public OperationExceptionHandler operationExceptionHandler() {
        return new N2oOperationExceptionHandler() {
            @Override
            public N2oException handle(CompiledObject.Operation operation, DataSet dataSet, Exception e) {
                if (e.getCause() instanceof InvocationTargetException
                        && e.getCause().getCause() instanceof RestException) {
                    RestException restException = (RestException)e.getCause().getCause();
                    // todo platform 2.1 if (restException.getResponseStatus() >= 400 && restException.getResponseStatus() < 500) {
                    throw new N2oUserException(restException.getMessage());
                }
                return super.handle(operation, dataSet, e);
            }
        };
    }
}
