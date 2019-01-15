package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.EmployeeBankCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.model.bank.Bank;
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
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

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
        List<EmployeeBank> list = employeeBankRepository.findByBankId(UUID.fromString(criteria.getBankId())).stream().map(this::model).collect(Collectors.toList());
        return new PageImpl<>(list);

    }

    private EmployeeBank model(EmployeeBankEntity entity) {
        if (entity == null) return null;
        EmployeeBank model = new EmployeeBank();
        model.setId(entity.getId());
        model.setPosition(entity.getPosition());
        model.setUser(model(entity.getUser()));
        if(entity.getUser()!=null){
            model.setEmployeeName(Stream.of(entity.getUser().getSurname(), entity.getUser().getName(), entity.getUser().getPatronymic()).filter(s -> s!=null && !s.isEmpty()).collect(joining(" ")));
        }
        model.setBank(model(entity.getBank()));
        return model;
    }

    private User model(UserEntity entity) {
        if (entity == null) return null;
        User model = new User();
        model.setId(entity.getId());
        model.setGuid(entity.getGuid() == null ? null : entity.getGuid().toString());
        model.setUsername(entity.getUsername());
        model.setName(entity.getName());
        model.setSurname(entity.getSurname());
        model.setPatronymic(entity.getPatronymic());
        model.setIsActive(entity.getIsActive());
        model.setEmail(entity.getEmail());
        StringBuilder builder = new StringBuilder();
        if (entity.getSurname() != null) {
            builder.append(entity.getSurname()).append(" ");
        }
        if (entity.getName() != null) {
            builder.append(entity.getName()).append(" ");
        }
        if (entity.getPatronymic() != null) {
            builder.append(entity.getPatronymic());
        }
        model.setFio(builder.toString());

        if (entity.getRoleList() != null) {
            model.setRoles(entity.getRoleList().stream().map(e -> {
                RoleEntity re = roleRepository.findOne(e.getId());
                return model(re);
            }).collect(Collectors.toList()));
        }

        return model;
    }

    private Bank model(BankEntity entity) {
        if (entity == null) return null;
        Bank model = new Bank();
        model.setId(entity.getId());
        model.setFullName(entity.getFullName());
        model.setShortName(entity.getShortName());
        model.setRegNum(entity.getRegNum());
        model.setInn(entity.getInn());
        model.setOgrn(entity.getOgrn());
        model.setKpp(entity.getKpp());
        model.setBik(entity.getBik());
        model.setActualAddress(entity.getActualAddress());
        model.setLegalAddress(entity.getLegalAddress());
        model.setLastActionDate(entity.getLastActionDate());
        model.setCreationDate(entity.getCreationDate());
        return model;
    }

    private EmployeeBankEntity entityForm( EmployeeBankForm model) {
        EmployeeBankEntity entity = new EmployeeBankEntity();
        entity.setPosition(model.getPosition());
        UserEntity userEntity = userRepository.findOne(model.getUser().getId());
        entity.setUser(userEntity);
        return entity;
    }


    private Role model(RoleEntity entity) {
        if (entity == null) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        return model;
    }

}
