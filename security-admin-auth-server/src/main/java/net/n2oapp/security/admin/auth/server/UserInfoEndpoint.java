package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.auth.common.User;
import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import net.n2oapp.security.auth.common.authority.SystemGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.n2oapp.security.admin.auth.server.UserTokenConverter.*;

@RestController
public class UserInfoEndpoint {

    private static final String USERNAME = "username";

    @RequestMapping(value = "/userinfo")
    public Map<String, Object> user(OAuth2Authentication authentication) {
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();
        List<String> systems = new ArrayList<>();

        authentication.getAuthorities().forEach(authority -> {
            if (authority instanceof RoleGrantedAuthority)
                roles.add(((RoleGrantedAuthority) authority).getRole());
            else if (authority instanceof PermissionGrantedAuthority)
                permissions.add(((PermissionGrantedAuthority) authority).getPermission());
            else if (authority instanceof SystemGrantedAuthority)
                systems.add(((SystemGrantedAuthority) authority).getSystem());
        });

        Map<String, Object> map = new LinkedHashMap<>();
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            map.put(NAME, user.getName());
            map.put(SURNAME, user.getSurname());
            map.put(PATRONYMIC, user.getPatronymic());
            map.put(EMAIL, user.getEmail());
            map.put(DEPARTMENT, user.getDepartment());
            map.put(ORGANIZATION, user.getOrganization());
            map.put(REGION, user.getRegion());
            map.put(USER_LEVEL, user.getUserLevel());
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
