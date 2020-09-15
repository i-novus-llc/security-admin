package net.n2oapp.framework.security.admin.gateway.adapter;

import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class JwtHelperHolder {

    private RestTemplate restTemplate = new RestTemplate();
    private volatile SignatureVerifier verifier;
    private String tokenKeyEndpointUrl;

    public JwtHelperHolder(String tokenKeyEndpointUrl) {
        this.tokenKeyEndpointUrl = tokenKeyEndpointUrl;
    }

    public Jwt decodeAndVerify(String token) {
        return JwtHelper.decodeAndVerify(token, getVerifier());
    }

    private synchronized SignatureVerifier getVerifier() {
        if (verifier == null) {
            Map<String, Object> response = restTemplate.getForObject(tokenKeyEndpointUrl, Map.class);
            if (response != null && response.get("value") != null)
                verifier = new RsaVerifier((String) response.get("value"));
        }

        return verifier;
    }
}
