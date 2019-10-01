package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.DepartmentCriteria;
import net.n2oapp.security.admin.api.model.Department;
import net.n2oapp.security.admin.api.service.DepartmentService;
import net.n2oapp.security.admin.impl.entity.DepartmentEntity;
import net.n2oapp.security.admin.impl.repository.DepartmentRepository;
import net.n2oapp.security.admin.impl.service.specification.DepartmentSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Реализация сервиса управления ролями
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository regionRepository;
    
    @Override
    public Page<Department> findAll(DepartmentCriteria criteria) {
        Specification<DepartmentEntity> specification = new DepartmentSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "id")));
        } else {
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "id"));
        }
        Page<DepartmentEntity> all = regionRepository.findAll(specification, criteria);
        return all.map(this::model);
    }

    private Department model(DepartmentEntity entity) {
        if (entity == null) return null;
        Department model = new Department();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        return model;

    }

    public void setDepartmentRepository(DepartmentRepository regionRepository) {
        this.regionRepository = regionRepository;
    }
}
