package net.n2oapp.framework.security.autoconfigure.userinfo.config;

import net.n2oapp.framework.security.autoconfigure.userinfo.JsonToPrincipalFilter;
import net.n2oapp.framework.security.autoconfigure.userinfo.UserInfoHeaderHelper;
import net.n2oapp.framework.security.autoconfigure.userinfo.UserInfoModel;
import net.n2oapp.framework.security.autoconfigure.userinfo.mapper.*;
import net.n2oapp.security.auth.common.OauthUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class UserInfoConfig {

    @Value("${n2o.platform.userinfo.header-name:n2o-user-info}")
    private String userInfoHeaderName;

    @Bean
    @ConditionalOnMissingBean(JsonToPrincipalAbstractMapper.class)
    public JsonToPrincipalAbstractMapper<UserInfoModel> jsonToPrincipalMapper() {
        return new JsonToPrincipalMapper();
    }

    @Bean
    @ConditionalOnMissingClass("net.n2oapp.framework.boot.N2oEnvironmentConfiguration")
    public FilterRegistrationBean<JsonToPrincipalFilter> userInfoFilter(JsonToPrincipalAbstractMapper jsonToPrincipalMapper) {
        JsonToPrincipalFilter jsonToPrincipalFilter = new JsonToPrincipalFilter(jsonToPrincipalMapper, userInfoHeaderName);
        FilterRegistrationBean<JsonToPrincipalFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jsonToPrincipalFilter);
        registration.addUrlPatterns("/*");
        registration.setName("userInfoFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    @ConditionalOnClass(name = "net.n2oapp.framework.boot.N2oEnvironmentConfiguration")
    @ConditionalOnMissingBean(PrincipalToJsonAbstractMapper.class)
    public PrincipalToJsonAbstractMapper<OauthUser> oauthPrincipalToJsonMapper() {
        return new OauthPrincipalToJsonMapper();
    }

    @Bean
    @ConditionalOnMissingClass("net.n2oapp.framework.boot.N2oEnvironmentConfiguration")
    @ConditionalOnMissingBean(PrincipalToJsonAbstractMapper.class)
    public PrincipalToJsonAbstractMapper<UserInfoModel> userInfoToJsonMapper() {
        return new UserInfoToJsonMapper<>();
    }

    @Bean
    public UserInfoHeaderHelper headerHelper() {
        return new UserInfoHeaderHelper();
    }
}
