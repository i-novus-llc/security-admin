package net.n2oapp.auth.gateway.filter;

import net.n2oapp.security.admin.impl.exception.UserNotFoundAuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2AuthenticationFailureEvent;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GatewayOAuth2ClientAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter {

    public GatewayOAuth2ClientAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

/*    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        if (!this.requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
        } else {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication.isAuthenticated()) {
            OAuth2AccessToken accessToken = this.restTemplate.getOAuth2ClientContext().getAccessToken();
            if (accessToken != null) {
                Authentication authResult = this.attemptAuthentication(request, response);
                if (authResult == null) {
                    return;
                }
                this.successfulAuthentication(request, response, chain, authResult);
//                this.successfulAuthentication(request, response, chain, authentication);
            } else {
                super.doFilter(req, res, chain);
            }
        }
    }*/


    /*@Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return super.requiresAuthentication(request, response) && ((OAuth2RestTemplate) this.restTemplate).getOAuth2ClientContext().getAccessToken() == null;
    }*/

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        try {
            return super.attemptAuthentication(request, response);
        } catch (UserNotFoundAuthenticationException e) {
            restTemplate.getOAuth2ClientContext().setAccessToken(null);
            throw e;
        }

    }
}
