package net.n2oapp.security.admin.auth.server;

import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.Map;

/**
 * Добавляет идентификатор ключа в хедер токена
 */
public class AccessTokenHeaderConverter extends JwtAccessTokenConverter {

    private JsonParser objectMapper = JsonParserFactory.create();
    private Signer signer;
    private String kid;


    @Override
    protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        if (kid == null)
            return super.encode(accessToken, authentication);

        String content;
        try {
            content = objectMapper.formatMap(getAccessTokenConverter().convertAccessToken(accessToken, authentication));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot convert access token to JSON", e);
        }
        return JwtHelper.encode(content, signer, Map.of("kid", kid)).getEncoded();
    }


    @Override
    public void setKeyPair(KeyPair keyPair) {
        signer = new RsaSigner((RSAPrivateKey) keyPair.getPrivate());
        super.setKeyPair(keyPair);
    }

    public void setKid(String kid) {
        this.kid = kid;
    }
}
