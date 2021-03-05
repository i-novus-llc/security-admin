package net.n2oapp.security.admin.auth.server;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration(exclude = RedisAutoConfiguration.class)
public class RedisSessionStoreConfig {

    @Configuration
    @Import(RedisAutoConfiguration.class)
    @ConditionalOnProperty(prefix = "spring.session", name = "store-type", havingValue = "redis")
    class RedisSessionStoreConfigCondition {

    }
}
