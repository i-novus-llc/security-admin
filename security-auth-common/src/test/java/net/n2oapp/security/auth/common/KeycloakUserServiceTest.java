package net.n2oapp.security.auth.common;

import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.*;

import static net.n2oapp.security.auth.common.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PropertySourceAutoConfiguration.class)
public class KeycloakUserServiceTest {

    @MockBean
    private UserDetailsService userDetailsService;

    private User testPrincipal;

    @Autowired
    private UserAttributeKeys userAttributeKeys;

    @BeforeEach
    public void setup() {

        Department department = new Department();
        department.setId(1);
        department.setCode(SOME_DEPARTMENT_CODE);
        department.setName(SOME_DEPARTMENT_NAME);

        Region region = new Region();
        region.setId(1);
        region.setCode(SOME_REGION_CODE);
        region.setName(SOME_REGION);

        Organization organization = new Organization();
        organization.setId(1);
        organization.setCode(SOME_ORGANIZATION_CODE);
        organization.setFullName(SOME_ORGANIZATION);

        Role role = new Role();
        role.setCode(SOME_ROLE);

        testPrincipal = new User();
        testPrincipal.setId(1);
        testPrincipal.setUsername(SOME_USERNAME);
        testPrincipal.setSurname(SOME_SURNAME);
        testPrincipal.setName(SOME_NAME);
        testPrincipal.setPatronymic(SOME_PATRONYMIC);
        testPrincipal.setDepartment(department);
        testPrincipal.setEmail(SOME_EMAIL);
        testPrincipal.setIsActive(Boolean.TRUE);
        testPrincipal.setPassword(SOME_PASSWORD);
        testPrincipal.setOrganization(organization);
        testPrincipal.setRegion(region);
        testPrincipal.setRoles(Arrays.asList(role));
        testPrincipal.setUserLevel(UserLevel.PERSONAL);

        Mockito.doReturn(testPrincipal).when(userDetailsService).loadUserDetails(Mockito.any());
    }

    @Test
    public void testExtractPrincipal() {

        Map<String, Object> map = new HashMap<>();

        map.put(PRINCIPIAL_ID_ATTR, String.valueOf(1));
        map.put(PRINCIPIAL_USERNAME_ATTR, SOME_USERNAME);
        map.put(PRINCIPIAL_SURNAME_ATTR, SOME_SURNAME);
        map.put(PRINCIPIAL_NAME_ATTR, SOME_NAME);
        map.put(PRINCIPIAL_PATRONYMIC_ATTR, SOME_PATRONYMIC);
        map.put(PRINCIPIAL_EMAIL_ATTR, SOME_EMAIL);

        OauthUser user = getExtractor().loadUser(prepareUserRequest(map));

        assertNotNull(user);

        assertEquals(SOME_USERNAME, user.getUsername());
        assertEquals(SOME_EMAIL, user.getEmail());
        assertEquals(SOME_SURNAME, user.getSurname());
        assertEquals(SOME_NAME, user.getFirstName());
        assertEquals(SOME_PATRONYMIC, user.getPatronymic());
        assertEquals(SOME_DEPARTMENT_CODE, user.getDepartment());
        assertEquals(SOME_DEPARTMENT_NAME, user.getDepartmentName());
        assertEquals(getUserShortName(), user.getUserShortName());
        assertEquals(getUserFullName(), user.getUserFullName());
//        assertEquals(Boolean.TRUE, user.isEnabled());
//        assertEquals(Boolean.TRUE, user.isAccountNonExpired());
//        assertEquals(VALUE_NOT_AVAILABLE, user.getPassword());
        assertEquals(SOME_ORGANIZATION_CODE, user.getOrganization());
        assertEquals(SOME_USER_LEVEL, user.getUserLevel());
        assertEquals(SOME_REGION_CODE, user.getRegion());
        assertEquals(getUserNameSurname(), user.getUserNameSurname());
        assertEquals(SOME_ROLE_LIST, user.getRoles());
        assertEquals(SOME_AUTHORITEIES, user.getAuthorities());

        List<String> permissions = user.getPermissions();

        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());
    }

    @Test
    public void testSetPrincipalKeys() {

        KeycloakUserService extractor = getExtractor();

        extractor.setPrincipalKeys(Collections.singletonList(PRINCIPIAL_OTHER_USERNAME_ATTR));

        Map<String, Object> map = new HashMap<>();
        map.put(PRINCIPIAL_OTHER_USERNAME_ATTR, SOME_USERNAME);

        OauthUser user = extractor.loadUser(prepareUserRequest(map));

        assertNotNull(user);

        assertEquals(SOME_USERNAME, user.getUsername());
    }

    private KeycloakUserService getExtractor() {

        KeycloakUserService extractor = new KeycloakUserService(userAttributeKeys, userDetailsService, SOME_SYSTEM);
        extractor.setDelegateOidcUserService(new OidcUserService() {
            @Override
            public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
                OauthUser oauthUser = new OauthUser(null, null, userRequest.getIdToken(), new OidcUserInfo(userRequest.getIdToken().getClaims()));
                return oauthUser;
            }
        });

        assertNotNull(extractor);

        return extractor;
    }

    private OidcUserRequest prepareUserRequest(Map map) {
        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token_vaue", Instant.MIN, Instant.MAX);
        map.putIfAbsent("sub", "sub");
        OidcIdToken oidcIdToken = new OidcIdToken("token_value", Instant.MIN, Instant.MAX, map);
        return new OidcUserRequest(
                ClientRegistration
                        .withRegistrationId("test")
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .clientId("test")
                        .clientSecret("test")
                        .tokenUri("test").build(), oAuth2AccessToken, oidcIdToken);
    }
}
