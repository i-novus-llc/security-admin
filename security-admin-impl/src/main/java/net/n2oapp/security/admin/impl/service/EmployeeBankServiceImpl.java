package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.EmployeeBankCriteria;
import net.n2oapp.security.admin.api.model.EmployeeBank;
import net.n2oapp.security.admin.api.model.EmployeeBankForm;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.EmployeeBankService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.impl.entity.BankEntity;
import net.n2oapp.security.admin.impl.entity.EmployeeBankEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.BankRepository;
import net.n2oapp.security.admin.impl.repository.EmployeeBankRepository;
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
 * Реализация сервиса управления уполномоченными лицами банка
 */
@Service
@Transactional
public class EmployeeBankServiceImpl implements EmployeeBankService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BankRepository bankRepository;
    private EmployeeBankRepository employeeBankRepository;

    @Autowired
    private UserService userService;


    public EmployeeBankServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BankRepository bankRepository, EmployeeBankRepository employeeBankRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bankRepository = bankRepository;
        this.employeeBankRepository = employeeBankRepository;
    }

    @Override
    public EmployeeBank create(EmployeeBankForm user) {
        User userCreate = userService.create(user.getUser());
        user.getUser().setId(userCreate.getId());
        return model(employeeBankRepository.save(entityForm(user)));
    }

    @Override
    public Page<EmployeeBank> findByBank(EmployeeBankCriteria criteria) {
        List<EmployeeBank> list = employeeBankRepository.findByBankId(criteria.getBankId()).stream().map(this::model).collect(Collectors.toList());
        return new PageImpl<>(list);
    }

    @Override
    public EmployeeBank get(UUID id) {
        EmployeeBankEntity employeeBankEntity = employeeBankRepository.findOne(id);
        return model(employeeBankEntity);
    }

    private EmployeeBank model(EmployeeBankEntity entity) {
        if (entity == null) return null;
        EmployeeBank model = new EmployeeBank();
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
        if (entity.getBank() != null)
            model.setBank(entity.getBank().extractModel());
        return model;
    }

    private EmployeeBankEntity entityForm(EmployeeBankForm model) {
        EmployeeBankEntity entity = new EmployeeBankEntity();
        entity.setPosition(model.getPosition());
        UserEntity userEntity = userRepository.findOne(model.getUser().getId());
        entity.setUser(userEntity);
        BankEntity bankEntity = bankRepository.findOne(model.getBank().getId());
        entity.setBank(bankEntity);
        return entity;
    }

}
