package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.service.AppSystemService;
import net.n2oapp.security.admin.impl.entity.ApplicationEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.SystemRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.specification.SystemSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления системами
 */
@Service
@Transactional
public class AppSystemServiceImpl implements AppSystemService {
    @Autowired
    private SystemRepository systemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public AppSystem create(AppSystemForm system) {
        checkSystemUniq(system.getCode(), null);
        AppSystem result = model(systemRepository.save(entity(system)));
        return result;
    }

    @Override
    public AppSystem update(AppSystemForm system) {
        SystemEntity entity = systemRepository.getOne(system.getCode());
        entity.setName(system.getName());
        entity.setDescription(system.getDescription());
        return model(systemRepository.save(entity));
    }

    @Override
    public void delete(String code) {
        checkSystemExist(code);
        systemRepository.deleteById(code);
    }

    @Override
    public AppSystem getById(String id) {
        return model(systemRepository.findById(id).orElse(null));
    }

    @Override
    public Page<AppSystem> findAll(SystemCriteria criteria) {
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
        return getById(code) != null;
    }

    private SystemEntity entity(AppSystemForm model) {
        if (model == null) return null;
        SystemEntity entity = new SystemEntity();
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setDescription(model.getDescription());
        if (model.getServices() != null) {
            entity.setApplicationList(model.getServices().stream().map(ApplicationEntity::new).collect(Collectors.toList()));
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
        return model;

    }

    private Application model(ApplicationEntity entity) {
        if (entity == null) return null;
        Application model = new Application();
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setSystemCode(entity.getSystemCode().getCode());
        return model;
    }

    /**
     * Валидация на уникальность кода системы при создании
     */
    private Boolean checkSystemUniq(String code, String systemId) {
        SystemEntity systemEntity= systemRepository.findById(code).orElse(null);
        if (systemEntity == null || systemEntity.getCode().equals(systemId)) {
            return true;
        } else {
            throw new UserException("exception.uniqueSystem");
        }
    }

    /**
     * Валидация на удаление системы
     * Запрещено удалять систему, если существует роль или право доступа в такой системе
     */
    private boolean checkSystemExist(String code) {
        if (roleRepository.countRolesWithSystemCode(code) == 0 && permissionRepository.countPermissionsWithSystemCode(code) == 0)
            return true;
        else
            throw new UserException("exception.roleOrPermissionWithSuchRoleExists");
    }
}
