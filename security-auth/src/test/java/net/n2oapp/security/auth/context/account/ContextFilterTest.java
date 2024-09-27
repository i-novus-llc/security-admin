package net.n2oapp.security.auth.context.account;

import jakarta.servlet.ServletException;
import net.n2oapp.security.admin.api.model.Account;
import net.n2oapp.security.admin.rest.client.AccountServiceRestClient;
import net.n2oapp.security.auth.common.OauthUser;
import net.n2oapp.security.auth.common.PropertySourceAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PropertySourceAutoConfiguration.class)
public class ContextFilterTest {

    @Mock
    private ContextUserInfoTokenServices userInfoTokenServices;
    @Mock
    private AccountServiceRestClient accountServiceRestClient;
    @Mock
    private ITemplateEngine templateEngine;
    @Mock
    private MockFilterChain mockFilterChain;

    @Test
    public void alreadyAuthAccountChosen() throws ServletException, IOException {
        doNothing().when(mockFilterChain).doFilter(any(), any());
        OAuth2AuthenticationToken oAuth2AuthenticationToken = oAuth2AuthenticationToken();
        ((OauthUser) oAuth2AuthenticationToken.getPrincipal()).setAccountId("1");
        SecurityContextHolder.setContext(new SecurityContextImpl());
        ContextFilter contextFilter = new ContextFilter(userInfoTokenServices, accountServiceRestClient);
        contextFilter.doFilterInternal(null, null, mockFilterChain);
        verify(mockFilterChain).doFilter(null, null);
    }

    @Test
    public void alreadyAuthSelectAccount() throws ServletException, IOException {
        SecurityContextHolder.setContext(new SecurityContextImpl(oAuth2AuthenticationToken()));
        OAuth2AuthenticationToken tokenWithAccount = oAuth2AuthenticationToken();
        ((OauthUser) tokenWithAccount.getPrincipal()).setAccountId("2");
        doReturn(tokenWithAccount).when(userInfoTokenServices).loadAccountAuthentication(anyInt(), any());
        ContextFilter contextFilter = new ContextFilter(userInfoTokenServices, accountServiceRestClient);

        MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "test/selectAccount");
        request.addParameter("accountId", "2");
        contextFilter.doFilterInternal(request, new MockHttpServletResponse(), null);

        String accountId = ((OauthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAccountId();
        assertThat(accountId, is("2"));
    }

    @Test
    public void alreadyAuthOneAccount() throws ServletException, IOException {
        doNothing().when(mockFilterChain).doFilter(any(), any());
        SecurityContextHolder.setContext(new SecurityContextImpl(oAuth2AuthenticationToken()));
        OAuth2AuthenticationToken tokenWithAccount = oAuth2AuthenticationToken();
        ((OauthUser) tokenWithAccount.getPrincipal()).setAccountId("2");
        doReturn(tokenWithAccount).when(userInfoTokenServices).loadAccountAuthentication(anyInt(), any());
        Account account = new Account();
        account.setId(2);
        doReturn(new PageImpl<>(List.of(account))).when(accountServiceRestClient).findAll(any());
        ContextFilter contextFilter = new ContextFilter(userInfoTokenServices, accountServiceRestClient);

        MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "test/someUrl");
        contextFilter.doFilterInternal(request, new MockHttpServletResponse(), mockFilterChain);

        String accountId = ((OauthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAccountId();
        assertThat(accountId, is("2"));

        ArgumentCaptor<OAuth2AuthenticationToken> tokenArgumentCaptor = ArgumentCaptor.forClass(OAuth2AuthenticationToken.class);

        verify(userInfoTokenServices).loadAccountAuthentication(eq(2), tokenArgumentCaptor.capture());
        accountId = ((OauthUser) tokenArgumentCaptor.getValue().getPrincipal()).getAccountId();
        assertNull("", accountId);
        verify(mockFilterChain).doFilter(any(), any());
    }

    @Test
    public void alreadyAuthManyAccount() throws ServletException, IOException {
        SecurityContextHolder.setContext(new SecurityContextImpl(oAuth2AuthenticationToken()));
        Account account = new Account();
        account.setId(2);
        Account account2 = new Account();
        account2.setId(3);
        doReturn(new PageImpl<>(List.of(account, account2))).when(accountServiceRestClient).findAll(any());
        doReturn("test").when(templateEngine).process(eq("classpath:public/context-page/context-page.html"), any());
        ContextFilter contextFilter = new ContextFilter(userInfoTokenServices, accountServiceRestClient);
        contextFilter.setTemplateEngine(templateEngine);
        MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "test/someUrl");
        contextFilter.doFilterInternal(request, new MockHttpServletResponse(), mockFilterChain);

        ArgumentCaptor<WebContext> webContextArgumentCaptor = ArgumentCaptor.forClass(WebContext.class);
        verify(templateEngine).process(eq("classpath:public/context-page/context-page.html"), webContextArgumentCaptor.capture());
        WebContext value = webContextArgumentCaptor.getValue();
        List<Account> accounts = (List) value.getVariable("accounts");
        assertThat(accounts.size(), is(2));
        assertThat(accounts.get(0).getId(), is(2));
        assertThat(accounts.get(1).getId(), is(3));
    }

    @Test
    public void testConstructor() {
        new ContextFilter(userInfoTokenServices, accountServiceRestClient, "test", "test", "test", Set.of("test"));
        new ContextFilter(userInfoTokenServices, accountServiceRestClient, Set.of("test"));
        new ContextFilter(userInfoTokenServices, accountServiceRestClient, "test", "test");
        new ContextFilter(userInfoTokenServices, accountServiceRestClient, "test");
        new ContextFilter(userInfoTokenServices, accountServiceRestClient);
    }

    private OAuth2AuthenticationToken oAuth2AuthenticationToken() {
        OidcIdToken oidcIdToken = new OidcIdToken("token_value", Instant.MIN, Instant.MAX, Map.of("sub", "sub"));
        OauthUser oauthUser = new OauthUser("admin", oidcIdToken);
        oauthUser.setEmail("test@i-novus.ru");
        oauthUser.setSurname("admin");
        OAuth2AuthenticationToken oAuth2AuthenticationToken = new OAuth2AuthenticationToken(oauthUser, null, "test");
        return oAuth2AuthenticationToken;
    }
}
