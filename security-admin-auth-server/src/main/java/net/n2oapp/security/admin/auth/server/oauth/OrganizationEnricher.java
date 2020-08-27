package net.n2oapp.security.admin.auth.server.oauth;

import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.OrganizationEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class OrganizationEnricher implements UserInfoEnricher<UserEntity> {

    @Override
    public Object buildValue(UserEntity source) {
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
