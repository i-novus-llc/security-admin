package net.n2oapp.security.admin.auth.server.oauth;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class PermissionsEnricher implements UserInfoEnricher<UserEntity> {

    private final Boolean permissionEnabled;

    public PermissionsEnricher(@Value("${access.permission.enabled}") Boolean permissionEnabled) {
        this.permissionEnabled = permissionEnabled;
    }

    @Override
    public Object buildValue(UserEntity source) {
        if (nonNull(source.getRoleList())) {
            if (Boolean.TRUE.equals((permissionEnabled))) {
                List<String> permissions = source.getRoleList().stream()
                        .filter(r -> nonNull(r.getPermissionList()))
                        .flatMap(r -> r.getPermissionList().stream())
                        .map(PermissionEntity::getCode).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(permissions)) {
                    return permissions;
                }
            }
        }
        return null;
    }

    @Override
    public String getAlias() {
        return "permissions";
    }
}
