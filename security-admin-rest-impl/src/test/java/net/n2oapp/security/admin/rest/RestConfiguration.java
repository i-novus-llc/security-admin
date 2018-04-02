package net.n2oapp.security.admin.rest;

import net.n2oapp.security.admin.rest.api.PermissionRestService;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.spring.JaxRsProxyClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfiguration {
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
