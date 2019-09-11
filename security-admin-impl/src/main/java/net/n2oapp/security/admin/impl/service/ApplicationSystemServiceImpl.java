package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.ApplicationCriteria;
import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.service.ApplicationSystemService;
import net.n2oapp.security.admin.impl.entity.ApplicationEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.*;
import net.n2oapp.security.admin.impl.service.specification.ApplicationSpecifications;
import net.n2oapp.security.admin.impl.service.specification.SystemSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления приложениями и системами
 */
@Service
@Transactional
public class ApplicationSystemServiceImpl implements ApplicationSystemService {
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private SystemRepository systemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Application createApplication(Application service) {
        checkServiceUniq(service.getCode());
        return model(applicationRepository.save(entity(service)));
    }

    @Override
    public Application updateApplication(Application service) {
        return model(applicationRepository.save(entity(service)));
    }

    @Override
    public void deleteApplication(String code) {
        applicationRepository.deleteById(code);
    }

    @Override
    public Application getApplication(String id) {
        return model(applicationRepository.findById(id).orElse(null));
    }

    @Override
    public Page<Application> findAllApplications(ApplicationCriteria criteria) {
        Specification<ApplicationEntity> specification = new ApplicationSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "code")));
        } else {
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));
        }
        Page<ApplicationEntity> all = applicationRepository.findAll(specification, criteria);
        return all.map(this::model);
    }

    @Override
    public Boolean isApplicationExist(String code) {
        return getApplication(code) != null;
    }


    @Override
    public AppSystem createSystem(AppSystemForm system) {
        checkSystemUniq(system.getCode());
        return model(systemRepository.save(entity(system)));
    }

    @Override
    public AppSystem updateSystem(AppSystemForm system) {
        SystemEntity entity = systemRepository.getOne(system.getCode());
        entity.setName(system.getName());
        entity.setDescription(system.getDescription());
        return model(systemRepository.save(entity));
    }

    @Override
    public void deleteSystem(String code) {
        checkSystemExist(code);
        systemRepository.deleteById(code);
    }

    @Override
    public AppSystem getSystem(String id) {
        return model(systemRepository.findById(id).orElse(null));
    }

    @Override
    public Page<AppSystem> findAllSystems(SystemCriteria criteria) {
        Specification<SystemEntity> specification = new SystemSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "code")));
        } else {
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));
        }
        Page<SystemEntity> all = systemRepository.findAll(specification, criteria);
        return all.map(this::model);
    }

    @Override
    public Boolean isSystemExist(String code) {
        return getSystem(code) != null;
    }

    private Application model(ApplicationEntity entity) {
        if (entity == null) return null;
        Application model = new Application();
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setSystemCode(entity.getSystemCode().getCode());
        model.setSystemName(entity.getSystemCode().getName());
        model.setOAuth(entity.getClient() != null);
        return model;
    }

    private ApplicationEntity entity(Application model) {
        if (model == null) return null;
        ApplicationEntity entity = new ApplicationEntity();
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setSystemCode(new SystemEntity(model.getSystemCode()));
        return entity;
    }

    private AppSystem model(SystemEntity entity) {
        if (entity == null) return null;
        AppSystem model = new AppSystem();
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setDescription(entity.getDescription());
        if (entity.getApplicationList() != null) {
            model.setApplications(entity.getApplicationList().stream().map(this::model).collect(Collectors.toList()));
        }
        return model;
    }

    private SystemEntity entity(AppSystemForm model) {
        if (model == null) return null;
        SystemEntity entity = new SystemEntity();
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setDescription(model.getDescription());
        return entity;
    }

    /**
     * Валидация на уникальность кода системы при создании
     */
    private void checkSystemUniq(String code) {
        Optional<SystemEntity> system = systemRepository.findById(code);
        if (system.isPresent() && system.get().getCode() != null)
            throw new UserException("exception.uniqueSystem");
    }

    /**
     * Валидация на удаление системы
     * Запрещено удалять систему, если существует роль или право доступа в такой системе
     */
    private void checkSystemExist(String code) {
        if (roleRepository.countRolesWithSystemCode(code) != 0 || permissionRepository.countPermissionsWithSystemCode(code) != 0)
            throw new UserException("exception.roleOrPermissionWithSuchRoleExists");
    }

    /**
     * Валидация на уникальность кода приложения при создании
     */
    private void checkServiceUniq(String code) {
        if (applicationRepository.findById(code).isPresent())
            throw new UserException("exception.uniqueApplication");
    }
}
