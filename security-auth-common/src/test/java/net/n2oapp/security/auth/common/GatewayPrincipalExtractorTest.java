package net.n2oapp.security.auth.common;

import org.junit.jupiter.api.BeforeEach;

import java.util.*;

class GatewayPrincipalExtractorTest {

    private final GatewayPrincipalExtractor extractor = new GatewayPrincipalExtractor();
    private Map<String, Object> map;


    private static final String USERNAME = "username";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String SURNAME = "surname";
    private static final String PATRONYMIC = "patronymic";
    private static final String DEPARTMENT = "department";
    private static final String CODE_KEY = "code";
    private static final String NAME_KEY = "name";
    private static final String ORGANIZATION = "organization";
    private static final String REGION = "region";
    private static final String ROLES = "roles";
    private static final String PERMISSIONS = "permissions";
    private static final String SYSTEMS = "systems";

    @BeforeEach
    public void before() {
        map = new HashMap<>();

        Map<String, Object> department = new LinkedHashMap<>();
        Map<String, Object> organization = new LinkedHashMap<>();
        Map<String, Object> region = new LinkedHashMap<>();

        List<String> roles = new ArrayList<>();
        roles.add("role");

        List<String> permissions = new ArrayList<>();
        permissions.add("permission");

        List<String> systems = new ArrayList<>();
        systems.add("system");


        department.put(CODE_KEY, "department");
        department.put(NAME_KEY, "departmentName");

        organization.put(CODE_KEY, "organization");

        region.put(CODE_KEY, "region");

        map.put(USERNAME, "username");
        map.put(NAME, "name");
        map.put(EMAIL, "email");
        map.put(SURNAME, "surname");
        map.put(PATRONYMIC, "patronymic");
        map.put(CODE_KEY, "code");

        map.put(DEPARTMENT, department);
        map.put(ORGANIZATION, organization);
        map.put(REGION, region);

        map.put(ROLES, roles);
        map.put(PERMISSIONS, permissions);
        map.put(SYSTEMS, systems);
    }

//    @Test
//    public void extractPrincipal() {
//        User user = (User) extractor.extractPrincipal(map);
//
//        assertEquals("email",user.getEmail());
//        assertEquals("name",user.getName());
//        assertEquals("surname",user.getSurname());
//        assertEquals("patronymic",user.getPatronymic());
//        assertEquals("username",user.getUsername());
//        assertEquals("department",user.getDepartment());
//        assertEquals("departmentName",user.getDepartmentName());
//        assertEquals("region",user.getRegion());
//        assertEquals("organization",user.getOrganization());
//
//        Collection<GrantedAuthority> authorities = user.getAuthorities();
//        assertTrue(authorities.stream().anyMatch(o -> o.getAuthority().equals("ROLE_role")));
//        assertTrue(authorities.stream().anyMatch(o -> o.getAuthority().equals("PERMISSION_permission")));
//        assertTrue(authorities.stream().anyMatch(o -> o.getAuthority().equals("SYSTEM_system")));
//    }
}