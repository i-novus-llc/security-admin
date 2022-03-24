package net.n2oapp.security.admin.auth.server.oauth;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.AccountRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.auth.common.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;
import static net.n2oapp.security.auth.common.UserTokenConverter.SID;

/**
 * Сервис для построения UserInfo
 */
@Transactional
public class UserInfoService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final Collection<UserInfoEnricher<AccountEntity>> userInfoEnrichers;
    private final SimpleUserDataEnricher simpleUserDataEnricher = new SimpleUserDataEnricher();

    private static final String ACCOUNT_ID = "accountId";

    public UserInfoService(UserRepository userRepository, AccountRepository accountRepository, Collection<UserInfoEnricher<AccountEntity>> userInfoEnrichers) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.userInfoEnrichers = userInfoEnrichers;
    }

    public Map<String, Object> buildUserInfo(OAuth2Authentication authentication, Integer accountId) {
        Map<String, Object> userInfo = new HashMap<>();
        if (authentication.getPrincipal() instanceof User && nonNull(accountId)) {
            AccountEntity account = accountRepository.getOne(accountId);
            if (!account.getUser().getUsername().equals(((User) authentication.getPrincipal()).getUsername()))
                return userInfo;
            userInfo.put(ACCOUNT_ID, account.getId().toString());
            for (UserInfoEnricher<AccountEntity> enricher : userInfoEnrichers)
                enricher.enrich(userInfo, account);
        }

        UserEntity user = userRepository.findOneByUsernameIgnoreCase(((User) authentication.getPrincipal()).getUsername());
        simpleUserDataEnricher.enrich(userInfo, user);

        if (authentication.getUserAuthentication() != null)
            userInfo.put(SID, ((Map<String, Object>) authentication.getUserAuthentication().getDetails()).get(SID));

        return userInfo;
    }
}
