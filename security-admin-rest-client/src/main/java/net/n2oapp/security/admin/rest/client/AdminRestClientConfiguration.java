package net.n2oapp.security.admin.rest.client;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.security.admin.rest.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

@EnableJaxRsProxyClient(
        classes = {UserRestService.class, AccountRestService.class, RoleRestService.class, PermissionRestService.class,
                SystemRestService.class, UserDetailsRestService.class,
                RegionRestService.class, OrganizationRestService.class, DepartmentRestService.class,
                UserLevelRestService.class, AccountTypeRestService.class},
        address = "${access.service.url}")
public class AdminRestClientConfiguration {

    @Bean
    public UserServiceRestClient userService(@Qualifier("userRestServiceJaxRsProxyClient") UserRestService client) {
        return new UserServiceRestClient(client);
    }

    @Bean
    public AccountServiceRestClient accountService(@Qualifier("accountRestServiceJaxRsProxyClient") AccountRestService client) {
        return new AccountServiceRestClient(client);
    }

    @Bean
    public UserDetailsServiceRestClient userDetailsService(@Qualifier("userDetailsRestServiceJaxRsProxyClient") UserDetailsRestService client) {
        return new UserDetailsServiceRestClient(client);
    }

    @Bean
    public RoleServiceRestClient roleService(@Qualifier("roleRestServiceJaxRsProxyClient") RoleRestService client) {
        return new RoleServiceRestClient(client);
    }

    @Bean
    public UserLevelServiceRestClient userLevelService(@Qualifier("userLevelRestServiceJaxRsProxyClient") UserLevelRestService client) {
        return new UserLevelServiceRestClient(client);
    }

    @Bean
    public DepartmentServiceRestClient departmentService(@Qualifier("departmentRestServiceJaxRsProxyClient") DepartmentRestService client) {
        return new DepartmentServiceRestClient(client);
    }

    @Bean
    public OrganizationServiceRestClient organizationService(@Qualifier("organizationRestServiceJaxRsProxyClient") OrganizationRestService organizationRestService,
                                                             @Autowired(required = false) @Qualifier("organizationPersistRestServiceJaxRsProxyClient") OrganizationPersistRestService organizationPersistRestService) {
        return new OrganizationServiceRestClient(organizationRestService, organizationPersistRestService);
    }

    @Bean
    public RegionServiceRestClient regionService(@Qualifier("regionRestServiceJaxRsProxyClient") RegionRestService client) {
        return new RegionServiceRestClient(client);
    }

    @Bean
    public PermissionServiceRestClient permissionService(@Qualifier("permissionRestServiceJaxRsProxyClient") PermissionRestService client) {
        return new PermissionServiceRestClient(client);
    }

    @Bean
    public SystemServiceRestClient appServiceService(@Qualifier("systemRestServiceJaxRsProxyClient") SystemRestService client) {
        return new SystemServiceRestClient(client);
    }

    @Bean
    public AccountTypeRestClient accountTypeService(@Qualifier("accountTypeRestServiceJaxRsProxyClient") AccountTypeRestService client) {
        return new AccountTypeRestClient(client);
    }

    @Configuration
    @ConditionalOnProperty(name = "access.organization-persist-mode", havingValue = "rest")
    @EnableJaxRsProxyClient(classes = OrganizationPersistRestService.class,
            address = "${access.service.url}")
    public static class OrganizationPersistRestServiceConfiguration {

    }
}
