package net.n2oapp.security.auth.common;

import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static net.n2oapp.security.auth.common.UserParamsUtil.extractFromMap;

public class KeycloakUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private OidcUserService delegateOidcUserService = new OidcUserService();

    private final UserAttributeKeys userAttributeKeys;
    private final UserDetailsService userDetailsService;
    private List<String> principalKeys;
    private final String externalSystem;

    public KeycloakUserService(UserAttributeKeys userAttributeKeys, UserDetailsService userDetailsService, String externalSystem) {
        this.userAttributeKeys = userAttributeKeys;
        this.userDetailsService = userDetailsService;
        this.externalSystem = externalSystem;
        principalKeys = userAttributeKeys.principal;
    }

    @Override
    public OauthUser loadUser(OidcUserRequest userRequest) {
        // Delegate to the default implementation for loading a user
        DefaultOidcUser defaultOidcUser = (DefaultOidcUser) delegateOidcUserService.loadUser(userRequest);

        User user = getUser(defaultOidcUser.getAttributes());
        OauthUser oauthUser = new OauthUser(user.getUsername(), defaultOidcUser.getIdToken());
        oauthUser.setSurname(user.getSurname());
        oauthUser.setFirstName(user.getName());
        oauthUser.setPatronymic(user.getPatronymic());
        oauthUser.setEmail(user.getEmail());
        oauthUser.setUsername(user.getUsername());

        if (nonNull(user.getDepartment())) {
            oauthUser.setDepartment(user.getDepartment().getCode());
            oauthUser.setDepartmentName(user.getDepartment().getName());
        }
        if (nonNull(user.getOrganization())) {
            oauthUser.setOrganization(user.getOrganization().getCode());
        }
        if (nonNull(user.getRegion())) {
            oauthUser.setRegion(user.getRegion().getCode());
        }
        if (nonNull(user.getUserLevel())) {
            oauthUser.setUserLevel(user.getUserLevel().toString());
        }

        return oauthUser;
    }

    protected User getUser(Map<String, Object> map) {
        Object usernameObj = extractFromMap(principalKeys, map);
        if (usernameObj == null)
            return null;
        Object roles = extractFromMap(userAttributeKeys.authorities, map);
        List<String> roleList = new ArrayList<>();
        if (roles instanceof Collection)
            roleList = new ArrayList<>((Collection<String>) roles);
        String username = (String) usernameObj;
        String surname = (String) extractFromMap(userAttributeKeys.surname, map);
        String name = (String) extractFromMap(userAttributeKeys.name, map);
        String email = (String) extractFromMap(userAttributeKeys.email, map);
        String patronymic = (String) extractFromMap(userAttributeKeys.patronymic, map);

        UserDetailsToken token = new UserDetailsToken();
        token.setUsername(username);
        token.setRoleNames(roleList);
        token.setExtUid((String) extractFromMap(userAttributeKeys.guid, map));
        token.setName(name);
        token.setSurname(surname);
        token.setPatronymic(patronymic);
        token.setEmail(email);
        token.setExternalSystem(externalSystem);
        return userDetailsService.loadUserDetails(token);
    }

    public KeycloakUserService setPrincipalKeys(List<String> pKeys) {
        principalKeys = pKeys;
        return this;
    }

    public void setDelegateOidcUserService(OidcUserService delegateOidcUserService) {
        this.delegateOidcUserService = delegateOidcUserService;
    }
}
