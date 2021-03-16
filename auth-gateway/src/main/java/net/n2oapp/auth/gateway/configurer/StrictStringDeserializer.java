package net.n2oapp.auth.gateway.configurer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Бросает исключение в случае попытки десериализации json поля с типом НЕ String в java модель со свойством типа java.lang.String
 */
public class StrictStringDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (jsonParser.getCurrentToken() != JsonToken.VALUE_STRING) {
            throw deserializationContext.wrongTokenException(jsonParser, String.class, JsonToken.VALUE_STRING, null);
        }
        return jsonParser.getValueAsString();
    }
}
