package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.auth.common.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static net.n2oapp.security.admin.auth.server.UserTokenConverter.*;

@RestController
public class UserInfoEndpoint {

    private static final String USERNAME = "username";

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/userinfo")
    @Transactional
    public Map<String, Object> user(OAuth2Authentication authentication) {
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();
        List<String> systems = new ArrayList<>();

        Map<String, Object> map = new LinkedHashMap<>();
        if (authentication.getPrincipal() instanceof User) {
            UserEntity user = userRepository.findOneByUsernameIgnoreCase(((User) authentication.getPrincipal()).getUsername());
            map.put(NAME, user.getName());
            map.put(SURNAME, user.getSurname());
            map.put(PATRONYMIC, user.getPatronymic());
            map.put(EMAIL, user.getEmail());
            map.put(DEPARTMENT, user.getDepartment());
            map.put(ORGANIZATION, user.getOrganization());
            map.put(REGION, user.getRegion());
            map.put(USER_LEVEL, user.getUserLevel());
            if (nonNull(user.getRoleList())) {
                roles.addAll(user.getRoleList().stream().map(r -> r.getCode()).collect(Collectors.toList()));
                permissions.addAll(user.getRoleList().stream().filter(r -> nonNull(r.getPermissionList())).flatMap(r -> r.getPermissionList().stream())
                        .map(p -> p.getCode()).collect(Collectors.toList()));
                systems.addAll(user.getRoleList().stream().filter(role -> nonNull(role.getSystemCode())).
                        map(role -> (role.getSystemCode().getCode())).collect(Collectors.toList()));
                systems.addAll(user.getRoleList().stream().filter(r -> nonNull(r.getPermissionList())).flatMap(r -> r.getPermissionList().stream())
                        .filter(permission -> nonNull(permission.getSystemCode())).map(p -> p.getSystemCode().getCode()).collect(Collectors.toList()));
                systems = systems.stream().distinct().collect(Collectors.toList());
            }
        }

        map.put(USERNAME, authentication.getName());
        map.put(ROLES, roles);
        map.put(PERMISSIONS, permissions);
        map.put(SYSTEMS, systems);
        if (authentication.getUserAuthentication() != null)
            map.put(SID, ((Map<String, Object>) authentication.getUserAuthentication().getDetails()).get(SID));
        return map;
    }
}
