package net.n2oapp.security.admin.impl.userInfo;

import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import net.n2oapp.security.admin.impl.entity.OrganizationEntity;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class OrganizationEnricher implements UserInfoEnricher<AccountEntity> {

    @Override
    public Object buildValue(AccountEntity source) {
        OrganizationEntity org = source.getOrganization();
        if (isNull(org)) return null;

        Organization orgModel = new Organization();
        orgModel.setId(org.getId());
        orgModel.setCode(org.getCode());
        orgModel.setShortName(org.getShortName());
        orgModel.setFullName(org.getFullName());
        orgModel.setInn(org.getInn());
        return orgModel;
    }

    @Override
    public String getAlias() {
        return "organization";
    }
}
