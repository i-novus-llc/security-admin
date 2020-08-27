package net.n2oapp.security.admin.api.oauth;

import java.util.Map;

/**
 * Интерфейс для обогащения UserInfo
 */
public interface UserInfoEnricher<T> {
    /**
     * Обогащает данными информацию о пользователе
     *
     * @param userInfo - информация о пользователе
     * @param source   - источник данных
     */
    default void enrich(Map<String, Object> userInfo, T source) {
        Object value = buildValue(source);
        if (value != null)
            userInfo.put(getAlias(), value);
    }

    /**
     * Подготовить значение для вставки в userInfo
     */
    Object buildValue(T source);

    /**
     * Заголовок, под которым данные попадут в информацию о пользователе
     */
    String getAlias();
}
