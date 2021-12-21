package net.n2oapp.security.auth.common;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(UserAttributeKeys.class)
@PropertySource("classpath:userAttributeKeys.properties")
public class PropertySourceAutoConfiguration {
}
