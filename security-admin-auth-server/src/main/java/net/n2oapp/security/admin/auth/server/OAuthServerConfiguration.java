package net.n2oapp.security.admin.auth.server;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.JSONObject;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.auth.server.logout.OIDCBackChannelLogoutHandler;
import net.n2oapp.security.auth.common.LogoutHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.OAuth2AuthorizationServerConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Set;

@Configuration
@EnableAuthorizationServer
public class OAuthServerConfiguration extends OAuth2AuthorizationServerConfiguration {

    public OAuthServerConfiguration(BaseClientDetails details, AuthenticationConfiguration authenticationConfiguration,
                                    ObjectProvider<TokenStore> tokenStore, ObjectProvider<AccessTokenConverter> tokenConverter,
                                    AuthorizationServerProperties properties) throws Exception {
        super(details, authenticationConfiguration, tokenStore, tokenConverter, properties);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        super.configure(endpoints);
        endpoints.redirectResolver(new RedirectResolverImpl());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(new GatewayService());
    }

    @Bean
    public LogoutHandler logoutHandler(KeyStoreKeyFactory keyStoreKeyFactory, ClientService clientService) {
        RsaSigner signer = new RsaSigner((RSAPrivateKey) keyStoreKeyFactory.getKeyPair("gateway").getPrivate());
        return new OIDCBackChannelLogoutHandler(signer, clientService);
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "access.auth.keystore")
    private static class KeystoreProperties {
        private String password;
        private String keyId;
    }

    @Configuration
    @EnableConfigurationProperties(KeystoreProperties.class)
    static class TokenStoreConfiguration {

        @Value("${access.token.include-claims:}")
        private Set<String> tokenIncludeClaims;

        @Autowired
        private KeystoreProperties properties;

        @Bean
        public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {
            return new JwtTokenStore(accessTokenConverter);
        }

        @Bean
        public KeyStoreKeyFactory keyStoreKeyFactory() {
            return new KeyStoreKeyFactory(new ClassPathResource("keystore/gateway.jks"), properties.getPassword().toCharArray());
        }

        @Bean
        public AccessTokenHeaderConverter accessTokenConverter(KeyStoreKeyFactory keyStoreKeyFactory) {
            AccessTokenHeaderConverter converter = new AccessTokenHeaderConverter();
            converter.setKeyPair(keyStoreKeyFactory.getKeyPair("gateway"));
            Boolean includeRoles = tokenIncludeClaims.contains("roles");
            Boolean includePermissions = tokenIncludeClaims.contains("permissions");
            Boolean includeSystems = tokenIncludeClaims.contains("systems");
            converter.setAccessTokenConverter(new GatewayAccessTokenConverter(includeRoles, includePermissions, includeSystems));
            converter.setKid(properties.getKeyId());
            return converter;
        }

        @Bean
        public JWKSet jwkSet(KeyStoreKeyFactory keyStoreKeyFactory) {
            RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyStoreKeyFactory.getKeyPair("gateway").getPublic())
                    .keyUse(KeyUse.SIGNATURE)
                    .algorithm(JWSAlgorithm.RS256)
                    .keyID(properties.getKeyId());
            return new JWKSet(builder.build());
        }

        @RestController
        public static class JwkSetRestController {

            @Autowired
            private JWKSet jwkSet;

            @GetMapping("/oauth/certs")
            public JSONObject certs() {
                return jwkSet.toJSONObject();
            }
        }

        /**
         * Если пользователь аутентифицирован,
         * возвращает страницу с сообщением, что пользователь уже вошёл
         */
        @Controller
        public static class IndexController {

            @Value("${access.auth.redirect:#{null}}")
            String redirectTo;

            @RequestMapping("/")
            public ModelAndView index() {
                ModelAndView mv = new ModelAndView();
                SecurityContext sc = SecurityContextHolder.getContext();
                if (sc != null && sc.getAuthentication() instanceof OAuth2Authentication)
                    mv.setViewName("forward:alreadyLogged.html");
                else if (redirectTo != null && !redirectTo.isEmpty())
                    mv.setViewName("redirect:" + redirectTo);
                else
                    mv.setViewName("forward:index.html");
                return mv;
            }
        }
    }
}
