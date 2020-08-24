package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.auth.server.oauth.OrganizationEnricher;
import net.n2oapp.security.admin.auth.server.oauth.RoleEnricher;
import net.n2oapp.security.admin.auth.server.oauth.UserInfoService;
import net.n2oapp.security.admin.impl.entity.*;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.auth.common.User;
import org.junit.Test;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.isNotNull;
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
        UserInfoService userInfoService = new UserInfoService(repository, true, List.of(new OrganizationEnricher(),
                new RoleEnricher()));
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
    }

    @Test
    public void enricherConfigurerTest() {
        UserInfoEnrichmentConfiguration.UserInfoEnrichersConfigurer configurer = new UserInfoEnrichmentConfiguration
                .UserInfoEnrichersConfigurer(Set.of("test1", "test2"), Arrays.asList(
                new UserInfoEnricher<>() {

                    @Override
                    public Object buildValue(Object source) {
                        return null;
                    }

                    @Override
                    public String getAlias() {
                        return "test0";
                    }
                },
                new UserInfoEnricher<>() {

                    @Override
                    public Object buildValue(Object source) {
                        return null;
                    }

                    @Override
                    public String getAlias() {
                        return "test1";
                    }
                }
        ));
        List<UserInfoEnricher<?>> configured = configurer.configure();
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
        entity.setDepartment(department);
        entity.setUserLevel(UserLevel.PERSONAL);
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
        entity.setRoleList(Collections.singletonList(role));
        OrganizationEntity organization = new OrganizationEntity();
        organization.setId(1);
        organization.setInn("testInn");
        organization.setCode("testCode");
        organization.setKpp("testKpp");
        entity.setOrganization(organization);
        return entity;
    }
}
