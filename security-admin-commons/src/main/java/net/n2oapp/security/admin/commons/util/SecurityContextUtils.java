package net.n2oapp.security.admin.commons.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * Извлечение токена из заголовка
 */

public interface SecurityContextUtils {

    static HttpServletRequest getServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Request attributes is empty");
        }
        return attributes.getRequest();
    }

    static String getRequestHost() {
        return getServletRequest().getHeader(HttpHeaders.HOST);
    }

    static String getAuthorizationHeader() {
        String authorization = getServletRequest().getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            throw new IllegalStateException(HttpHeaders.AUTHORIZATION + " header doesn't exist");
        }
        return authorization;
    }

    /**
     * Получение значений из ноды jwt-токена
     */
    static Map<String, String> getJwtNodeValues(String jwt, String... jwtNodeNames) {
        String[] jwtParts = jwt.split("\\.");
        String jwtBodyEncoded = jwtParts[1];

        String jwtBodyDecoded = new String(Base64.getDecoder().decode(jwtBodyEncoded));
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            final JsonNode jwtBodyNode = objectMapper.readTree(jwtBodyDecoded);
            Map<String, String> result = new HashMap<>();
            for (String nodeName : jwtNodeNames) {
                String value = jwtBodyNode.get(nodeName).asText();
                if (value != null) result.put(nodeName, value);
            }
            return result;
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed extracting node \'" + Arrays.toString(jwtNodeNames) + "\' in JWT token", e);
        }
    }

    static String getJwtTokenValue(String jwtNodeName) {
        return getJwtNodeValues(getAuthorizationHeader(), jwtNodeName).get(jwtNodeName);
    }

    static Map<String, String> getJwtTokenValues(String... jwtNodesName) {
        return getJwtNodeValues(getAuthorizationHeader(), jwtNodesName);
    }
}
