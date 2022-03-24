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

import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.core.GrantedAuthority;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Извлекает Userinfo из ответа auth-gateway
 */
public class GatewayPrincipalExtractor implements PrincipalExtractor, AuthoritiesExtractor {

    private static final String USERNAME = "username";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String SURNAME = "surname";
    private static final String PATRONYMIC = "patronymic";
    private static final String DEPARTMENT = "department";
    private static final String CODE_KEY = "code";
    private static final String NAME_KEY = "name";
    private static final String ORGANIZATION = "organization";
    private static final String REGION = "region";
    private static final String ACCOUNT_ID = "accountId";

    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        return UserParamsUtil.extractAuthorities(map);
    }

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        //todo учесть что параметры в кейклок могут быть названы по разному, смотри AuthoritiesPrincipalExtractor
        User user = new User((String) map.get(USERNAME), "N/A", extractAuthorities(map), (String) map.get(SURNAME), (String) map.get(NAME),
                (String) map.get(PATRONYMIC), (String) map.get(EMAIL));
        user.setAccountId((String) map.get(ACCOUNT_ID));

        LinkedHashMap department = (LinkedHashMap) map.get(DEPARTMENT);

        if (department != null) {
            user.setDepartment((String) department.get(CODE_KEY));
            user.setDepartmentName((String) department.get(NAME_KEY));
        }

        LinkedHashMap organization = (LinkedHashMap) map.get(ORGANIZATION);
        if (organization != null)
            user.setOrganization((String) organization.get(CODE_KEY));

        LinkedHashMap region = (LinkedHashMap) map.get(REGION);
        if (region != null)
            user.setRegion((String) region.get(CODE_KEY));

        return user;
    }
}
