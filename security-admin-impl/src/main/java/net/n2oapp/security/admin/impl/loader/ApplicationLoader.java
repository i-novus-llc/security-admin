package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.impl.entity.ApplicationEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.ApplicationRepository;
import org.springframework.stereotype.Component;

/**
 * Загрузчик приложений
 */
@Component
public class ApplicationLoader extends RepositoryServerLoader<Application, ApplicationEntity, String> {

    public ApplicationLoader(ApplicationRepository repository) {
        super(repository, (model, subject) -> {
            if (model == null) return null;
            ApplicationEntity app = new ApplicationEntity();
            app.setCode(model.getCode());
            app.setName(model.getName());
            if (subject != null) {
                app.setSystemCode(new SystemEntity(subject));
            }
            return app;
        }, (systemCode -> repository.findBySystemCode(new SystemEntity(systemCode))), ApplicationEntity::getCode);
    }

    @Override
    public String getTarget() {
        return "applications";
    }

    @Override
    public Class<Application> getDataType() {
        return Application.class;
    }
}
