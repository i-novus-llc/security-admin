package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.rest.api.PermissionRestService;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.spring.JaxRsProxyClientConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfiguration {
    @Bean
    public UserServiceRestClient userServiceRestClient(@Qualifier("userRestProxyClient") UserRestService client) {
        return new UserServiceRestClient(client);
    }

    @Bean
    public UserDetailsServiceRestClient userDetailsServiceRestClient(@Qualifier("userRestProxyClient") UserRestService client) {
        return new UserDetailsServiceRestClient(client);
    }

    @Bean
    public RoleServiceRestClient roleServiceRestClient(@Qualifier("roleRestProxyClient") RoleRestService client) {
        return new RoleServiceRestClient(client);
    }

    @Bean
    public PermissionServiceRestClient permissionServiceRestClient(@Qualifier("permissionRestProxyClient") PermissionRestService client) {
        return new PermissionServiceRestClient(client);
    }

    @Configuration
    public static class RoleRestProxyClient extends JaxRsProxyClientConfiguration {

        @Override
        @Bean("roleRestProxyClient")
        protected Client jaxRsProxyClient() {
            return super.jaxRsProxyClient();
        }

        @Override
        protected Class<?> getServiceClass() {
            return RoleRestService.class;
        }
    }

    @Configuration
    static class UserRestProxyClient extends JaxRsProxyClientConfiguration {
        @Override
        @Bean("userRestProxyClient")
        protected Client jaxRsProxyClient() {
            return super.jaxRsProxyClient();
        }
        @Override
        protected Class<?> getServiceClass() {
            return UserRestService.class;
        }
    }

    @Configuration
    static class PermissionRestProxyClient extends JaxRsProxyClientConfiguration {

        @Override
        @Bean("permissionRestProxyClient")
        protected Client jaxRsProxyClient() {
            return super.jaxRsProxyClient();
        }

        @Override
        protected Class<?> getServiceClass() {
            return PermissionRestService.class;
        }
    }
}
