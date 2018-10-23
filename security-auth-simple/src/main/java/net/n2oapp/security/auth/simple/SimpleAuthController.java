package net.n2oapp.security.auth.simple;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SimpleAuthController {

    @Value("${security.admin.login.title}")
    private String title;
    @Value("${n2o.auth.registration.enabled}")
    private Boolean registrationEnabled;

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", title);
        model.addAttribute("registrationEnabled", registrationEnabled);
        return "login";
    }

    @RequestMapping("/registration")
    public String registration() {
        return "registration";
    }

}