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
package net.n2oapp.security.auth;

import net.n2oapp.framework.access.simple.PermissionApi;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.security.auth.common.UserParamsUtil;
import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Интерфейс для проверки прав и ролей пользователя с помощью Spring Security
 */
public class SecuritySimplePermissionApi implements PermissionApi {

    @Override
    public boolean hasPermission(UserContext user, String permissionId) {
        UserDetails userDetails = UserParamsUtil.getUserDetails();
        return userDetails != null && userDetails.getAuthorities().stream()
                .filter(a -> a instanceof PermissionGrantedAuthority)
                .anyMatch(grantedAuthority -> ((PermissionGrantedAuthority) grantedAuthority).getPermission().equalsIgnoreCase(permissionId));
    }

    @Override
    public boolean hasRole(UserContext user, String roleId) {
        UserDetails userDetails = UserParamsUtil.getUserDetails();
        return userDetails != null && userDetails.getAuthorities().stream()
                .filter(a -> a instanceof RoleGrantedAuthority)
                .anyMatch(grantedAuthority -> ((RoleGrantedAuthority) grantedAuthority).getRole().equalsIgnoreCase(roleId));
    }

    @Override
    public boolean hasAuthentication(UserContext user) {
        UserDetails userDetails = UserParamsUtil.getUserDetails();
        return userDetails != null;
    }

    @Override
    public boolean hasUsername(UserContext user, String name) {
        return UserParamsUtil.getUsername().equalsIgnoreCase(name);
    }
}
