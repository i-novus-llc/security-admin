package net.n2oapp.framework.security.autoconfigure.userinfo.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import net.n2oapp.framework.security.autoconfigure.userinfo.UserInfoAdvice;
import net.n2oapp.framework.security.autoconfigure.userinfo.UserInfoStateHolder;
import net.n2oapp.framework.security.autoconfigure.userinfo.mapper.PrincipalToJsonAbstractMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.HttpHeaders.writableHttpHeaders;

@AutoConfiguration
public class InterceptorConfig {

    @Value("${n2o.platform.userinfo.send-by-default:true}")
    private boolean userinfoSendByDefault;

    @Value("${n2o.platform.userinfo.header-name:n2o-user-info}")
    private String userInfoHeaderName;

    @Bean
    public ClientHttpRequestInterceptor userinfoClientHttpRequestInterceptor(PrincipalToJsonAbstractMapper principalToJsonMapper) {
        return (request, body, execution) -> {
            addUserInfoHeader(request.getHeaders(), principalToJsonMapper);
            return execution.execute(request, body);
        };
    }

    @Bean
    public RequestInterceptor userinfoFeignInterceptor(PrincipalToJsonAbstractMapper principalToJsonMapper) {
        return template -> addUserInfoHeader(template, principalToJsonMapper);
    }

    private void addUserInfoHeader(Object headers, PrincipalToJsonAbstractMapper principalMapper) {
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

    @Bean
    public UserInfoAdvice userInfoAdvice() {
        return new UserInfoAdvice();
    }

    @Bean
    public RestTemplate platformRestTemplate(@Qualifier("userinfoClientHttpRequestInterceptor") ClientHttpRequestInterceptor interceptor) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of(interceptor));
        return restTemplate;
    }

    @Bean
    public RestClient platformRestClient(@Qualifier("userinfoClientHttpRequestInterceptor") ClientHttpRequestInterceptor userinfoClientHttpRequestInterceptor) {
        return RestClient.builder().requestInterceptor(userinfoClientHttpRequestInterceptor).build();
    }
}
