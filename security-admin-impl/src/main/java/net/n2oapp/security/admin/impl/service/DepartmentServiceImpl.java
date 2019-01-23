package net.n2oapp.security.admin.impl.service;


import net.n2oapp.security.admin.api.criteria.DepartmentCriteria;
import net.n2oapp.security.admin.api.model.department.Department;
import net.n2oapp.security.admin.api.model.department.DepartmentCreateForm;
import net.n2oapp.security.admin.api.model.department.DepartmentForm;
import net.n2oapp.security.admin.api.model.department.DepartmentUpdateForm;
import net.n2oapp.security.admin.api.service.DepartmentService;
import net.n2oapp.security.admin.impl.entity.DepartmentEntity;
import net.n2oapp.security.admin.impl.repository.DepartmentRepository;
import net.n2oapp.security.admin.impl.service.specification.DepartmentSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Реализация сервиса управления сведениями о поздразделении ДОМ.РФ
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

     private DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Page<Department> findAll(DepartmentCriteria criteria) {
        final Specification<DepartmentEntity> specification = new DepartmentSpecifications(criteria);
        final Page<DepartmentEntity> all = departmentRepository.findAll(specification, criteria);
        return all.map(DepartmentEntity::extractModel);
    }

    @Override
    public Department getById(String id) {
        DepartmentEntity entity = departmentRepository.findOne(UUID.fromString(id));
        return entity.extractModel();
    }

    @Override
    public Department create(DepartmentCreateForm department) {
        validate(department);
        DepartmentEntity entity = entityForm(new DepartmentEntity(), department);
        DepartmentEntity saved = departmentRepository.save(entity);
        return saved.extractModel();
    }

    @Override
    public Department update(DepartmentUpdateForm department) {
        validate(department);
        Department model = getById(department.getId().toString());

        DepartmentEntity entity = entityForm(new DepartmentEntity(), department);
        entity.setId(department.getId());
        entity.setLastActionDate(LocalDateTime.now(Clock.systemUTC()));
        DepartmentEntity saved = departmentRepository.save(entity);
        return saved.extractModel();
    }

    private DepartmentEntity entityForm(DepartmentEntity entity, DepartmentForm model) {
        entity.setFullName(model.getFullName());
        entity.setShortName(model.getShortName());
        return entity;
    }
    private void validate(DepartmentForm department) {
        requireNonNull(department.getFullName());
        requireNonNull(department.getShortName());
    }
}
