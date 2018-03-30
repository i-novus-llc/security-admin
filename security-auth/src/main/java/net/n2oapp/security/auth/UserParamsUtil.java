package net.n2oapp.security.auth;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Утилитный класс для получения username и sessionId
 */
public class UserParamsUtil {

    /**
     * @param authentication Объект с информацией об аутентификации
     * @return пустую строку или, если имеется, id сессии
     */
    public static String getSessionId(Authentication authentication) {
        String sessionId = "";
        if (authentication == null)
            return sessionId;
        if (authentication.getDetails() instanceof WebAuthenticationDetails) {
            WebAuthenticationDetails sessionDetails = (WebAuthenticationDetails)authentication.getDetails();
            return sessionDetails.getSessionId();
        } else {
            PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(authentication.getClass(), "sessionId");
            if (propertyDescriptor == null)
                return sessionId;
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod == null)
                return sessionId;
            try {
                return (String) readMethod.invoke(authentication);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
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
        } else if(principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            username = userDetails.getUsername();
        }

        return username;
    }
}