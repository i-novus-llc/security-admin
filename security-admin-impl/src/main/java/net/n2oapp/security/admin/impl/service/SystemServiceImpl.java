package net.n2oapp.security.admin.impl.service;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.audit.AuditService;
import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.service.RefChangeDataExportService;
import net.n2oapp.security.admin.api.service.SystemService;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.SystemRepository;
import net.n2oapp.security.admin.impl.service.specification.SystemSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Реализация сервиса управления приложениями и системами
 */
@Service
@Transactional
public class SystemServiceImpl implements SystemService {

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

    @Value("${access.permission.enabled}")
    private boolean permissionEnabled;

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

    private AppSystem model(SystemEntity entity) {
        if (entity == null) return null;
        AppSystem model = new AppSystem();
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setDescription(entity.getDescription());
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

    private void checkSystemExists(String code) {
        if (!systemRepository.existsById(code))
            throw new UserException("exception.systemNotExists");
    }

    private AppSystem audit(String action, AppSystem appSys) {
        auditService.audit(action, appSys, appSys.getCode(), "audit.system");
        return appSys;
    }
}
