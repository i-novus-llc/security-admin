package net.n2oapp.security.admin.configurer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.n2oapp.platform.jaxrs.MapperConfigurer;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ObjectMapperConfigurer implements MapperConfigurer {
    @Override
    public void configure(ObjectMapper mapper) {
        SimpleModule m = new SimpleModule();
        m.addDeserializer(String.class, new StrictTypeDeserializer<>(JsonToken.VALUE_STRING, JsonParser::getValueAsString));
        m.addDeserializer(Integer.class, new StrictTypeDeserializer<>(JsonToken.VALUE_NUMBER_INT, JsonParser::getValueAsInt));
        mapper.registerModule(m);
    }

    /**
     * Бросает исключение в случае несоответствия типов в json и java модели
     */
    public static class StrictTypeDeserializer<T> extends JsonDeserializer<T> {

        private final CheckedFunction<T> getter;
        private final JsonToken token;

        public StrictTypeDeserializer(JsonToken token, CheckedFunction<T> getter) {
            this.getter = getter;
            this.token = token;
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getCurrentToken() != token) {
                throw ctxt.wrongTokenException(p, String.class, token, null);
            }
            return getter.apply(p);
        }
    }

    @FunctionalInterface
    public interface CheckedFunction<R> {
        R apply(JsonParser t) throws IOException;
    }
}
