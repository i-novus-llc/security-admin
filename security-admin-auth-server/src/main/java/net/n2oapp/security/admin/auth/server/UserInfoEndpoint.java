package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.admin.auth.server.oauth.UserInfoService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserInfoEndpoint {

    private final UserInfoService userInfoService;

    public UserInfoEndpoint(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }


    @RequestMapping(value = "/userinfo")
    public Map<String, Object> user(OAuth2Authentication authentication) {
        return userInfoService.buildUserInfo(authentication);
    }
}
