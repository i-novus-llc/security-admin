package net.n2oapp.security.auth.simple;

import net.n2oapp.framework.api.context.Context;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.framework.context.smart.impl.api.RootContextProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

/**
 * Провайдер имени пользователя, контекста и идетификатора сессии
 * предоставляет эти данные в контексте n2o
 */
public class SpringSecurityUserDetailsProvider implements RootContextProvider {

    public static final HashSet<String> PARAMS = new HashSet<>(Arrays.asList(UserContext.USERNAME, UserContext.CONTEXT, UserContext.SESSION));

    @Override
    public Map<String, Object> get(Context ctx) {
        Map<String, Object> map = new HashMap<>();
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null)
            return map;
        Authentication authentication = context.getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser"))
            return map;
        String sessionId = UserParamsUtil.getSessionId(authentication);
        map.put(UserContext.SESSION, sessionId);
        map.put(UserContext.USERNAME, UserParamsUtil.getUsername(authentication.getPrincipal()));
        map.put(UserContext.CONTEXT, sessionId);
        return map;
    }

    @Override
    public Set<String> getParams() {
        return PARAMS;
    }

    @Override
    public boolean isCacheable() {
        return false;
    }
}
