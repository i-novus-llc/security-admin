package net.n2oapp.security.admin.api.criteria;

import lombok.Data;
import net.n2oapp.security.admin.api.enumeration.BankTypeEnum;

import java.util.UUID;

@Data
public class BankCriteria extends BaseCriteria {
    private String name;
    private BankTypeEnum type;
    private UUID parent;
}
