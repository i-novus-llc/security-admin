package net.n2oapp.security.auth.context.account;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import net.n2oapp.security.admin.api.criteria.AccountCriteria;
import net.n2oapp.security.admin.api.model.Account;
import net.n2oapp.security.admin.rest.client.AccountServiceRestClient;
import net.n2oapp.security.auth.common.OauthUser;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

public class ContextFilter extends OncePerRequestFilter {

    private static final String DEFAULT_SELECT_ACCOUNT_TEMPLATE_PATH = "classpath:public/context-page/context-page.html";
    private static final String DEFAULT_SELECT_ACCOUNT_CSS_PATH = "css/context-page.css";
    private static final String DEFAULT_SELECT_ACCOUNT_EMBLEM_PATH = "static/rusEmblem.svg";

    private Set<String> defaultIgnoredUrls = Set.of("/static/**", "/public/**", "/dist/**", "/webjars/**", "/lib/**", "/build/**", "/bundle/**", "/error", "/serviceWorker.js", "/css/**", "/manifest.json", "/favicon.ico");
    private final OrRequestMatcher orRequestMatcher;

    private ContextUserInfoTokenServices userInfoTokenServices;
    private AccountServiceRestClient accountServiceRestClient;
    @Setter
    private ITemplateEngine templateEngine;

    private String selectAccountTemplatePath;
    private String selectAccountCssPath;
    private String selectAccountEmblemPath;

    public ContextFilter(ContextUserInfoTokenServices userInfoTokenServices,
                         AccountServiceRestClient accountServiceRestClient,
                         String selectAccountTemplatePath, String selectAccountCssPath, String selectAccountEmblemPath, Set<String> ignoredUrls) {
        this.userInfoTokenServices = userInfoTokenServices;
        this.accountServiceRestClient = accountServiceRestClient;
        this.selectAccountTemplatePath = selectAccountTemplatePath;
        this.selectAccountCssPath = selectAccountCssPath;
        this.selectAccountEmblemPath = selectAccountEmblemPath;
        this.templateEngine = new TemplateEngine();
        AbstractConfigurableTemplateResolver resolver = new UrlTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        ((TemplateEngine) templateEngine).setTemplateResolver(resolver);
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        for (String pattern : isEmpty(ignoredUrls) ? defaultIgnoredUrls : ignoredUrls) {
            requestMatchers.add(new AntPathRequestMatcher(pattern, HttpMethod.GET.name()));
        }
        orRequestMatcher = new OrRequestMatcher(requestMatchers);
    }

    public ContextFilter(ContextUserInfoTokenServices userInfoTokenServices, AccountServiceRestClient accountServiceRestClient, String selectAccountCssPath) {
        this(userInfoTokenServices, accountServiceRestClient, DEFAULT_SELECT_ACCOUNT_TEMPLATE_PATH, selectAccountCssPath, DEFAULT_SELECT_ACCOUNT_EMBLEM_PATH, null);
    }

    public ContextFilter(ContextUserInfoTokenServices userInfoTokenServices, AccountServiceRestClient accountServiceRestClient, String selectAccountCssPath, String selectAccountEmblemPath) {
        this(userInfoTokenServices, accountServiceRestClient, DEFAULT_SELECT_ACCOUNT_TEMPLATE_PATH, selectAccountCssPath, selectAccountEmblemPath, null);
    }

    public ContextFilter(ContextUserInfoTokenServices userInfoTokenServices, AccountServiceRestClient accountServiceRestClient, Set<String> ignoredUrls) {
        this(userInfoTokenServices, accountServiceRestClient, DEFAULT_SELECT_ACCOUNT_TEMPLATE_PATH, DEFAULT_SELECT_ACCOUNT_CSS_PATH, DEFAULT_SELECT_ACCOUNT_EMBLEM_PATH, ignoredUrls);
    }

    public ContextFilter(ContextUserInfoTokenServices userInfoTokenServices, AccountServiceRestClient accountServiceRestClient) {
        this(userInfoTokenServices, accountServiceRestClient, Collections.EMPTY_SET);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (isNull(currentAuthentication) || nonNull(((OauthUser) currentAuthentication.getPrincipal()).getAccountId())) {
            filterChain.doFilter(request, response);
            return;
        }
        OauthUser user = (OauthUser) currentAuthentication.getPrincipal();

        if (request.getRequestURI().contains("/selectAccount")) {
            String accountId = request.getParameter("accountId");
            selectAccount(Integer.valueOf(accountId), currentAuthentication);
            response.sendRedirect("/");
            return;
        }

        List<Account> accountList = accountServiceRestClient.findAll(new AccountCriteria(user.getName())).getContent();
        if (accountList.size() == 1) {
            selectAccount(accountList.get(0).getId(), currentAuthentication);
            filterChain.doFilter(request, response);
        } else {
            writePage(request, response, accountList);
        }
    }

    private void writePage(HttpServletRequest request, HttpServletResponse response, List<Account> accounts) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setHeader("Content-Type", "text/html;charset=UTF-8");


        WebContext context = createContext(request,response);

        context.setVariable("css", selectAccountCssPath);
        context.setVariable("emblem", selectAccountEmblemPath);
        context.setVariable("accounts", accounts);

        String accountSelectPage = templateEngine.process(selectAccountTemplatePath, context);
        response.getWriter().write(accountSelectPage);
    }

    private void selectAccount(Integer accountId, Authentication currentAuthentication) {
        OAuth2AuthenticationToken oAuth2Authentication = userInfoTokenServices.loadAccountAuthentication(accountId, currentAuthentication);
        oAuth2Authentication.setDetails(currentAuthentication.getDetails());
        SecurityContextHolder.getContext().setAuthentication(oAuth2Authentication);
    }

    private WebContext createContext(HttpServletRequest req, HttpServletResponse res) {
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(req.getServletContext());
        IServletWebExchange exchange = application.buildExchange(req, res);
        return new WebContext(exchange);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return orRequestMatcher.matches(request);
    }
}