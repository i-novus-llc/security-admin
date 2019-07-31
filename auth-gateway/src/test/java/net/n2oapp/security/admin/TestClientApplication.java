package net.n2oapp.security.admin;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@EnableAutoConfiguration
@Configuration
@EnableOAuth2Sso
@RestController
public class TestClientApplication {

    @RequestMapping("/")
    public String home(Principal user, Authentication authentication) {
        return "Hello " + user.getName();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(TestClientApplication.class)
                .properties("spring.config.name=test-client").run(args);
    }
}
