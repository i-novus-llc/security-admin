package net.n2oapp.security.admin.auth.server.oauth;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.auth.common.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static net.n2oapp.security.admin.auth.server.UserTokenConverter.*;

/**
 * Сервис для построения UserInfo
 */
@Transactional
public class UserInfoService {

    private final boolean permissionEnabled;
    private final UserRepository userRepository;
    private final Collection<UserInfoEnricher<UserEntity>> userInfoEnrichers;

    public UserInfoService(UserRepository userRepository, boolean permissionEnabled, Collection<UserInfoEnricher<UserEntity>> userInfoEnrichers) {
        this.permissionEnabled = permissionEnabled;
        this.userRepository = userRepository;
        this.userInfoEnrichers = userInfoEnrichers;
    }

    public Map<String, Object> buildUserInfo(OAuth2Authentication authentication) {
        Map<String, Object> userInfo = new HashMap<>();
        List<String> permissions = new ArrayList<>();
        List<String> systems = new ArrayList<>();

        if (authentication.getPrincipal() instanceof User) {
            UserEntity user = userRepository.findOneByUsernameIgnoreCase(((User) authentication.getPrincipal()).getUsername());
            for (UserInfoEnricher<UserEntity> enricher : userInfoEnrichers)
                enricher.enrich(userInfo, user);

            if (nonNull(user.getRoleList())) {
                if (permissionEnabled) {
                    permissions.addAll(user.getRoleList().stream().filter(r -> nonNull(r.getPermissionList())).flatMap(r -> r.getPermissionList().stream())
                            .map(p -> p.getCode()).collect(Collectors.toList()));
                    systems.addAll(user.getRoleList().stream().filter(r -> nonNull(r.getPermissionList())).flatMap(r -> r.getPermissionList().stream())
                            .filter(permission -> nonNull(permission.getSystemCode())).map(p -> p.getSystemCode().getCode()).collect(Collectors.toList()));
                }
                systems.addAll(user.getRoleList().stream().filter(role -> nonNull(role.getSystemCode())).
                        map(role -> (role.getSystemCode().getCode())).collect(Collectors.toList()));
                systems = systems.stream().distinct().collect(Collectors.toList());
            }
        }

        userInfo.put(PERMISSIONS, permissions);
        userInfo.put(SYSTEMS, systems);

        if (authentication.getUserAuthentication() != null)
            userInfo.put(SID, ((Map<String, Object>) authentication.getUserAuthentication().getDetails()).get(SID));
        return userInfo;
    }
}
