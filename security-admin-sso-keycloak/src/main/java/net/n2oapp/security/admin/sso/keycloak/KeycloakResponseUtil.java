package net.n2oapp.security.admin.sso.keycloak;

import jakarta.ws.rs.core.Response;
import org.keycloak.representations.idm.ErrorRepresentation;
import org.springframework.http.ResponseEntity;

/**
 * Утилиты для обработки ответов keycloak
 */
final class KeycloakResponseUtil {

    private KeycloakResponseUtil() {
    }

    /**
     * Получить текст ошибки из ответа keycloak
     *
     * @param response ответ keycloak
     * @return текст ошибки
     */
    static String getErrorMessage(ResponseEntity<Response> response) {
        Response body = response.getBody();
        if (body == null)
            return "Keycloak request failed with status " + response.getStatusCode().value();
        return body.readEntity(ErrorRepresentation.class).getErrorMessage();
    }
}
