package net.n2oapp.security.auth;

import net.n2oapp.framework.access.AdminService;
import net.n2oapp.framework.access.api.AuthorizationApi;
import net.n2oapp.framework.access.simple.PermissionApi;
import net.n2oapp.framework.api.event.N2oEventBus;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileBindTerminalPipeline;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.security.auth.access.AdvancedAuthorizationApi;
import net.n2oapp.security.auth.access.SimpleAccessSchemaValidatorOverrided;
import net.n2oapp.security.auth.context.SpringSecurityUserContext;
import net.n2oapp.security.auth.listener.AuthenticationLogoutHandler;
import net.n2oapp.security.auth.listener.AuthenticationSuccessListener;
import net.n2oapp.security.auth.listener.SessionDestroyedHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SecurityAuthConfiguration {

    @Value("${n2o.access.schema.id}")
    private String accessSchemaId;

    @Value("${n2o.access.N2oObjectAccessPoint.default:false}")
    private Boolean defaultObjectAccess;

    @Value("${n2o.access.N2oReferenceAccessPoint.default:true}")
    private Boolean defaultReferenceAccess;

    @Value("${n2o.access.N2oPageAccessPoint.default:true}")
    private Boolean defaultPageAccess;

    @Value("${n2o.access.N2oUrlAccessPoint.default:true}")
    private Boolean defaultUrlAccess;

    @Value("${n2o.access.N2oColumnAccessPoint.default:true}")
    private Boolean defaultColumnAccess;

    @Value("${n2o.access.N2oFilterAccessPoint.default:true}")
    private Boolean defaultFilterAccess;


    @Bean
    public PermissionApi permissionApi() {
        return new SecuritySimplePermissionApi();
    }

    @Bean
    public AuthorizationApi authorizationApi(PermissionApi permissionApi, AdminService adminService, N2oApplicationBuilder applicationBuilder) {
        ReadCompileBindTerminalPipeline pipeline = applicationBuilder
                .read().transform().validate().cache()
                .compile().transform().cache().bind();
        return new AdvancedAuthorizationApi(permissionApi, adminService, pipeline, accessSchemaId, defaultObjectAccess,
                defaultReferenceAccess, defaultPageAccess, defaultUrlAccess, defaultColumnAccess, defaultFilterAccess);
    }

    @Bean
    public AuthenticationLogoutHandler logoutHandler () {
        return new AuthenticationLogoutHandler();
    }

    @Bean
    public AuthenticationSuccessListener authenticationSuccessListener(N2oEventBus eventBus) {
        AuthenticationSuccessListener successListener = new AuthenticationSuccessListener();
        successListener.setEventBus(eventBus);
        return successListener;
    }

    @Bean
    public SessionDestroyedHandler destroyedHandler(N2oEventBus eventBus) {
        SessionDestroyedHandler destroyedHandler = new SessionDestroyedHandler();
        destroyedHandler.setEventBus(eventBus);
        return destroyedHandler;
    }

    @Bean
    public SimpleAccessSchemaValidatorOverrided accessSchemaValidator() {
        return new SimpleAccessSchemaValidatorOverrided();
    }

    @Primary
    @Bean
    public SpringSecurityUserContext n2oContext () {
        return new SpringSecurityUserContext();
    }
}
