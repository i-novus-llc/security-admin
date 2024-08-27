package net.n2oapp.security.admin;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
//import ru.i_novus.ms.rdm.sync.RdmClientSyncAutoConfiguration;

@Configuration
@ConditionalOnProperty(prefix = "rdm.sync", name = "enabled", havingValue = "false")
//@EnableAutoConfiguration(exclude = RdmClientSyncAutoConfiguration.class)
public class RdmClientSyncAutoConfigurationExcluder {
}