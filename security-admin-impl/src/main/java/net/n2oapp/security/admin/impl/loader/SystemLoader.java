package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * Загрузчик систем
 */
@Component
public class SystemLoader extends RepositoryServerLoader<AppSystem, SystemEntity, String> {

    public SystemLoader(CrudRepository<SystemEntity, String> repository) {
        super(repository, (model, subject) -> {
            if (model == null) return null;
            SystemEntity system = new SystemEntity();
            system.setCode(model.getCode());
            system.setName(model.getName());
            system.setDescription(model.getDescription());
            return system;
        });
    }

    @Override
    public String getTarget() {
        return "systems";
    }

    @Override
    public Class<AppSystem> getDataType() {
        return AppSystem.class;
    }
}
