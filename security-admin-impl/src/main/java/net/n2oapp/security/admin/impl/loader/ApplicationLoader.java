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
            if (model.getSystemCode() != null) {
                SystemEntity system = new SystemEntity();
                system.setCode(model.getSystemCode());
                app.setSystemCode(system);
            }
            return app;
        }, (subject -> repository.findAll()), ApplicationEntity::getCode);
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
