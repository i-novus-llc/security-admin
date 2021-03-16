package net.n2oapp.auth.gateway.configurer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.n2oapp.platform.jaxrs.MapperConfigurer;
import org.springframework.stereotype.Component;

/**
 * Конфигуратор для добавления StrictStringDeserializer
 */
@Component
public class ObjectMapperConfigurer implements MapperConfigurer {
    @Override
    public void configure(ObjectMapper mapper) {
        SimpleModule m = new SimpleModule();
        m.addDeserializer(String.class, new StrictStringDeserializer());
        mapper.registerModule(m);
    }
}
