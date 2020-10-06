package net.n2oapp.security.auth.common;

import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import net.n2oapp.security.auth.common.authority.SystemGrantedAuthority;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.*;

import static net.n2oapp.security.auth.common.TestConstants.*;
import static org.junit.Assert.*;
import static org.springframework.security.core.context.SecurityContextHolder.clearContext;

public class UserParamsUtilTest {

    private static final String PRINCIPIAL_DEPARTMENT_NAME_ATTR = "departmentName";
    private static final String PRINCIPIAL_DEPARTMENT_ATTR = "department";
    private static final String PRINCIPIAL_CREDENTIAL_NON_EXPIRED_ATTR = "credentialsNonExpired";
    private static final String PRINCIPIAL_ROLES_ATTR = "roles";
    private static final String PRINCIPIAL_USER_SHORT_NAME_ATTR = "userShortName";
    private static final String PRINCIPIAL_USER_FULL_NAME_ATTR = "userFullName";
    private static final String PRINCIPIAL_PASSWORD_ATTR = "password";
    private static final String PRINCIPIAL_USER_NAME_SURNAME_ATTR = "userNameSurname";
    private static final String PRINCIPIAL_ORGANIZATION_ATTR = "organization";
    private static final String PRINCIPIAL_ACCOUNT_NON_EXPIRED_ATTR = "accountNonExpired";
    private static final String PRINCIPIAL_ENABLED_EXPIRED_ATTR = "enabled";
    private static final String PRINCIPIAL_USER_LEVEL_ATTR = "userLevel";
    private static final String PRINCIPIAL_REGION_ATTR = "region";
    private static final String PRINCIPIAL_CLASS_ATTR = "class";

    private static final String PRINCIPIAL_ACCOUNT_NON_LOCKED_ATTR = "accountNonLocked";
    private static final String PRINCIPIAL_AUTHORITEIES_ATTR = "authorities";

    private static final String SOME_ANONYMOUS_USERNAME = "anonymous";
    private static final String SOME_ANONYMOUS_PASSWORD = "anonymous";
    private static final String SOME_ANONYMOUS_ROLE = "ANONYMOUS";

    private static final String SOME_SESSION_ID_2 = "2";
    private static final String SOME_SESSION_ID_4 = "4";
    private static final String SOME_SESSION_ID_19 = "19";

    private static final String ROLES = "roles";
    private static final String PERMISSIONS = "permissions";
    private static final String SYSTEMS = "systems";

    private TestingAuthenticationToken testingAuthenticationToken;
    private TestingAuthenticationToken testingAuthTokenWithPrincipalIsDetails;
    private AnonymousAuthenticationToken anonymousAuthenticationToken;

    private MockHttpServletRequest request;

    private WebAuthenticationDetails authenticationDetails;

    private User testPrincipal;

    @Before
    public void setup() {

        testingAuthenticationToken = new TestingAuthenticationToken(SOME_USERNAME, SOME_PASSWORD, SOME_ROLE);

        anonymousAuthenticationToken = new AnonymousAuthenticationToken(SOME_ANONYMOUS_USERNAME, SOME_ANONYMOUS_PASSWORD,
                AuthorityUtils.createAuthorityList(SOME_ANONYMOUS_ROLE));

        request = new MockHttpServletRequest();
        request.setSession(new MockHttpSession());
        authenticationDetails = new WebAuthenticationDetails(request);

        testPrincipal = new User(SOME_USERNAME, SOME_PASSWORD, Collections.singleton(SOME_GRANTED_AUTHORITY));
        testPrincipal.setSurname(SOME_SURNAME);
        testPrincipal.setName(SOME_NAME);
        testPrincipal.setPatronymic(SOME_PATRONYMIC);
        testPrincipal.setDepartment(SOME_DEPARTMENT);
        testPrincipal.setDepartmentName(SOME_DEPARTMENT_NAME);
        testPrincipal.setOrganization(SOME_ORGANIZATION);
        testPrincipal.setUserLevel(SOME_USER_LEVEL);
        testPrincipal.setRegion(SOME_REGION);
        testPrincipal.setEmail(SOME_EMAIL);

        testingAuthTokenWithPrincipalIsDetails = new TestingAuthenticationToken(testPrincipal, SOME_PASSWORD);
    }

    @After
    public void cleanup() {
        clearContext();
    }

    @Test
    public void testGetSessionId_whenNullContext() {
        assertEquals(StringUtils.EMPTY, UserParamsUtil.getSessionId());
    }

    @Test
    public void testGetSessionId_withTestingAuthDetails() {
        testingAuthenticationToken.setDetails(authenticationDetails);
        SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);
        assertEquals(SOME_SESSION_ID_19, UserParamsUtil.getSessionId());
    }

    @Test
    public void testGetSessionId_withAnonymousAuthDetails() {
        anonymousAuthenticationToken.setDetails(authenticationDetails);
        SecurityContextHolder.getContext().setAuthentication(anonymousAuthenticationToken);
        assertEquals(SOME_SESSION_ID_4, UserParamsUtil.getSessionId());
    }

    @Test
    public void testGetSessionId_withOutAuthDetails() {
        testingAuthenticationToken.setDetails(authenticationDetails);
        assertEquals(StringUtils.EMPTY, UserParamsUtil.getSessionId());
    }

    @Test
    public void testGetSessionId_withArg() {
        testingAuthenticationToken.setDetails(authenticationDetails);
        assertEquals(SOME_SESSION_ID_2, UserParamsUtil.getSessionId(testingAuthenticationToken));
    }

    @Test
    public void testGetSessionId_whenPrincipalIsDetails() {
        testingAuthenticationToken.setDetails(testPrincipal);
        assertEquals(StringUtils.EMPTY, UserParamsUtil.getSessionId(testingAuthenticationToken));
    }

    @Test
    public void testGetUsername_whenNullContext() {
        assertEquals(StringUtils.EMPTY, UserParamsUtil.getUsername());
    }

    @Test
    public void testGetUsername_whenNullAuth() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertEquals(StringUtils.EMPTY, UserParamsUtil.getUsername());
    }

    @Test
    public void testGetUsername_whenAnonymousAuth() {
        anonymousAuthenticationToken.setDetails(authenticationDetails);
        SecurityContextHolder.getContext().setAuthentication(anonymousAuthenticationToken);
        assertEquals(StringUtils.EMPTY, UserParamsUtil.getUsername());
    }

    @Test
    public void testGetUsername_whenPrincipalIsString() {
        SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);
        assertEquals(SOME_USERNAME, UserParamsUtil.getUsername());
    }

    @Test
    public void testGetUsername_whenPrincipalIsDetails() {
        SecurityContextHolder.getContext().setAuthentication(testingAuthTokenWithPrincipalIsDetails);
        assertEquals(SOME_USERNAME, UserParamsUtil.getUsername());
    }

    @Test
    public void testGetUserDetails_whenNullContext() {
        assertNull(UserParamsUtil.getUserDetails());
    }

    @Test
    public void testGetUserDetails_whenNullAuth() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertNull(UserParamsUtil.getUserDetails());
    }

    @Test
    public void testGetUserDetails_whenPrincipalIsDetails() {
        SecurityContextHolder.getContext().setAuthentication(testingAuthTokenWithPrincipalIsDetails);
        assertEquals(testPrincipal, UserParamsUtil.getUserDetails());
    }

    @Test
    public void testGetUserDetails_withTestingAuthDetails() {
        initSecurityContextWithPrincipal();
        assertEquals(testPrincipal, UserParamsUtil.getUserDetails());
    }

    @Test
    public void testGetUserDetails_whenNullUserDetails() {
        testingAuthenticationToken.setDetails(null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);
        assertNull(UserParamsUtil.getUserDetails());
    }

    @Test
    public void testGetUserDetailsAsMap_withNullArg() {
        Map<String, Object> map = UserParamsUtil.getUserDetailsAsMap(null);
        assertTrue(map.isEmpty());
    }

    @Test
    public void testGetUserDetailsAsMap_withUserDetailsArg() {
        Map<String, Object> map = UserParamsUtil.getUserDetailsAsMap(testPrincipal);
        assertUserDetailsMap(map);
    }

    @Test
    public void testGetUserDetailsAsMap_withoutArg() {
        initSecurityContextWithPrincipal();

        Map<String, Object> map = UserParamsUtil.getUserDetailsAsMap();
        assertUserDetailsMap(map);
    }

    @Test
    public void testGetUserDetailsProperty() {
        User user = new User(SOME_USERNAME);
        user.setSurname(SOME_SURNAME);
        assertEquals(SOME_SURNAME, UserParamsUtil.getUserDetailsProperty(user, PRINCIPIAL_SURNAME_ATTR));
    }

    @Test
    public void testGetUserDetailsProperty_whenNullUserDetails() {
        assertNull(UserParamsUtil.getUserDetailsProperty(null, PRINCIPIAL_SURNAME_ATTR));
    }

    @Test
    public void testGetUserDetailsProperty_withOnceArg() {
        initSecurityContextWithPrincipal();
        assertEquals(SOME_SURNAME, UserParamsUtil.getUserDetailsProperty(PRINCIPIAL_SURNAME_ATTR));
    }

    @Test
    public void testExtractAuthorities() {

        Map map = new HashMap<String, List<String>>();
        map.put(ROLES, Arrays.asList(SOME_ROLE));
        map.put(PERMISSIONS, Arrays.asList(SOME_PERMISSION));
        map.put(SYSTEMS, Arrays.asList(SOME_SYSTEM));

        List<GrantedAuthority> authorities = UserParamsUtil.extractAuthorities(map);
        assertFalse(authorities.isEmpty());

        for (GrantedAuthority authority : authorities) {

            if (authority instanceof RoleGrantedAuthority) {
                assertEquals(new RoleGrantedAuthority(SOME_ROLE), authority);
            } else if (authority instanceof PermissionGrantedAuthority) {
                assertEquals(new PermissionGrantedAuthority(SOME_PERMISSION), authority);
            } else if (authority instanceof SystemGrantedAuthority) {
                assertEquals(new SystemGrantedAuthority(SOME_SYSTEM), authority);
            } else {
                fail();
            }
        }
    }

    private void initSecurityContextWithPrincipal() {
        testingAuthenticationToken.setDetails(testPrincipal);
        SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);
    }

    private void assertUserDetailsMap(Map<String, Object> map) {
        assertNotNull(map);

        assertEquals(SOME_DEPARTMENT_NAME, map.get(PRINCIPIAL_DEPARTMENT_NAME_ATTR));
        assertEquals(SOME_DEPARTMENT, map.get(PRINCIPIAL_DEPARTMENT_ATTR));
        assertEquals(Boolean.TRUE, map.get(PRINCIPIAL_CREDENTIAL_NON_EXPIRED_ATTR));
        assertEquals(SOME_ROLE_LIST, map.get(PRINCIPIAL_ROLES_ATTR));
        assertEquals(getUserFullName(), map.get(PRINCIPIAL_USER_FULL_NAME_ATTR));
        assertEquals(SOME_AUTHORITEIES, map.get(PRINCIPIAL_AUTHORITEIES_ATTR));
        assertEquals(Boolean.TRUE, map.get(PRINCIPIAL_ENABLED_EXPIRED_ATTR));
        assertEquals(getUserShortName(), map.get(PRINCIPIAL_USER_SHORT_NAME_ATTR));
        assertEquals(SOME_PASSWORD, map.get(PRINCIPIAL_PASSWORD_ATTR));
        assertEquals(SOME_SURNAME, map.get(PRINCIPIAL_SURNAME_ATTR));
        assertEquals(SOME_NAME, map.get(PRINCIPIAL_NAME_ATTR));
        assertEquals(SOME_PATRONYMIC, map.get(PRINCIPIAL_PATRONYMIC_ATTR));
        assertEquals(SOME_USER_LEVEL, map.get(PRINCIPIAL_USER_LEVEL_ATTR));
        assertEquals(new ArrayList<>(), map.get(PRINCIPIAL_PERMISSIONS_ATTR));
        assertEquals(SOME_ORGANIZATION, map.get(PRINCIPIAL_ORGANIZATION_ATTR));
        assertEquals(Boolean.TRUE, map.get(PRINCIPIAL_ACCOUNT_NON_EXPIRED_ATTR));
        assertEquals(SOME_REGION, map.get(PRINCIPIAL_REGION_ATTR));
        assertEquals(User.class, map.get(PRINCIPIAL_CLASS_ATTR));
        assertEquals(getUserNameSurname(), map.get(PRINCIPIAL_USER_NAME_SURNAME_ATTR));
        assertEquals(SOME_EMAIL, map.get(PRINCIPIAL_EMAIL_ATTR));
        assertEquals(Boolean.TRUE, map.get(PRINCIPIAL_ACCOUNT_NON_LOCKED_ATTR));
        assertEquals(SOME_USERNAME, map.get(PRINCIPIAL_USERNAME_ATTR));
    }
}