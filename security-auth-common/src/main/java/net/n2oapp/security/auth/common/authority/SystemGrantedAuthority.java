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
 * Полномочие, основанное на доступе к определенной системе
 */
public class SystemGrantedAuthority implements GrantedAuthority {

    private static final String DEFAULT_SYSTEM_PREFIX = "SYSTEM_";

    private final String system;

    public SystemGrantedAuthority(String system) {
        Assert.hasText(system, "A granted authority textual representation is required");
        this.system = system;
    }

    @Override
    public String getAuthority() {
        return system.startsWith(DEFAULT_SYSTEM_PREFIX) ? system : DEFAULT_SYSTEM_PREFIX + system;
    }

    public String getSystem() {
        return system;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof SystemGrantedAuthority) {
            return system.equals(((SystemGrantedAuthority) obj).system);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.system.hashCode();
    }

    @Override
    public String toString() {
        return system;
    }
}
