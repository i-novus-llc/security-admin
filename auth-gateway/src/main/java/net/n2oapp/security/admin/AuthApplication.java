package net.n2oapp.security.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
@RestController

@Order(SecurityProperties.BASIC_AUTH_ORDER - 2)
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @RequestMapping({"/user", "/me"})
    public Map<String, String> user(Principal principal, Authentication authentication) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", principal.getName());
        map.put("authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).reduce((a, b) -> a + "," + b).get());
        return map;
    }
}