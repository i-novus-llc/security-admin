package net.n2oapp.security.admin.impl.userinfo;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.AccountRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

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

    public Map<String, Object> buildUserInfo(Integer accountId) {
        Map<String, Object> userInfo = new HashMap<>();
        if (nonNull(accountId)) {
            AccountEntity account = accountRepository.getReferenceById(accountId);
            userInfo.put(ACCOUNT_ID, account.getId().toString());
            for (UserInfoEnricher<AccountEntity> enricher : userInfoEnrichers)
                enricher.enrich(userInfo, account);
            UserEntity user = userRepository.findOneByUsernameIgnoreCase(account.getUser().getUsername());
            simpleUserDataEnricher.enrich(userInfo, user);
        }
        return userInfo;
    }
}
