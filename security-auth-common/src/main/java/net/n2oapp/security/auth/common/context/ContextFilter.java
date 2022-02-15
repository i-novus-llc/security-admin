package net.n2oapp.security.auth.common.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.n2oapp.security.admin.api.criteria.AccountCriteria;
import net.n2oapp.security.admin.api.model.Account;
import net.n2oapp.security.admin.rest.client.AccountServiceRestClient;
import net.n2oapp.security.auth.common.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ContextFilter implements Filter {

    private ContextUserInfoTokenServices userInfoTokenServices;

    private AccountServiceRestClient accountServiceRestClient;

    public ContextFilter(ContextUserInfoTokenServices userInfoTokenServices, AccountServiceRestClient accountServiceRestClient) {
        this.userInfoTokenServices = userInfoTokenServices;
        this.accountServiceRestClient = accountServiceRestClient;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (isNull(currentAuthentication) || nonNull(((User) currentAuthentication.getPrincipal()).getAccountId())) {
            chain.doFilter(request, response);
            return;
        }
        User user = (User) currentAuthentication.getPrincipal();

        if (((HttpServletRequest) request).getRequestURI().contains("/chooseAccount")) {
            String[] split = ((HttpServletRequest) request).getRequestURI().split("/");
            chooseAccount(Integer.valueOf(split[split.length - 1]), currentAuthentication);
            ((HttpServletResponse) response).sendRedirect("/");
            return;
        }

        List<Account> accountList = accountServiceRestClient.findAll(new AccountCriteria(user.getUsername())).getContent();
        if (accountList.size() == 1) {
            chooseAccount(accountList.get(0).getId(), currentAuthentication);
            chain.doFilter(request, response);
        } else {
            writePage((HttpServletResponse) response, accountList);
        }
    }

    private void writePage(HttpServletResponse response, List<Account> accounts) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ArrayList<String> links = new ArrayList<>();
        final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        accounts.stream().forEach(account -> links.add(baseUrl + "/chooseAccount/" + account.getId()));
        response.getWriter().write(new ObjectMapper().writeValueAsString(links));
    }

    private void chooseAccount(Integer accountId, Authentication currentAuthentication) {
        OAuth2Authentication oAuth2Authentication = userInfoTokenServices.loadAuthentication(((OAuth2AuthenticationDetails) currentAuthentication.getDetails()).getTokenValue(), accountId);
        oAuth2Authentication.setDetails(currentAuthentication.getDetails());
        ((User) oAuth2Authentication.getPrincipal()).setAccountId(accountId);
        SecurityContextHolder.getContext().setAuthentication(oAuth2Authentication);
    }
}