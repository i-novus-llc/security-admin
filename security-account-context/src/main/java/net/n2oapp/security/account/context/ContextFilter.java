package net.n2oapp.security.account.context;

import net.n2oapp.security.admin.api.criteria.AccountCriteria;
import net.n2oapp.security.admin.api.model.Account;
import net.n2oapp.security.admin.rest.client.AccountServiceRestClient;
import net.n2oapp.security.auth.common.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ContextFilter implements Filter {

    private static final String DEFAULT_SELECT_ACCOUNT_TEMPLATE_PATH = "classpath:public/context-page/context-page.html";
    private static final String DEFAULT_SELECT_ACCOUNT_CSS_PATH = "css/context-page.css";
    private static final String DEFAULT_SELECT_ACCOUNT_EMBLEM_PATH = "static/rusEmblem.svg";

    private ContextUserInfoTokenServices userInfoTokenServices;
    private AccountServiceRestClient accountServiceRestClient;
    private TemplateEngine templateEngine;

    private String selectAccountTemplatePath;
    private String selectAccountCssPath;
    private String selectAccountEmblemPath;

    public ContextFilter(ContextUserInfoTokenServices userInfoTokenServices,
                         AccountServiceRestClient accountServiceRestClient,
                         String selectAccountTemplatePath, String selectAccountCssPath, String selectAccountEmblemPath) {
        this.userInfoTokenServices = userInfoTokenServices;
        this.accountServiceRestClient = accountServiceRestClient;
        this.selectAccountTemplatePath = selectAccountTemplatePath;
        this.selectAccountCssPath = selectAccountCssPath;
        this.selectAccountEmblemPath = selectAccountEmblemPath;
        this.templateEngine = new TemplateEngine();
        AbstractConfigurableTemplateResolver resolver = new UrlTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        templateEngine.setTemplateResolver(resolver);
    }

    public ContextFilter(ContextUserInfoTokenServices userInfoTokenServices, AccountServiceRestClient accountServiceRestClient, String selectAccountCssPath) {
        this(userInfoTokenServices, accountServiceRestClient, DEFAULT_SELECT_ACCOUNT_TEMPLATE_PATH, selectAccountCssPath, DEFAULT_SELECT_ACCOUNT_EMBLEM_PATH);
    }

    public ContextFilter(ContextUserInfoTokenServices userInfoTokenServices, AccountServiceRestClient accountServiceRestClient, String selectAccountCssPath, String selectAccountEmblemPath) {
        this(userInfoTokenServices, accountServiceRestClient, DEFAULT_SELECT_ACCOUNT_TEMPLATE_PATH, selectAccountCssPath, selectAccountEmblemPath);
    }

    public ContextFilter(ContextUserInfoTokenServices userInfoTokenServices, AccountServiceRestClient accountServiceRestClient) {
        this(userInfoTokenServices, accountServiceRestClient, DEFAULT_SELECT_ACCOUNT_TEMPLATE_PATH, DEFAULT_SELECT_ACCOUNT_CSS_PATH, DEFAULT_SELECT_ACCOUNT_EMBLEM_PATH);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (isNull(currentAuthentication) || nonNull(((User) currentAuthentication.getPrincipal()).getAccountId())) {
            chain.doFilter(request, response);
            return;
        }
        User user = (User) currentAuthentication.getPrincipal();

        if (((HttpServletRequest) request).getRequestURI().contains("/selectAccount")) {
            String accountId = request.getParameter("accountId");
            selectAccount(Integer.valueOf(accountId), currentAuthentication);
            ((HttpServletResponse) response).sendRedirect("/");
            return;
        }

        List<Account> accountList = accountServiceRestClient.findAll(new AccountCriteria(user.getUsername())).getContent();
        if (accountList.size() == 1) {
            selectAccount(accountList.get(0).getId(), currentAuthentication);
            chain.doFilter(request, response);
        } else {
            writePage((HttpServletRequest) request, (HttpServletResponse) response, accountList);
        }
    }

    private void writePage(HttpServletRequest request, HttpServletResponse response, List<Account> accounts) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setHeader("Content-Type", "text/html;charset=UTF-8");

        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("css", selectAccountCssPath);
        context.setVariable("emblem", selectAccountEmblemPath);
        context.setVariable("accounts", accounts);

        String accountSelectPage = templateEngine.process(selectAccountTemplatePath, context);
        response.getWriter().write(accountSelectPage);
    }

    private void selectAccount(Integer accountId, Authentication currentAuthentication) {
        OAuth2Authentication oAuth2Authentication = userInfoTokenServices.loadAuthentication(accountId);
        oAuth2Authentication.setDetails(currentAuthentication.getDetails());
        SecurityContextHolder.getContext().setAuthentication(oAuth2Authentication);
    }
}