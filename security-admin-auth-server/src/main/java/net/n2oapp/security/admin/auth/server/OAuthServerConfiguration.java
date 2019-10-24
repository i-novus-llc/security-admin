package net.n2oapp.security.admin.auth.server;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.OAuth2AuthorizationServerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableAuthorizationServer
public class OAuthServerConfiguration extends OAuth2AuthorizationServerConfiguration {


    public OAuthServerConfiguration(BaseClientDetails details, AuthenticationConfiguration authenticationConfiguration, ObjectProvider<TokenStore> tokenStore, ObjectProvider<AccessTokenConverter> tokenConverter, AuthorizationServerProperties properties) throws Exception {
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


    @Configuration
    static class TokenStoreConfiguration {

        @Value("${access.auth.key-store-password}")
        private String keyStorePassword;

        @Value("${access.auth.key-id}")
        private String keyId;

        @Bean
        public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {
            return new JwtTokenStore(accessTokenConverter);
        }

        @Bean
        public KeyStoreKeyFactory keyStoreKeyFactory() {
            return new KeyStoreKeyFactory(new ClassPathResource("keystore/gateway.jks"), keyStorePassword.toCharArray());
        }

        @Bean
        public AccessTokenHeaderConverter accessTokenConverter(KeyStoreKeyFactory keyStoreKeyFactory) {
            AccessTokenHeaderConverter converter = new AccessTokenHeaderConverter();
            converter.setKeyPair(keyStoreKeyFactory.getKeyPair("gateway"));
            converter.setAccessTokenConverter(new GatewayAccessTokenConverter(new UserTokenConverter()));
            converter.setKid(keyId);
            return converter;
        }

        @Bean
        public JWKSet jwkSet(KeyStoreKeyFactory keyStoreKeyFactory) {
            RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyStoreKeyFactory.getKeyPair("gateway").getPublic())
                    .keyUse(KeyUse.SIGNATURE)
                    .algorithm(JWSAlgorithm.RS256)
                    .keyID(keyId);
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
    }

}
