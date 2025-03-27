package net.n2oapp.framework.security.autoconfigure.userinfo.config;

import feign.RequestInterceptor;
import net.n2oapp.framework.security.autoconfigure.userinfo.UserInfoAdvice;
import net.n2oapp.framework.security.autoconfigure.userinfo.UserInfoHeaderHelper;
import net.n2oapp.framework.security.autoconfigure.userinfo.mapper.PrincipalToJsonAbstractMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@AutoConfiguration
@AutoConfigureBefore(name = {"net.n2oapp.framework.boot.N2oFrameworkAutoConfiguration", "net.n2oapp.framework.boot.N2oEngineConfiguration"})
public class InterceptorConfig {

    @Bean
    public ClientHttpRequestInterceptor userinfoClientHttpRequestInterceptor(PrincipalToJsonAbstractMapper principalToJsonMapper, UserInfoHeaderHelper userInfoHeaderHelper) {
        return (request, body, execution) -> {
            userInfoHeaderHelper.addUserInfoHeader(request.getHeaders(), principalToJsonMapper);
            return execution.execute(request, body);
        };
    }

    @Bean
    public RequestInterceptor userinfoFeignInterceptor(PrincipalToJsonAbstractMapper principalToJsonMapper,
                                                       UserInfoHeaderHelper userInfoHeaderHelper) {
        return template -> userInfoHeaderHelper.addUserInfoHeader(template, principalToJsonMapper);
    }

    @Bean
    public UserInfoAdvice userInfoAdvice() {
        return new UserInfoAdvice();
    }

    @Bean
    public RestTemplateBuilder builder(@Qualifier("userinfoClientHttpRequestInterceptor") ClientHttpRequestInterceptor interceptor, RestTemplateBuilderConfigurer restTemplateBuilderConfigurer) {
        return restTemplateBuilderConfigurer.configure(new RestTemplateBuilder().interceptors(interceptor));
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
