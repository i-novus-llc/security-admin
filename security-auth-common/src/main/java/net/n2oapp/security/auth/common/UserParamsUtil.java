/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.n2oapp.security.auth.common;

import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import net.n2oapp.security.auth.common.authority.SystemGrantedAuthority;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Утилитный класс для получения username и sessionId
 */
public class UserParamsUtil {

    private static final String ROLES = "roles";
    private static final String PERMISSIONS = "permissions";
    private static final String SYSTEMS = "systems";

    /**
     * Получение текущей сессии пользователя по контексту {@link SecurityContextHolder}
     *
     * @return пустую строку или, если имеется, id сессии
     */
    public static String getSessionId() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null)
            return "";
        return getSessionId(context.getAuthentication());
    }

    /**
     * Получение сессии пользователя по аутентификации
     *
     * @param authentication Объект с информацией об аутентификации
     * @return пустую строку или, если имеется, id сессии
     */
    public static String getSessionId(Authentication authentication) {
        if (authentication == null)
            return "";
        if (authentication.getDetails() instanceof WebAuthenticationDetails) {
            WebAuthenticationDetails sessionDetails = (WebAuthenticationDetails) authentication.getDetails();
            return sessionDetails.getSessionId();
        } else {
            String sessionId = getProperty(authentication,
                    BeanUtils.getPropertyDescriptor(authentication.getClass(), "sessionId"));
            return sessionId == null ? "" : sessionId;
        }
    }

    /**
     * Получение имени текущего пользователя по контексту {@link SecurityContextHolder}
     *
     * @return пустую строку или имя пользователя, если он не анонимный
     */
    public static String getUsername() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null)
            return "";
        Authentication authentication = context.getAuthentication();
        if (authentication == null)
            return "";
        if (authentication instanceof AnonymousAuthenticationToken)
            return "";
        return getUsername(authentication.getPrincipal());
    }

    /**
     * Получение имени пользователя по principal
     *
     * @param principal Объект с данными пользвателя
     * @return пустую строку или имя пользователя, если он не анонимный
     */
    public static String getUsername(Object principal) {
        String username = "";
        if (principal instanceof String) {
            username = (String) principal;
        } else if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            username = userDetails.getUsername();
        }
        return username;
    }

    /**
     * Получить детальную информацию о текущем пользователе по контексту {@link SecurityContextHolder}
     *
     * @param <T> Класс детальнйо информации о пользователе
     * @return Детальная информация о пользователе
     */
    public static <T extends UserDetails> T getUserDetails() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null)
            return null;
        Authentication authentication = context.getAuthentication();
        if (authentication == null)
            return null;
        Object details = authentication.getPrincipal();
        if (details instanceof UserDetails)
            return (T) details;
        details = authentication.getDetails();
        if (details instanceof UserDetails)
            return (T) details;
        return null;
    }

    /**
     * Получить детальную информацию о пользователю в виде ключ-значение
     *
     * @return Детальная информация о пользователе в виде ключ-значение
     */
    public static <T extends UserDetails> Map<String, Object> getUserDetailsAsMap(T userDetails) {
        if (userDetails == null)
            return Collections.emptyMap();
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(userDetails.getClass());
        Map<String, Object> map = new HashMap<>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            map.put(propertyDescriptor.getName(), getProperty(userDetails, propertyDescriptor));
        }
        return map;
    }

    /**
     * Получить детальную информацию о пользователю в виде ключ-значение
     *
     * @return Детальная информация о пользователе в виде ключ-значение
     */
    public static Map<String, Object> getUserDetailsAsMap() {
        return getUserDetailsAsMap(getUserDetails());
    }

    /**
     * Получить детальную информацию о пользователе по имени свойства
     *
     * @param userDetails Детальная информация о пользователе
     * @param property    Имя свойства
     * @param <T>         Тип значения
     * @return Детальная информация о пользователе по имени свойства
     */
    public static <T, U extends UserDetails> T getUserDetailsProperty(U userDetails, String property) {
        if (userDetails == null)
            return null;
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(userDetails.getClass(), property);
        return getProperty(userDetails, propertyDescriptor);
    }

    /**
     * Получить детальную информацию о пользователе по имени свойства
     *
     * @param property Имя свойства
     * @param <T>      Тип значения
     * @return Детальная информация о пользователе по имени свойства
     */
    public static <T> T getUserDetailsProperty(String property) {
        return getUserDetailsProperty(getUserDetails(), property);
    }

    private static <T> T getProperty(Object bean, PropertyDescriptor propertyDescriptor) {
        if (propertyDescriptor == null)
            return null;
        Method readMethod = propertyDescriptor.getReadMethod();
        if (readMethod == null)
            return null;
        try {
            return (T) readMethod.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Извлечь список полномочий из информации о пользователе
     *
     * @param map Информация о пользователе
     * @return Список полномочий
     */
    public static List<GrantedAuthority> extractAuthorities(Map<String, ?> map) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (map.containsKey(ROLES)) {
            for (String role : (List<String>) map.get(ROLES)) {
                authorities.add(new RoleGrantedAuthority(role));
            }
        }
        if (map.containsKey(PERMISSIONS)) {
            for (String permission : (List<String>) map.get(PERMISSIONS)) {
                authorities.add(new PermissionGrantedAuthority(permission));
            }
        }
        if (map.containsKey(SYSTEMS)) {
            for (String system : (List<String>) map.get(SYSTEMS)) {
                authorities.add(new SystemGrantedAuthority(system));
            }
        }
        return authorities;
    }
}