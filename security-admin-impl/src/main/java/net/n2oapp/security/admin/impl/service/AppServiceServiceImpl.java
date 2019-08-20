package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.ServiceCriteria;
import net.n2oapp.security.admin.api.model.AppService;
import net.n2oapp.security.admin.api.model.AppServiceForm;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.service.AppServiceService;
import net.n2oapp.security.admin.impl.entity.ServiceEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.PermissionRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.ServiceRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.specification.ServiceSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления службами
 */
@Service
@Transactional
public class AppServiceServiceImpl implements AppServiceService {
    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public AppService create(AppServiceForm service) {
        checkServiceUniq(service.getCode());
        return model(serviceRepository.save(entity(service)));
    }

    @Override
    public AppService update(AppServiceForm service) {
        return model(serviceRepository.save(entity(service)));
    }

    @Override
    public void delete(String code) {
        serviceRepository.deleteById(code);
    }

    @Override
    public AppService getById(String id) {
        return model(serviceRepository.findById(id).orElse(null));
    }

    @Override
    public Page<AppService> findAll(ServiceCriteria criteria) {
        Specification<ServiceEntity> specification = new ServiceSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "code")));
        } else {
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));
        }
        Page<ServiceEntity> all = serviceRepository.findAll(specification, criteria);
        return all.map(this::model);
    }

    @Override
    public Boolean isServiceExist(String code) {
        return getById(code) != null;
    }

    private ServiceEntity entity(AppServiceForm model) {
        if (model == null) return null;
        ServiceEntity entity = new ServiceEntity();
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
        if (entity.getServiceList() != null) {
            model.setAppServices(entity.getServiceList().stream().map(this::model).collect(Collectors.toList()));
        }
        return model;

    }

    private AppService model(ServiceEntity entity) {
        if (entity == null) return null;
        AppService model = new AppService();
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setSystemCode(entity.getSystemCode().getCode());
        return model;
    }


    /**
     * Валидация на уникальность кода системы при создании
     */
    private Boolean checkServiceUniq(String code) {
        ServiceEntity systemEntity= serviceRepository.findById(code).orElse(null);
        if (systemEntity == null) {
            return true;
        } else {
            throw new UserException("exception.uniqueSystem");
        }
    }
}
