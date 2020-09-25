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
package net.n2oapp.security.auth.context;

import net.n2oapp.framework.api.context.ContextEngine;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.security.auth.common.UserParamsUtil;
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
