package net.n2oapp.security.admin.rest.client;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.security.admin.rest.api.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableJaxRsProxyClient(
        classes = {UserRestService.class, RoleRestService.class, PermissionRestService.class, SystemRestService.class,
                ApplicationRestService.class, ClientRestService.class},
        address = "${sec.admin.rest.url}")
public class AdminRestClientConfiguration {

    @Bean
    public UserServiceRestClient userService(@Qualifier("userRestServiceJaxRsProxyClient") UserRestService client) {
        return new UserServiceRestClient(client);
    }

    @Bean
    public UserDetailsServiceRestClient userDetailsService(@Qualifier("userRestServiceJaxRsProxyClient") UserRestService client) {
        return new UserDetailsServiceRestClient(client);
    }

    @Bean
    public RoleServiceRestClient roleService(@Qualifier("roleRestServiceJaxRsProxyClient") RoleRestService client) {
        return new RoleServiceRestClient(client);
    }

    @Bean
    public PermissionServiceRestClient permissionService(@Qualifier("permissionRestServiceJaxRsProxyClient") PermissionRestService client) {
        return new PermissionServiceRestClient(client);
    }

    @Bean
    public AppSystemServiceRestClient systemService(@Qualifier("systemRestServiceJaxRsProxyClient") SystemRestService client) {
        return new AppSystemServiceRestClient(client);
    }

    @Bean
    public ApplicationServiceRestClient appServiceService(@Qualifier("applicationRestServiceJaxRsProxyClient") ApplicationRestService client) {
        return new ApplicationServiceRestClient(client);
    }

    @Bean
    public ClientServiceRestClient clientService(@Qualifier("clientRestServiceJaxRsProxyClient") ClientRestService client) {
        return new ClientServiceRestClient(client);
    }

}
