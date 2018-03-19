package net.n2oapp.security.auth.simple;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * Утилитный класс для получения username и sessionId
 */
public class UserParamsUtil {

    /**
     * @param authentication Объект с информацией об аутентификации
     * @return пустую строку или, если имеется, id сессии
     */
    public static String getSessionId(Authentication authentication) {
        WebAuthenticationDetails sessionDetails = (WebAuthenticationDetails)authentication.getDetails();
        if (sessionDetails == null)
            return "";

        return sessionDetails.getSessionId();
    }

    /**
     * Получение username в зависимости от типа principal
     * @param principal Объект с данными пользвателя
     * @return пустую строку или имя пользователя, если он не анонимный
     */
    public static String getUsername(Object principal) {
        String username = "";
        if(principal instanceof String) {
            username = (String) principal;
        } else if (principal instanceof User) {
            User userDetails = (User) principal;
            username = userDetails.getUsername();
        } else if(principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            username = userDetails.getUsername();
        }

        return username;
    }
}