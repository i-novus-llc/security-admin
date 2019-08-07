package net.n2oapp.security.auth.context;

import net.n2oapp.framework.api.context.ContextEngine;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.security.user.UserParamsUtil;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * Контекст пользователя N2O основанный на свойствах секьюрити контекста пользователя {@link UserDetails}
 */
public class SpringSecurityUserContext implements ContextEngine {
    @Override
    public Object get(String param, Map<String, Object> baseParams) {
        if (baseParams.containsKey(param))
            return baseParams.get(param);
        return get(param);
    }

    @Override
    public void set(Map<String, Object> context, Map<String, Object> baseParams) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(String param) {
        if (param.equals(UserContext.USERNAME))
            return UserParamsUtil.getUsername();
        if (param.equals(UserContext.SESSION))
            return UserParamsUtil.getSessionId();
        if (param.equals(UserContext.CONTEXT))
            return UserParamsUtil.getSessionId();
        return UserParamsUtil.getUserDetailsProperty(param);
    }

    @Override
    public void set(Map<String, Object> context) {
        throw new UnsupportedOperationException();
    }
}
