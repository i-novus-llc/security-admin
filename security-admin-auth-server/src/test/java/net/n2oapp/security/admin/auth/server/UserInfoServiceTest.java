package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.admin.api.model.Department;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.auth.server.oauth.*;
import net.n2oapp.security.admin.impl.entity.*;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.auth.common.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Тестирование сервиса по наполнению userinfo
 */
public class UserInfoServiceTest {


    @Test
    public void testUserInfoService() {
        UserRepository repository = mock(UserRepository.class);

        when(repository.findOneByUsernameIgnoreCase("testUser")).thenReturn(initUserEntity());
        UserInfoService userInfoService = new UserInfoService(repository,
                List.of(new OrganizationEnricher(), new RolesEnricher(), new SystemsEnricher(true),
                        new RegionEnricher(), new DepartmentEnricher(), new PermissionsEnricher(true),
                        new SimpleUserDataEnricher()));
        OAuth2Authentication authentication = mock(OAuth2Authentication.class);
        User user = new User("testUser");
        when(authentication.getPrincipal()).thenReturn(user);

        Map<String, Object> userinfo = userInfoService.buildUserInfo(authentication);
        assertThat(userinfo.get("patronymic"), is("testPatronymic"));
        assertThat(userinfo.get("surname"), is("testSurname"));
        assertThat(userinfo.get("name"), is("testName"));
        assertThat(userinfo.get("email"), is("testEmail"));
        assertThat(((List<String>) userinfo.get("systems")).get(0), is("testSystemCode"));
        assertThat(((Organization) userinfo.get("organization")).getId(), is(1));
        assertThat(((Organization) userinfo.get("organization")).getCode(), is("testCode"));
        assertThat(((Organization) userinfo.get("organization")).getInn(), is("testInn"));
        assertThat(((List<String>) userinfo.get("roles")).get(0), is("testRoleCode"));
        assertThat(((Department) userinfo.get("department")).getId(), is(1));
        assertThat(((Department) userinfo.get("department")).getCode(), is("testCode"));
        assertThat(((Department) userinfo.get("department")).getName(), is("testName"));
        assertThat(((Region) userinfo.get("region")).getId(), is(1));
        assertThat(((Region) userinfo.get("region")).getName(), is("testName"));
        assertThat(((Region) userinfo.get("region")).getCode(), is("testCode"));
        assertThat(((Region) userinfo.get("region")).getOkato(), is("testOkato"));
    }

    @Test
    public void enricherConfigurerTest() {
        UserInfoEnrichmentConfiguration.UserInfoEnrichersConfigurer configurer = new UserInfoEnrichmentConfiguration
                .UserInfoEnrichersConfigurer(Set.of("test1", "test2"), Arrays.asList(
                new UserInfoEnricher<>() {
                    @Override
                    public Object buildValue(UserEntity source) {
                        return null;
                    }

                    @Override
                    public String getAlias() {
                        return "test0";
                    }
                },
                new UserInfoEnricher<>() {

                    @Override
                    public Object buildValue(UserEntity source) {
                        return null;
                    }

                    @Override
                    public String getAlias() {
                        return "test1";
                    }
                }
        ));
        List<UserInfoEnricher<UserEntity>> configured = configurer.configure();
        assertThat(configured.size(), is(1));
        assertThat(configured.get(0).getAlias(), is("test1"));
    }

    private UserEntity initUserEntity() {
        UserEntity entity = new UserEntity();
        entity.setUsername("testUser");
        entity.setName("testName");
        entity.setSurname("testSurname");
        entity.setPatronymic("testPatronymic");
        entity.setEmail("testEmail");
        entity.setPatronymic("testPatronymic");
        DepartmentEntity department = new DepartmentEntity();
        department.setId(1);
        department.setCode("testCode");
        department.setName("testName");
        //        todo SECURITY-396
//        entity.setDepartment(department);
//        entity.setUserLevel(UserLevel.PERSONAL);
        RoleEntity role = new RoleEntity();
        role.setId(1);
        role.setCode("testRoleCode");
        role.setName("testRoleName");
        PermissionEntity permission = new PermissionEntity();
        permission.setCode("testPermissionCode");
        permission.setName("testPermissionName");
        SystemEntity system = new SystemEntity();
        system.setCode("testSystemCode");
        system.setName("testSystemName");
        permission.setSystemCode(system);
        role.setPermissionList(Collections.singletonList(permission));
        //        todo SECURITY-396
//        entity.setRoleList(Collections.singletonList(role));
        OrganizationEntity organization = new OrganizationEntity();
        organization.setId(1);
        organization.setInn("testInn");
        organization.setCode("testCode");
        organization.setKpp("testKpp");
//        todo SECURITY-396
//        entity.setOrganization(organization);

        RegionEntity regionEntity = new RegionEntity();
        regionEntity.setId(1);
        regionEntity.setName("testName");
        regionEntity.setCode("testCode");
        regionEntity.setOkato("testOkato");
        //        todo SECURITY-396
//        entity.setRegion(regionEntity);

        return entity;
    }
}
