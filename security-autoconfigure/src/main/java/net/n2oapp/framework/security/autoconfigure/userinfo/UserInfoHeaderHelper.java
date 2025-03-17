package net.n2oapp.framework.security.autoconfigure.userinfo;

import feign.RequestTemplate;
import net.n2oapp.framework.security.autoconfigure.userinfo.mapper.PrincipalToJsonAbstractMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.HttpHeaders.writableHttpHeaders;

public class UserInfoHeaderHelper {

    @Value("${n2o.platform.userinfo.send-by-default:true}")
    private boolean userinfoSendByDefault;

    @Value("${n2o.platform.userinfo.header-name:n2o-user-info}")
    private String userInfoHeaderName;

    public void addUserInfoHeader(Object headers, PrincipalToJsonAbstractMapper principalMapper) {
        Boolean userInfo = UserInfoStateHolder.get();
        if ((isNull(userInfo) && userinfoSendByDefault) || (nonNull(userInfo) && userInfo)) {
            SecurityContext context = SecurityContextHolder.getContext();
            if (isNull(context))
                return;
            Authentication authentication = context.getAuthentication();
            if (isNull(authentication))
                return;
            Object principal = authentication.getPrincipal();
            if (isNull(principal))
                return;
            String encoded = URLEncoder.encode(principalMapper.map(principal), StandardCharsets.UTF_8);
            if (headers instanceof HttpHeaders httpHeaders)
                writableHttpHeaders(httpHeaders).add(userInfoHeaderName, encoded);
            else if (headers instanceof RequestTemplate requestTemplate) {
                requestTemplate.header(userInfoHeaderName, encoded);
            }
        }
    }

}
