package net.n2oapp.security.auth.common;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration
@EnableConfigurationProperties(UserAttributeKeys.class)
@PropertySource("classpath:userAttributeKeys.properties")
public class PropertySourceAutoConfiguration {
}
