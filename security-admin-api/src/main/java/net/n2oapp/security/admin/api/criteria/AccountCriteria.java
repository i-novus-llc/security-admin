package net.n2oapp.security.admin.api.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCriteria extends BaseCriteria {

    public AccountCriteria() {
    }

    public AccountCriteria(String userName) {
        super(0, 100);
        this.username = userName;
    }

    private Integer userId;

    private String username;
}
