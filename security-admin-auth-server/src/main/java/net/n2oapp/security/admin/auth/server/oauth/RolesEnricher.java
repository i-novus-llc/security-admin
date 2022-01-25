package net.n2oapp.security.admin.auth.server.oauth;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class RolesEnricher implements UserInfoEnricher<UserEntity> {
    @Override
    public Object buildValue(UserEntity source) {
        //        todo SECURITY-396
//        List<RoleEntity> roles = source.getRoleList();
//        if (roles == null || roles.isEmpty())
//            return null;
//        return source.getRoleList().stream().map(RoleEntity::getCode).collect(Collectors.toList());
        return null;
    }

    @Override
    public String getAlias() {
        return "roles";
    }
}
