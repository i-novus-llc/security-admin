package net.n2oapp.security.admin.auth.server.oauth;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SystemsEnricher implements UserInfoEnricher<UserEntity> {

    private final Boolean permissionEnabled;

    public SystemsEnricher(@Value("${access.permission.enabled}") Boolean permissionEnabled) {
        this.permissionEnabled = permissionEnabled;
    }

    @Override
    public Object buildValue(UserEntity source) {
        //        todo SECURITY-396
//        if (nonNull(source.getRoleList())) {
//            List<String> systems = new ArrayList<>();
//            if (Boolean.TRUE.equals((permissionEnabled))) {
//                systems.addAll(source.getRoleList().stream().filter(r -> nonNull(r.getPermissionList())).flatMap(r -> r.getPermissionList().stream())
//                        .filter(permission -> nonNull(permission.getSystemCode())).map(p -> p.getSystemCode().getCode()).collect(Collectors.toList()));
//            }
//            systems.addAll(source.getRoleList().stream().filter(role -> nonNull(role.getSystemCode())).
//                    map(role -> (role.getSystemCode().getCode())).collect(Collectors.toList()));
//            systems = systems.stream().distinct().collect(Collectors.toList());
//            if (!CollectionUtils.isEmpty(systems))
//                return systems;
//        }
        return null;
    }

    @Override
    public String getAlias() {
        return "systems";
    }
}
