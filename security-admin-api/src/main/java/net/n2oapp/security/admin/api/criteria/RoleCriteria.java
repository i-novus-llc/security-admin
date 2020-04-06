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

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Критерий фильтрации ролей
 */
@Data
public class RoleCriteria extends BaseCriteria {
    private String name;
    private String description;
    private List<String> permissionCodes;
    private List<String> systemCodes;
    private String userLevel;
    private Boolean forForm;
    private Boolean groupBySystem;

    public void setPermissionCodes(List<String> permissionCodes) {
        this.permissionCodes = permissionCodes != null ? permissionCodes : new ArrayList<>();
    }
}
