package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.audit.AuditService;
import net.n2oapp.security.admin.api.criteria.ApplicationCriteria;
import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.service.ApplicationSystemService;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.api.service.RefChangeDataExportService;
import net.n2oapp.security.admin.impl.entity.ApplicationEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.ApplicationRepository;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.SystemRepository;
import net.n2oapp.security.admin.impl.service.specification.ApplicationSpecifications;
import net.n2oapp.security.admin.impl.service.specification.SystemSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;

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
    private RoleRepository roleRepository;
    @Autowired(required = false)
    private PermissionRepository permissionRepository;
    @Autowired
    private AuditService auditService;
    @Autowired
    private RefChangeDataExportService changeDataExportService;
    @Autowired
    private ClientService clientService;

    @Value("${access.permission.enabled}")
    private boolean permissionEnabled;

    @Override
    public Application createApplication(Application service) {
        checkServiceUniq(service.getCode());
        checkSystemExists(service.getSystemCode());
        Application result = model(applicationRepository.save(entity(service)));

        changeDataExportService.changeApplicationData(singletonList(result), emptyList());

        return audit("audit.applicationCreate", result);
    }

    @Override
    public Application updateApplication(Application service) {
        if (Boolean.FALSE.equals(service.getOAuth()) && service.getClient() != null) {
            service.getClient().setEnabled(false);
            service.getClient().setClientId(service.getCode());
            clientService.persist(service.getClient());
        }
        Application result = model(applicationRepository.save(entity(service)));

        changeDataExportService.changeApplicationData(singletonList(result), emptyList());

        return audit("audit.applicationUpdate", result);
    }

    @Override
    public void deleteApplication(String code) {
        ApplicationEntity app = applicationRepository.findById(code).orElse(null);
        if (isNull(app))
            throw new UserException("exception.applicationNotFound");
        applicationRepository.deleteById(code);
        Application model = model(app);

        changeDataExportService.changeApplicationData(emptyList(), singletonList(model));

        audit("audit.applicationDelete", model);
    }

    @Override
    public Application getApplication(String id) {
        return model(applicationRepository.findById(id).orElse(null));
    }

    @Override
    public Page<Application> findAllApplications(ApplicationCriteria criteria) {
        Specification<ApplicationEntity> specification = new ApplicationSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(new ArrayList<>());
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
        AppSystem result = model(systemRepository.save(entity(system)));

        changeDataExportService.changeSystemData(singletonList(result), emptyList());

        return audit("audit.appSystemCreate", result);
    }

    @Override
    public AppSystem updateSystem(AppSystemForm system) {
        AppSystem result = model(systemRepository.save(entity(system)));

        changeDataExportService.changeSystemData(singletonList(result), emptyList());

        return audit("audit.appSystemUpdate", result);
    }

    @Override
    public void deleteSystem(String code) {
        checkSystemWithAuthorities(code);
        SystemEntity sys = systemRepository.findById(code).orElse(null);
        systemRepository.deleteById(code);
        if (sys != null) {
            AppSystem model = model(sys);

            changeDataExportService.changeSystemData(emptyList(), singletonList(model));

            audit("audit.appSystemDelete", model);
        }
    }

    @Override
    public AppSystem getSystem(String id) {
        Optional<SystemEntity> system = systemRepository.findById(id);
        if (system.isEmpty()) {
            Response response = Response.status(404).header("x-error-message", "system with such id doesn't exists").build();
            throw new NotFoundException(response);
        }
        return model(system.get());

    }

    @Override
    public Page<AppSystem> findAllSystems(SystemCriteria criteria) {
        Specification<SystemEntity> specification = new SystemSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(new ArrayList<>());
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
        model.setClient(entity.getClient() == null ? null : ClientServiceImpl.model(entity.getClient(), permissionEnabled));
        return model;
    }

    private ApplicationEntity entity(Application model) {
        if (model == null) return null;
        ApplicationEntity entity = new ApplicationEntity();
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setSystemCode(new SystemEntity(model.getSystemCode()));
        if (Boolean.TRUE.equals(model.getOAuth())) {
            model.getClient().setClientId(model.getCode());
            entity.setClient(ClientServiceImpl.entity(model.getClient()));
        }
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
        model.setShortName(entity.getShortName());
        model.setShowOnInterface(entity.getShowOnInterface());
        model.setIcon(entity.getIcon());
        model.setUrl(entity.getUrl());
        model.setPublicAccess(entity.getPublicAccess());
        model.setViewOrder(entity.getViewOrder());
        return model;
    }

    private SystemEntity entity(AppSystemForm model) {
        if (model == null) return null;
        SystemEntity entity = new SystemEntity();
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setDescription(model.getDescription());
        if (Boolean.TRUE.equals(model.getShowOnInterface())) {
            entity.setShowOnInterface(model.getShowOnInterface());
            entity.setShortName(model.getShortName());
            entity.setIcon(model.getIcon());
            entity.setUrl(model.getUrl());
            entity.setPublicAccess(model.getPublicAccess());
            entity.setViewOrder(model.getViewOrder());
        }
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
    private void checkSystemWithAuthorities(String code) {
        if (roleRepository.countRolesWithSystemCode(code) != 0 || (permissionEnabled && permissionRepository.countPermissionsWithSystemCode(code) != 0))
            throw new UserException("exception.roleOrPermissionWithSuchRoleExists");
    }

    /**
     * Валидация на уникальность кода приложения при создании
     */
    private void checkServiceUniq(String code) {
        if (applicationRepository.findById(code).isPresent())
            throw new UserException("exception.uniqueApplication");
    }

    private void checkSystemExists(String code) {
        if (!systemRepository.existsById(code))
            throw new UserException("exception.systemNotExists");
    }

    private Application audit(String action, Application app) {
        auditService.audit(action, app, app.getCode(), "audit.application");
        return app;
    }

    private AppSystem audit(String action, AppSystem appSys) {
        auditService.audit(action, appSys, appSys.getCode(), "audit.system");
        return appSys;
    }
}
