package net.n2oapp.framework.security.autoconfigure.access;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import static net.n2oapp.framework.api.StringUtils.isEmpty;

@Slf4j
public class SecurityPropertyInformer implements ApplicationListener<ApplicationStartedEvent> {

    private static final String SERVER_URL_KEY = "access.keycloak.server-url";
    private static final String REALM_KEY = "access.keycloak.realm";
    private static final String IS_ENABLED_KEY = "access.security-autoconfigure.disabled";

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
        String serverUrl = environment.getProperty(SERVER_URL_KEY);
        String realm = environment.getProperty(REALM_KEY);
        String isEnabled = environment.getProperty(IS_ENABLED_KEY);
        if (isEmpty(serverUrl) || isEmpty(realm)) {
            log.warn("Property 'access.keycloak.server-url' and 'access.keycloak.realm' must be set, to enable Security Autoconfiguration");
            if (isEmpty(isEnabled)) {
                log.info("If you don't need Security Autoconfiguration to be active, set 'access.security-autoconfigure.disabled' property to 'true'");
            }
        }
    }
}