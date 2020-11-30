package net.n2oapp.security.admin.auth.server;

import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Добавляет идентификатор ключа в хедер токена
 */
public class AccessTokenConverter extends JwtAccessTokenConverter {

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

    /**
     * Заглушка на рефреш токен до нормальной реализации. Считывает значение сессии из ещё не переписанного
     * рефреш токена (если тот существует) и сетит в новый.
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        if (accessToken.getRefreshToken() != null) {
            try {
                Map<String, Object> claims = super.decode(accessToken.getRefreshToken().getValue());
                PreAuthenticatedAuthenticationToken userAuthentication = (PreAuthenticatedAuthenticationToken) authentication.getUserAuthentication();
                Object details = userAuthentication.getDetails();
                if (details == null)
                    details = new HashMap<>();
                if (details instanceof Map) {
                    Map<String, Object> detailsMap = (Map<String, Object>) details;
                    detailsMap.put("sid", claims.get("sid"));
                    userAuthentication.setDetails(detailsMap);
                }
            } catch (InvalidTokenException e) {
                // Проглатываем исключение, если рефреш токен ещё не сформирован и его невозможно декодировать.
            }
        }
        return super.enhance(accessToken, authentication);
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
