package net.n2oapp.security.admin.rest.client;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.security.admin.rest.api.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableJaxRsProxyClient(
        classes = {UserRestService.class, RoleRestService.class, PermissionRestService.class,
                ApplicationSystemRestService.class, ClientRestService.class, UserDetailsRestService.class,
                RegionRestService.class, OrganizationRestService.class, DepartmentRestService.class, UserLevelRestService.class},
        address = "${access.service.url}")
public class AdminRestClientConfiguration {

    @Bean
    public UserServiceRestClient userService(@Qualifier("userRestServiceJaxRsProxyClient") UserRestService client) {
        return new UserServiceRestClient(client);
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
    public OrganizationServiceRestClient organizationService(@Qualifier("organizationRestServiceJaxRsProxyClient") OrganizationRestService client) {
        return new OrganizationServiceRestClient(client);
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
    public ApplicationSystemServiceRestClient appServiceService(@Qualifier(
            "applicationSystemRestServiceJaxRsProxyClient") ApplicationSystemRestService client) {
        return new ApplicationSystemServiceRestClient(client);
    }

    @Bean
    public ClientServiceRestClient clientService(@Qualifier("clientRestServiceJaxRsProxyClient") ClientRestService client) {
        return new ClientServiceRestClient(client);
    }
}
