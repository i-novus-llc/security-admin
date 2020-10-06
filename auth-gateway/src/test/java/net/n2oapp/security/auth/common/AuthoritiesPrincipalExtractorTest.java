package net.n2oapp.security.auth.common;

import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.impl.service.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static net.n2oapp.security.auth.common.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class AuthoritiesPrincipalExtractorTest {

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    private User testPrincipal;

    @Before
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

        net.n2oapp.security.auth.common.User user = (net.n2oapp.security.auth.common.User) getExtractor().extractPrincipal(map);

        assertNotNull(user);

        assertEquals(user.getUsername(), SOME_USERNAME);
        assertEquals(user.getEmail(), SOME_EMAIL);
        assertEquals(user.getSurname(), SOME_SURNAME);
        assertEquals(user.getName(), SOME_NAME);
        assertEquals(user.getPatronymic(), SOME_PATRONYMIC);
        assertEquals(user.getDepartment(), SOME_DEPARTMENT_CODE);
        assertEquals(user.getDepartmentName(), SOME_DEPARTMENT_NAME);
        assertEquals(user.getUserShortName(), getUserShortName());
        assertEquals(user.getUserFullName(), getUserFullName());
        assertEquals(user.isEnabled(), Boolean.TRUE);
        assertEquals(user.isAccountNonExpired(), Boolean.TRUE);
        assertEquals(user.getPassword(), VALUE_NOT_AVAILABLE);
        assertEquals(user.getOrganization(), SOME_ORGANIZATION_CODE);
        assertEquals(user.getUserLevel(), SOME_USER_LEVEL);
        assertEquals(user.getRegion(), SOME_REGION_CODE);
        assertEquals(user.getUserNameSurname(), getUserNameSurname());
        assertEquals(user.getRoles(), SOME_ROLE_LIST);
        assertEquals(user.getAuthorities(), SOME_AUTHORITEIES);

        List<String> permissions = user.getPermissions();

        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());
    }

    @Test
    public void testExtractPrincipal_whenNullUserName() {

        Map<String, Object> map = new HashMap<>();

        map.put(PRINCIPIAL_USERNAME_ATTR, null);

        net.n2oapp.security.auth.common.User user = (net.n2oapp.security.auth.common.User) getExtractor().extractPrincipal(map);

        assertNull(user);
    }

    @Test
    public void testExtractAuthorities_withGrantedAuthorityKey() {

        Map<String, Object> map = new HashMap<>();
        map.put(GRANTED_AUTHORITY_KEY, Arrays.asList(SOME_GRANTED_AUTHORITY));

        List<GrantedAuthority> authorities = getExtractor().extractAuthorities(map);
        assertNotNull(authorities);
        assertEquals(authorities.size(), 1);

        assertEquals(authorities.get(0), SOME_GRANTED_AUTHORITY);
    }

    @Test
    public void testSetPrincipalKeys() {

        AuthoritiesPrincipalExtractor extractor = getExtractor();

        extractor.setPrincipalKeys(new String[]{PRINCIPIAL_OTHER_USERNAME_ATTR});

        Map<String, Object> map = new HashMap<>();
        map.put(PRINCIPIAL_OTHER_USERNAME_ATTR, SOME_USERNAME);

        net.n2oapp.security.auth.common.User user = (net.n2oapp.security.auth.common.User) extractor.extractPrincipal(map);

        assertNotNull(user);

        assertEquals(user.getUsername(), SOME_USERNAME);
    }

    private AuthoritiesPrincipalExtractor getExtractor() {

        AuthoritiesPrincipalExtractor extractor = new AuthoritiesPrincipalExtractor(userDetailsService, SOME_SYSTEM);

        assertNotNull(extractor);

        return extractor;
    }
}
