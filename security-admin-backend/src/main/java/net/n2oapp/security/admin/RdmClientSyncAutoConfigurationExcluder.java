package net.n2oapp.security.admin;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import ru.inovus.ms.rdm.sync.RdmClientSyncAutoConfiguration;

@Configuration
@ConditionalOnProperty(prefix = "rdm.sync", name = "enabled", havingValue = "false")
@EnableAutoConfiguration(exclude = RdmClientSyncAutoConfiguration.class)
public class RdmClientSyncAutoConfigurationExcluder {
}