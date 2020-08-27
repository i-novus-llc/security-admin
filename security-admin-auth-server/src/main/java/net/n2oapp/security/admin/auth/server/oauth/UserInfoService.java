package net.n2oapp.security.admin.auth.server.oauth;

import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.auth.common.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static net.n2oapp.security.admin.auth.server.UserTokenConverter.SID;

/**
 * Сервис для построения UserInfo
 */
@Transactional
public class UserInfoService {

    private final UserRepository userRepository;
    private final Collection<UserInfoEnricher<UserEntity>> userInfoEnrichers;

    public UserInfoService(UserRepository userRepository, Collection<UserInfoEnricher<UserEntity>> userInfoEnrichers) {
        this.userRepository = userRepository;
        this.userInfoEnrichers = userInfoEnrichers;
    }

    public Map<String, Object> buildUserInfo(OAuth2Authentication authentication) {
        Map<String, Object> userInfo = new HashMap<>();

        if (authentication.getPrincipal() instanceof User) {
            UserEntity user = userRepository.findOneByUsernameIgnoreCase(((User) authentication.getPrincipal()).getUsername());
            for (UserInfoEnricher<UserEntity> enricher : userInfoEnrichers)
                enricher.enrich(userInfo, user);
        }

        if (authentication.getUserAuthentication() != null)
            userInfo.put(SID, ((Map<String, Object>) authentication.getUserAuthentication().getDetails()).get(SID));

        return userInfo;
    }
}
