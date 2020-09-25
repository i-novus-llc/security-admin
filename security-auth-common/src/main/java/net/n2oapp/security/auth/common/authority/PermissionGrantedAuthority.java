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
package net.n2oapp.security.auth.common.authority;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

/**
 * Полномочие, основанное на правах доступа
 */
public class PermissionGrantedAuthority implements GrantedAuthority {

    private static final String DEFAULT_PERMISSION_PREFIX = "PERMISSION_";

    private final String permission;

    public PermissionGrantedAuthority(String permission) {
        Assert.hasText(permission, "A granted authority textual representation is required");
        this.permission = permission;
    }

    public String getAuthority() {
        return DEFAULT_PERMISSION_PREFIX + permission;
    }

    public String getPermission() {
        return permission;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof PermissionGrantedAuthority) {
            return permission.equals(((PermissionGrantedAuthority) obj).permission);
        }

        return false;
    }

    public int hashCode() {
        return this.permission.hashCode();
    }

    public String toString() {
        return this.permission;
    }
}
