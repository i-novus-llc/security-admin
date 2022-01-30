package net.n2oapp.security.admin.auth.server.oauth;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import net.n2oapp.security.admin.impl.repository.AccountRepository;
import net.n2oapp.security.auth.common.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static net.n2oapp.security.auth.common.UserTokenConverter.SID;

/**
 * Сервис для построения UserInfo
 */
@Transactional
public class UserInfoService {

    private final AccountRepository accountRepository;
    private final Collection<UserInfoEnricher<AccountEntity>> userInfoEnrichers;

    public UserInfoService(AccountRepository accountRepository, Collection<UserInfoEnricher<AccountEntity>> userInfoEnrichers) {
        this.accountRepository = accountRepository;
        this.userInfoEnrichers = userInfoEnrichers;
    }

    public Map<String, Object> buildUserInfo(OAuth2Authentication authentication) {
        Map<String, Object> userInfo = new HashMap<>();

        if (authentication.getPrincipal() instanceof User) {
            AccountEntity account = accountRepository.getOne(((User) authentication.getPrincipal()).getAccountId());
            for (UserInfoEnricher<AccountEntity> enricher : userInfoEnrichers)
                enricher.enrich(userInfo, account);
        }

        if (authentication.getUserAuthentication() != null)
            userInfo.put(SID, ((Map<String, Object>) authentication.getUserAuthentication().getDetails()).get(SID));

        return userInfo;
    }
}
