package net.n2oapp.security.admin;

import net.n2oapp.security.admin.rest.api.PermissionRestService;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.spring.AbstractJaxRsClientConfiguration;
import org.apache.cxf.jaxrs.client.spring.EnableJaxRsProxyClient;
import org.apache.cxf.jaxrs.client.spring.JaxRsProxyClientConfiguration;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication
@Configuration
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
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


