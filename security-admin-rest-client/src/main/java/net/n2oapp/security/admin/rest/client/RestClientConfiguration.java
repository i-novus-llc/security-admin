package net.n2oapp.security.admin.rest.client;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.n2oapp.security.admin.rest.api.PermissionRestService;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestClientConfiguration {
    @Value("${sec.admin.rest.url}")
    private String restUrl;

    @Bean
    public UserServiceRestClient userServiceRestClient(UserRestService client) {
        return new UserServiceRestClient(client);
    }

    @Bean
    public UserDetailsServiceRestClient userDetailsServiceRestClient(UserRestService client) {
        return new UserDetailsServiceRestClient(client);
    }

    @Bean
    public RoleServiceRestClient roleServiceRestClient(RoleRestService client) {
        return new RoleServiceRestClient(client);
    }

    @Bean
    public PermissionServiceRestClient permissionServiceRestClient(PermissionRestService client) {
        return new PermissionServiceRestClient(client);
    }

    @Bean
    public JacksonJsonProvider cxfJsonProvider() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule clientBackendModule = new SimpleModule("ClientBackendModule", Version.unknownVersion());
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(Page.class, RestPage.class);
        clientBackendModule.setAbstractTypes(resolver);
        mapper.registerModule(clientBackendModule);
        return new JacksonJsonProvider(mapper);
    }

    @Bean
    public UserRestService userRestService(@Qualifier("cxfJsonProvider") JacksonJsonProvider cxfJsonProvider) {
        return createClient(cxfJsonProvider, UserRestService.class);
    }

    @Bean
    public RoleRestService roleRestService(@Qualifier("cxfJsonProvider") JacksonJsonProvider cxfJsonProvider) {
        return createClient(cxfJsonProvider, RoleRestService.class);
    }

    @Bean
    public PermissionRestService permissionRestService(@Qualifier("cxfJsonProvider") JacksonJsonProvider cxfJsonProvider) {
        return createClient(cxfJsonProvider, PermissionRestService.class);
    }

    private <T> T createClient(JacksonJsonProvider cxfJsonProvider, Class<T> restServiceClass) {
        List<Object> providers = new ArrayList<>();
        providers.add(cxfJsonProvider);
        T resource = JAXRSClientFactory.create(restUrl,
                restServiceClass,
                providers);
        return resource;
    }
}
