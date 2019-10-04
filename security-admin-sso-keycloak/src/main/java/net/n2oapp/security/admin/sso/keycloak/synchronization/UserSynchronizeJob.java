package net.n2oapp.security.admin.sso.keycloak.synchronization;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Автоматический запуск синхронизации пользователей
 */
public class UserSynchronizeJob implements Job {

    @Autowired
    private KeycloakUserSynchronizeProvider keycloakUserSynchronizeProvider;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        keycloakUserSynchronizeProvider.startSynchronization();
    }

}
