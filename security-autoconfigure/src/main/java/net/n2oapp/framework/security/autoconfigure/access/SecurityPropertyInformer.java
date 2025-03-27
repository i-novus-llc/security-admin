package net.n2oapp.framework.security.autoconfigure.access;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import static net.n2oapp.framework.api.StringUtils.isEmpty;

@Slf4j
public class SecurityPropertyInformer implements ApplicationListener<ApplicationStartedEvent> {

    public static final String SERVER_URL_KEY = "access.keycloak.server-url";
    public static final String REALM_KEY = "access.keycloak.realm";
    public static final String IS_ENABLED_KEY = "access.security-autoconfigure.disabled";

    public static final String PROPERTY_WARN_MESSAGE = "Property 'access.keycloak.server-url' and 'access.keycloak.realm' must be set, to enable Security Autoconfiguration";
    public static final String CONFIG_DISABLING_ADVICE_MESSAGE = "If you don't need Security Autoconfiguration to be active, set 'access.security-autoconfigure.disabled' property to 'true'";

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
        String serverUrl = environment.getProperty(SERVER_URL_KEY);
        String realm = environment.getProperty(REALM_KEY);
        String isEnabled = environment.getProperty(IS_ENABLED_KEY);
        if (isEmpty(serverUrl) || isEmpty(realm)) {
            log.warn(PROPERTY_WARN_MESSAGE);
            if (isEmpty(isEnabled)) {
                log.info(CONFIG_DISABLING_ADVICE_MESSAGE);
            }
        }
    }
}