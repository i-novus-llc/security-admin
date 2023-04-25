package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.rest.client.feign.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
public class AdminRestClientConfiguration {

    @Bean
    public UserServiceRestClient userService(UserServiceFeignClient client) {
        return new UserServiceRestClient(client);
    }

    @Bean
    public AccountServiceRestClient accountService(AccountServiceFeignClient client) {
        return new AccountServiceRestClient(client);
    }

    @Bean
    public UserDetailsServiceRestClient userDetailsService(UserDetailsServiceFeignClient client) {
        return new UserDetailsServiceRestClient(client);
    }

    @Bean
    public RoleServiceRestClient roleService(RoleServiceFeignClient client) {
        return new RoleServiceRestClient(client);
    }

    @Bean
    public UserLevelServiceRestClient userLevelService(UserLevelServiceFeignClient client) {
        return new UserLevelServiceRestClient(client);
    }

    @Bean
    public DepartmentServiceRestClient departmentService(DepartmentServiceFeignClient client) {
        return new DepartmentServiceRestClient(client);
    }

    @Bean
    public OrganizationServiceRestClient organizationService(OrganizationServiceFeignClient organizationRestService,
                                                             @Autowired(required = false) OrganizationServicePersistFeignClient organizationPersistRestService) {
        return new OrganizationServiceRestClient(organizationRestService, organizationPersistRestService);
    }

    @Bean
    public RegionServiceRestClient regionService(RegionServiceFeignClient client) {
        return new RegionServiceRestClient(client);
    }

    @Bean
    public PermissionServiceRestClient permissionService(PermissionServiceFeignClient client) {
        return new PermissionServiceRestClient(client);
    }

    @Bean
    public SystemServiceRestClient appServiceService(SystemServiceFeignClient client) {
        return new SystemServiceRestClient(client);
    }

    @Bean
    public AccountTypeRestClient accountTypeService(AccountTypeFeignClient client) {
        return new AccountTypeRestClient(client);
    }
}
