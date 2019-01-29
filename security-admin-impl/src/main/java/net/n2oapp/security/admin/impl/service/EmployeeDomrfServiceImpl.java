package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.EmployeeDomrfCriteria;
import net.n2oapp.security.admin.api.model.EmployeeDomrf;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.department.Department;
import net.n2oapp.security.admin.api.service.EmployeeDomrfService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.impl.entity.DepartmentEntity;
import net.n2oapp.security.admin.impl.entity.EmployeeDomrfEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.repository.DepartmentRepository;
import net.n2oapp.security.admin.impl.repository.EmployeeDomrfRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author lgalimova
 * @since 18.01.2019
 */
@Service
@Transactional
public class EmployeeDomrfServiceImpl implements EmployeeDomrfService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private DepartmentRepository departmentRepository;
    private EmployeeDomrfRepository employeeDomrfRepository;

    @Autowired
    private UserService userService;

    public EmployeeDomrfServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                                    DepartmentRepository departmentRepository, EmployeeDomrfRepository employeeDomrfRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.employeeDomrfRepository = employeeDomrfRepository;
    }

    @Override
    public Page<EmployeeDomrf> findByDepartment(EmployeeDomrfCriteria criteria) {
        List<EmployeeDomrf> list;
        if (criteria.getDepartmentId() != null) {
            list = employeeDomrfRepository.findByDepartmentId(criteria.getDepartmentId()).stream().map(this::model).collect(Collectors.toList());
        } else {
            list = employeeDomrfRepository.findAll().stream().map(this::model).collect(Collectors.toList());
        }
        return new PageImpl<>(list);
    }

    @Override
    public EmployeeDomrf get(UUID id) {
        EmployeeDomrfEntity employeeDomrfEntity = employeeDomrfRepository.findOne(id);
        return model(employeeDomrfEntity);
    }

    private EmployeeDomrf model(EmployeeDomrfEntity entity) {
        if (entity == null) return null;
        EmployeeDomrf model = new EmployeeDomrf();
        model.setId(entity.getId());
        model.setPosition(entity.getPosition());
        User user = entity.getUser().extractModel();
        if (entity.getUser().getRoleList() != null) {
            user.setRoles(entity.getUser().getRoleList().stream().map(e -> {
                RoleEntity re = roleRepository.findOne(e.getId());
                return re.extractModel();
            }).collect(Collectors.toList()));
        }
        model.setUser(user);
        if (entity.getDepartment() != null) {
            model.setDepartment(entity.getDepartment().extractModel());

        }
        return model;
    }

    private Department model(DepartmentEntity entity) {
        if (entity == null) return null;
        Department model = new Department();
        model.setId(entity.getId());
        model.setFullName(entity.getFullName());
        model.setShortName(entity.getShortName());
        model.setLastActionDate(entity.getLastActionDate());
        model.setCreationDate(entity.getCreationDate());
        return model;
    }
}