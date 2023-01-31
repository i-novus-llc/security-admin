package net.n2oapp.security.auth;

import net.n2oapp.security.auth.common.OauthUser;
import net.n2oapp.security.auth.context.SpringSecurityUserContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringUserContextWithToken extends SpringSecurityUserContext {

    @Override
    public Object get(String param) {
        if ("token".equals(param)) {
            OauthUser details = getAuthenticationDetails();
            if (details != null) {
                return details.getIdToken().getTokenValue();
            }
        }
        return super.get(param);
    }

    private OauthUser getAuthenticationDetails() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Authentication authentication = context.getAuthentication();
            if (authentication != null) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof OauthUser) {
                    return (OauthUser) principal;
                }
            }
        }
        return null;
    }
}
