package net.n2oapp.framework.security.admin.gateway.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.jwt.Jwt;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = JwtVerifierTest.class,
        properties = {"spring.cloud.consul.enabled=false"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@SpringBootApplication
public class JwtVerifierTest {

    @Autowired
    private SessionRegistry sessionRegistry;

    @MockBean
    private RestTemplate restTemplateMock;

    @Autowired
    private JwtVerifier jwtVerifier;

    @Test
    public void test() {
        LinkedHashMap<String, Object> publicKey = new LinkedHashMap<>();
        publicKey.put("alg", "SHA256withRSA");
        publicKey.put("value", "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArgK+/dvHbSnFJxhDYFRx9E8HXI96/GRcHl+mes9B2usX9mNrQESDpm+hOzMtal+LjAbqbJmlbL+/Tu7pf4G3MfuiNOmkWiyX9S6B3aKvib6RUC0EKfz8fEH4Z7fZKRRhevU2pTHYaiOz3m7zHkK6snYMcsKpYU/HqyWXPRRh+gcv1a4Rr2ta+CPWfpMWSWW1pgg9lc8rJTUnudRfHusSHqE3L2/ut0Zf8EZiXCSmznP4ifO5Hm95i9z0axnTfb16Ie34b70tFoUJ2sPGGFDdBtgTtx35yJ6QkbuLk+dnIyGrYWWPEah6omjZhec0mRuyzuEbVpmA4Cel5j1Spcx1/QIDAQAB\n-----END PUBLIC KEY-----");
        when(restTemplateMock.getForObject(anyString(), any(Class.class))).thenReturn(publicKey);
        jwtVerifier.setRestTemplate(restTemplateMock);
        Jwt jwt = jwtVerifier.decodeAndVerify("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJhY2Nlc3MtYXBwIiwiaXNzIjoiYXV0aC1nYXRld2F5IiwiZXZlbnQiOiJMT0dPVVQiLCJ1c2VybmFtZSI6InRlc3QiLCJzaWQiOiIxNkYzMEI4NjBBMjhFOUUyMDAyQkM4MTMxNTlDNUVENiJ9.c2CTG5s3tmd60knZOWzpb736tdeOFzaB5pwwzC80aGd736kI_0ruT-uuiTcTBNsIqusv8SWC4OPTqmq0l16jvcF7_2HUxTOA1VaWlGWS1N1V_1Ji-4YQRnQSZ7SuhC5PVemZjl-G5y2kM6Z6Jo60HSrNDhjalWF6R-FpVyey3gKkggt2ySgMFZ4k11q9MUbjlnfHqizp6iGNdhdgYkRjNaxW3OMZwNjXJHRCjO1MmIAF5PUPP79QSqVYViUFN69BR8lYCJNP0RrOEtTYKqxhdxkX96Lhw29o1n5tAJLKVEg8JswNnN3mFTWYl2QjKxIp8YGyOK7SsEuk9tSEwXs7Yg");
        assertThat(jwt.getClaims(), is("{\"aud\":\"access-app\",\"iss\":\"auth-gateway\",\"event\":\"LOGOUT\",\"username\":\"test\",\"sid\":\"16F30B860A28E9E2002BC813159C5ED6\"}"));
    }
}
