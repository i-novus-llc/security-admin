package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.ApplicationCriteria;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.service.ApplicationService;
import net.n2oapp.security.admin.impl.entity.ApplicationEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import net.n2oapp.security.admin.impl.repository.ApplicationRepository;
import net.n2oapp.security.admin.impl.service.specification.ApplicationSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Реализация сервиса управления приложениями
 */
@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public Application create(Application service) {
        checkServiceUniq(service.getCode());
        return model(applicationRepository.save(entity(service)));
    }

    @Override
    public Application update(Application service) {
        return model(applicationRepository.save(entity(service)));
    }

    @Override
    public void delete(String code) {
        applicationRepository.deleteById(code);
    }

    @Override
    public Application getById(String id) {
        return model(applicationRepository.findById(id).orElse(null));
    }

    @Override
    public Page<Application> findAll(ApplicationCriteria criteria) {
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
        return getById(code) != null;
    }

    private ApplicationEntity entity(Application model) {
        if (model == null) return null;
        ApplicationEntity entity = new ApplicationEntity();
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setSystemCode(new SystemEntity(model.getSystemCode()));
        return entity;
    }


    private Application model(ApplicationEntity entity) {
        if (entity == null) return null;
        Application model = new Application();
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setSystemCode(entity.getSystemCode().getCode());
        model.setOAuth(entity.getClient() != null);
        return model;
    }


    /**
     * Валидация на уникальность кода приложения при создании
     */
    private Boolean checkServiceUniq(String code) {
        ApplicationEntity applicationEntity = applicationRepository.findById(code).orElse(null);
        if (applicationEntity == null) {
            return true;
        } else {
            throw new UserException("exception.uniqueApplication");
        }
    }
}
