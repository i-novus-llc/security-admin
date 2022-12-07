package net.n2oapp.security.admin.impl.userinfo;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserInfoEndpoint {

    private final UserInfoService userInfoService;

    public UserInfoEndpoint(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @RequestMapping(value = "/userinfo/{accountId}")
    public Map<String, Object> user(@PathVariable Integer accountId) {
        return userInfoService.buildUserInfo(accountId);
    }
}
