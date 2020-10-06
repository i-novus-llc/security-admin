package net.n2oapp.framework.security.admin.gateway.adapter;

import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;

public class LogoutToken implements Jwt {

    @Override
    public String getClaims() {
        return "{\"aud\":\"access-app\",\"iss\":\"auth-gateway\",\"event\":\"LOGOUT\",\"username\":\"test_user\",\"sid\":\"97C5AC1039B77FFE4F6AB55ACF5707EC\"}";
    }

    @Override
    public String getEncoded() {
        return null;
    }

    @Override
    public void verifySignature(SignatureVerifier verifier) {

    }

    @Override
    public byte[] bytes() {
        return new byte[0];
    }
}
