package net.n2oapp.security.admin.auth.server.logout;

import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.auth.common.LogoutHandler;
import net.n2oapp.security.auth.common.User;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  Обработка BackChannelLogout согласно OpenID Connect
 */
public class OIDCBackChannelLogoutHandler implements LogoutHandler {

    private static final Logger log = LoggerFactory.getLogger(OIDCBackChannelLogoutHandler.class);

    private ClientService clientService;
    private Signer signer;
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();


    public OIDCBackChannelLogoutHandler(Signer signer, ClientService clientService) {
        this.clientService = clientService;
        this.signer = signer;
    }

    @Override
    public void doLogout(Authentication authentication) {
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

    private List<Client> getTargetClients() {
        ClientCriteria criteria = new ClientCriteria();
        criteria.setSize(Integer.MAX_VALUE);
        return clientService.findAll(criteria).stream()
                .filter(Client::getIsAuthorizationCode).collect(Collectors.toList());
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
}
