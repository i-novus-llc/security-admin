package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

/**
 * Критерий фильтрации клиентов
 */
@Data
public class ClientCriteria extends BaseCriteria {
    private String clientId;
}
