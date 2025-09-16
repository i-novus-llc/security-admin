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

import java.util.ArrayList;

/**
 * Реализация сервиса управления ролями
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository regionRepository;
    @Autowired
    private Mapper mapper;
    
    @Override
    public Page<Department> findAll(DepartmentCriteria criteria) {
        Specification<DepartmentEntity> specification = new DepartmentSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(new ArrayList<>());
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "id"));
        }
        Page<DepartmentEntity> all = regionRepository.findAll(specification, criteria);
        return all.map(mapper::model);
    }

    public void setDepartmentRepository(DepartmentRepository regionRepository) {
        this.regionRepository = regionRepository;
    }
}
