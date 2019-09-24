package net.n2oapp.security.admin.sso.keycloak.synchronization;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

/**
 * Автоматический запуск синхронизации пользователей
 */
public class UserSynchronizeJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        getKeycloakUserSynchronizeProvider(context).startSynchronization();
    }

    private KeycloakUserSynchronizeProvider getKeycloakUserSynchronizeProvider(JobExecutionContext context) {
        String key = KeycloakUserSynchronizeProvider.class.getSimpleName();
        try {
            return (KeycloakUserSynchronizeProvider) context.getScheduler().getContext().get(key);
        } catch (SchedulerException e) {
            throw new IllegalStateException(e);
        }
    }
}
