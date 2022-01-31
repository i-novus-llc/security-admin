package net.n2oapp.security.auth.common;

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

import static java.util.Objects.nonNull;
import static net.n2oapp.security.auth.common.UserParamsUtil.extractFromMap;

public class UserTokenConverter implements UserAuthenticationConverter {

    public static final String ROLES = "roles";
    public static final String PERMISSIONS = "permissions";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String SURNAME = "surname";
    public static final String PATRONYMIC = "patronymic";
    public static final String SID = "sid";
    public static final String USER = "username";
    public static final String DEPARTMENT = "department";
    public static final String ORGANIZATION = "organization";
    public static final String REGION = "region";
    public static final String USER_LEVEL = "userLevel";
    public static final String SYSTEMS = "systems";

    private final UserAttributeKeys userAttributeKeys;
    private final Boolean includeRoles;
    private final Boolean includePermissions;
    private final Boolean includeSystems;

    public UserTokenConverter(UserAttributeKeys userAttributeKeys, Boolean includeRoles, Boolean includePermissions, Boolean includeSystems) {
        this.userAttributeKeys = userAttributeKeys;
        this.includeRoles = includeRoles;
        this.includePermissions = includePermissions;
        this.includeSystems = includeSystems;
    }

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            response.computeIfAbsent(USER, value -> user.getUsername());
            response.computeIfAbsent(NAME, value -> user.getName());
            response.computeIfAbsent(DEPARTMENT, value -> user.getDepartment());
            response.computeIfAbsent(ORGANIZATION, value -> user.getOrganization());
            response.computeIfAbsent(USER_LEVEL, value -> user.getUserLevel());
            response.computeIfAbsent(REGION, value -> user.getRegion());
            response.computeIfAbsent(SURNAME, value -> user.getSurname());
            response.computeIfAbsent(PATRONYMIC, value -> user.getPatronymic());
            response.computeIfAbsent(EMAIL, value -> user.getEmail());
            if (authentication.getDetails() instanceof Map) {
                response.put(SID, ((Map) authentication.getDetails()).get(SID));
            } else if (authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
                response.put(SID, ((OAuth2AuthenticationDetails) authentication.getDetails()).getSessionId());
            }
        } else {
            response.computeIfAbsent(USER, value -> authentication.getName());
        }
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            List<String> roles = new ArrayList<>();
            List<String> permissions = new ArrayList<>();
            List<String> systems = new ArrayList<>();
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority instanceof RoleGrantedAuthority)
                    roles.add(((RoleGrantedAuthority) authority).getRole());
                else if (authority instanceof PermissionGrantedAuthority)
                    permissions.add(((PermissionGrantedAuthority) authority).getPermission());
                else if (authority instanceof SystemGrantedAuthority)
                    systems.add(((SystemGrantedAuthority) authority).getSystem());
            }
            if (!roles.isEmpty() && Boolean.TRUE.equals(includeRoles))
                response.put(ROLES, roles);
            if (!permissions.isEmpty() && Boolean.TRUE.equals(includePermissions))
                response.put(PERMISSIONS, permissions);
            if (!systems.isEmpty() && Boolean.TRUE.equals(includeSystems))
                response.put(SYSTEMS, systems);
        }
        return response;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (nonNull(extractFromMap(userAttributeKeys.principal, map))) {
            Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
            User principal = new User((String) extractFromMap(userAttributeKeys.principal, map), "N/A", authorities, (String) extractFromMap(userAttributeKeys.surname, map), (String) extractFromMap(userAttributeKeys.name, map),
                    (String) extractFromMap(userAttributeKeys.patronymic, map), (String) extractFromMap(userAttributeKeys.email, map));
            principal.setDepartment((String) extractFromMap(userAttributeKeys.department, map));
            principal.setOrganization((String) extractFromMap(userAttributeKeys.organization, map));
            principal.setUserLevel((String) extractFromMap(userAttributeKeys.userLevel, map));
            principal.setRegion((String) extractFromMap(userAttributeKeys.region, map));
            // todo SECURITY-396 (исправить после выбора контекста с ui)
            principal.setAccountId(1);
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
