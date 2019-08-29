package net.n2oapp.security.admin;

import net.n2oapp.security.auth.common.User;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.n2oapp.security.admin.UserTokenConverter.*;

@RestController
public class UserInfoEndpoint {

    private static final String USERNAME = "username";

    @RequestMapping(value = "/userinfo")
    public Map<String, Object> user(OAuth2Authentication authentication) {
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        authentication.getAuthorities().forEach(authority -> {
            if (authority instanceof RoleGrantedAuthority)
                roles.add(((RoleGrantedAuthority) authority).getRole());
            else
                permissions.add(authority.getAuthority());
        });

        Map<String, Object> map = new LinkedHashMap<>();
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            map.put(NAME, user.getName());
            map.put(SURNAME, user.getSurname());
            map.put(PATRONYMIC, user.getPatronymic());
            map.put(EMAIL, user.getEmail());

        }

        map.put(USERNAME, authentication.getName());
        map.put(ROLES, roles);
        map.put(PERMISSIONS, permissions);
        map.put(SID, ((Map<String, Object>) authentication.getUserAuthentication().getDetails()).get(SID));
        return map;
    }
}
