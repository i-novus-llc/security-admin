package net.n2oapp.security.admin.impl.userinfo;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import org.springframework.stereotype.Component;

@Component
public class UserLevelEnricher implements UserInfoEnricher<AccountEntity> {

    @Override
    public Object buildValue(AccountEntity source) {
        return source.getUserLevel();
    }

    @Override
    public String getAlias() {
        return "userLevel";
    }
}
