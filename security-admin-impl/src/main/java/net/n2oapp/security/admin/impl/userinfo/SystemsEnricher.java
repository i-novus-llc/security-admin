package net.n2oapp.security.admin.impl.userinfo;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class SystemsEnricher implements UserInfoEnricher<AccountEntity> {

    private final Boolean permissionEnabled;

    public SystemsEnricher(@Value("${access.permission.enabled}") Boolean permissionEnabled) {
        this.permissionEnabled = permissionEnabled;
    }

    @Override
    public Object buildValue(AccountEntity source) {
        if (nonNull(source.getRoleList())) {
            List<String> systems = new ArrayList<>();
            if (Boolean.TRUE.equals((permissionEnabled))) {
                systems.addAll(source.getRoleList().stream().filter(r -> nonNull(r.getPermissionList())).flatMap(r -> r.getPermissionList().stream())
                        .filter(permission -> nonNull(permission.getSystemCode())).map(p -> p.getSystemCode().getCode()).collect(Collectors.toList()));
            }
            systems.addAll(source.getRoleList().stream().filter(role -> nonNull(role.getSystemCode())).
                    map(role -> (role.getSystemCode().getCode())).collect(Collectors.toList()));
            systems = systems.stream().distinct().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(systems))
                return systems;
        }
        return null;
    }

    @Override
    public String getAlias() {
        return "systems";
    }
}
