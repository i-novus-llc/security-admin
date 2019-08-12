package net.n2oapp.framework.security.auth.oauth.gateway;

import net.n2oapp.security.auth.common.User;
import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GatewayResourceExtractor implements PrincipalExtractor, AuthoritiesExtractor {
    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (map.containsKey("roles")) {
            for (String role : (List<String>) map.get("roles")) {
                authorities.add(new RoleGrantedAuthority(role));
            }
        }
        if (map.containsKey("permissions")) {
            for (String role : (List<String>) map.get("permissions")) {
                authorities.add(new PermissionGrantedAuthority(role));
            }
        }
        return authorities;
    }

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        return new User((String) map.get("username"), "N/A", extractAuthorities(map), (String) map.get("surname"), (String) map.get("name"),
                (String) map.get("patronymic"), (String) map.get("email"));
    }
}
