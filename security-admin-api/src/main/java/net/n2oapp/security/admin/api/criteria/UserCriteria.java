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
package net.n2oapp.security.admin.api.criteria;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Критерий фильтрации пользователей
 */
@Getter
@Setter
public class UserCriteria extends BaseCriteria {
    private String username;
    private String email;
    private String fio;
    private String isActive;
    private List<Integer> roleIds;
    private List<String> roleCodes;
    private String password;
    private List<String> systems;
    private String userLevel;
    private Integer regionId;
    private String regionCode;
    private List<Integer> organizations;
    private Integer departmentId;
    private String extSys;
    private LocalDateTime lastActionDate;

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds != null ? roleIds : new ArrayList<>();
    }

    public void setRoleCodes(List<String> roleCodes) {
        this.roleCodes = roleCodes != null ? roleCodes : new ArrayList<>();
    }

}
