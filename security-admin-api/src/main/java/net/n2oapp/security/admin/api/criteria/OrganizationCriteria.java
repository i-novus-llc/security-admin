package net.n2oapp.security.admin.api.criteria;

import lombok.Data;

import java.util.List;

/**
 * Критерий фильтрации организаций
 */
@Data
public class OrganizationCriteria extends BaseCriteria {
    private String shortName;
    private String name;
    private String ogrn;
    private List<String> systemCodes;
}
