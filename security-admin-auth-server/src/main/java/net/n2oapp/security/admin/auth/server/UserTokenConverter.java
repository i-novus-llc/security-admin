package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.auth.common.User;
import net.n2oapp.security.auth.common.UserParamsUtil;
import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import net.n2oapp.security.auth.common.authority.SystemGrantedAuthority;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.util.*;

public class UserTokenConverter implements UserAuthenticationConverter {

    static final String ROLES = "roles";
    static final String PERMISSIONS = "permissions";
    static final String NAME = "name";
    static final String EMAIL = "email";
    static final String SURNAME = "surname";
    static final String PATRONYMIC = "patronymic";
    static final String SID = "sid";
    static final String USER = "username";
    static final String DEPARTMENT = "department";
    static final String ORGANIZATION = "organization";
    static final String REGION = "region";
    static final String USER_LEVEL = "userLevel";
    static final String SYSTEMS = "systems";

    public UserTokenConverter(List<String> tokenInclude) {
        rolesInclude = tokenInclude.contains("roles");
        permissionsInclude = tokenInclude.contains("permissions");
        systemsInclude = tokenInclude.contains("systems");
    }

    private Boolean rolesInclude;
    private Boolean permissionsInclude;
    private Boolean systemsInclude;

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            response.put(USER, user.getUsername());
            response.put(NAME, user.getName());
            response.put(DEPARTMENT, user.getDepartment());
            response.put(ORGANIZATION, user.getOrganization());
            response.put(USER_LEVEL, user.getUserLevel());
            response.put(REGION, user.getRegion());
            response.put(SURNAME, user.getSurname());
            response.put(PATRONYMIC, user.getPatronymic());
            response.put(EMAIL, user.getEmail());
            if (authentication.getDetails() instanceof Map) {
                response.put(SID, ((Map) authentication.getDetails()).get(SID));
            } else if (authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
                response.put(SID, ((OAuth2AuthenticationDetails) authentication.getDetails()).getSessionId());
            }
        } else {
            response.put(USER, authentication.getName());
        }
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            List<String> roles = new ArrayList<>();
            List<String> permissions = new ArrayList<>();
            List<String> systems = new ArrayList<>();
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority instanceof RoleGrantedAuthority)
                    roles.add(((RoleGrantedAuthority) authority).getRole());
                else if (authority instanceof PermissionGrantedAuthority)
                    permissions.add(authority.getAuthority());
                else if (authority instanceof SystemGrantedAuthority)
                    systems.add(((SystemGrantedAuthority) authority).getSystem());
            }
            if (!roles.isEmpty() && rolesInclude)
                response.put(ROLES, roles);
            if (!permissions.isEmpty() && permissionsInclude)
                response.put(PERMISSIONS, permissions);
            if (!systems.isEmpty() && systemsInclude)
                response.put(SYSTEMS, systems);
        }
        return response;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(USER)) {
            Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
            User principal = new User((String) map.get(USER), "N/A", authorities, (String) map.get(SURNAME), (String) map.get(NAME),
                    (String) map.get(PATRONYMIC), (String) map.get(EMAIL));
            principal.setDepartment((String) map.get(DEPARTMENT));
            principal.setOrganization((String) map.get(ORGANIZATION));
            principal.setUserLevel((String) map.get(USER_LEVEL));
            principal.setRegion((String) map.get(REGION));
            AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
            authentication.setDetails(map);
            return authentication;
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        return UserParamsUtil.extractAuthorities(map);
    }
}
