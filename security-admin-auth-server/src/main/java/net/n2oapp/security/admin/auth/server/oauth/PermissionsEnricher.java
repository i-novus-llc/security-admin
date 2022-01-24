package net.n2oapp.security.admin.auth.server.oauth;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PermissionsEnricher implements UserInfoEnricher<UserEntity> {

    private final Boolean permissionEnabled;

    public PermissionsEnricher(@Value("${access.permission.enabled}") Boolean permissionEnabled) {
        this.permissionEnabled = permissionEnabled;
    }

    @Override
    public Object buildValue(UserEntity source) {
        //        todo SECURITY-396
//        if (nonNull(source.getRoleList())) {
//            if (Boolean.TRUE.equals((permissionEnabled))) {
//                List<String> permissions = source.getRoleList().stream()
//                        .filter(r -> nonNull(r.getPermissionList()))
//                        .flatMap(r -> r.getPermissionList().stream())
//                        .map(PermissionEntity::getCode).collect(Collectors.toList());
//                if (!CollectionUtils.isEmpty(permissions)) {
//                    return permissions;
//                }
//            }
//        }
        return null;
    }

    @Override
    public String getAlias() {
        return "permissions";
    }
}
