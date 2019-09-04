package net.n2oapp.auth.gateway;

import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.auth.common.User;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Обработчик единого выхода
 */
@Component
public class BackChannelLogoutHandler implements LogoutSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(BackChannelLogoutHandler.class);

    private LogoutSuccessHandler logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();

    private Signer signer;

    private RestTemplate restTemplate = new RestTemplate();

    private ObjectMapper mapper = new ObjectMapper();

    @Value("${access.jwt.signing_key}")
    private String signingKey;

    @Value("${access.keycloak.logout-uri}")
    private String logoutUri;

    @Autowired
    private ClientService clientService;

    @PostConstruct
    public void postConstruct() {
        signer = new RsaSigner(signingKey);
        logoutSuccessHandler = new RedirectLogoutRequestHandler(logoutUri);
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        logoutSuccessHandler.onLogoutSuccess(request, response, authentication);

        for (Client c : getTargetClients()) {
            if (c.getLogoutUrl() != null && authentication instanceof OAuth2Authentication) {
                try {
                    restTemplate.exchange(
                            prepareUrl(c.getLogoutUrl()),
                            HttpMethod.POST,
                            prepareRequest(c.getClientId(), (OAuth2Authentication) authentication),
                            String.class
                    );
                } catch (Exception e) {
                    log.error("Back-Channel logout for " + c.getClientId() + " failed", e);
                }
            }
        }
    }

    private HttpEntity<MultiValueMap<String, String>> prepareRequest(String clientId, OAuth2Authentication authentication) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("logout_token", createLogoutToken(clientId, authentication));
        return new HttpEntity<>(body, headers);
    }

    private String createLogoutToken(String clientId, OAuth2Authentication authentication) {
        Map<String, Object> map = new HashMap<>();
        map.put("iss", "auth-gateway");
        map.put("aud", clientId);
        map.put("event", "LOGOUT");
        map.put("username", ((User) authentication.getUserAuthentication().getPrincipal()).getUsername());
        map.put("sid", ((OAuth2AuthenticationDetails) authentication.getDetails()).getSessionId());

        String content;
        try {
            content = mapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot convert access token to JSON", e);
        }
        return JwtHelper.encode(content, signer).getEncoded();
    }

    private String prepareUrl(String url) throws URISyntaxException {
        return new URI(url + "/backchannel_logout").normalize().toString();
    }

    private List<Client> getTargetClients() {
        return clientService.findAll(new ClientCriteria()).stream()
                .filter(client -> client.getGrantTypes().contains("authorization_code")).collect(Collectors.toList());
    }
}
