package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.MailService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.commons.util.PasswordGenerator;
import net.n2oapp.security.admin.commons.util.UserValidations;
import net.n2oapp.security.admin.impl.audit.AuditHelper;
import net.n2oapp.security.admin.impl.entity.*;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.specification.UserSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Реализация сервиса управления пользователями
 */
@Transactional
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SsoUserRoleProvider provider;

    @Autowired
    private PasswordGenerator passwordGenerator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserValidations userValidations;
    @Autowired
    private AuditHelper audit;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, SsoUserRoleProvider provider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.provider = provider;
    }

    @Override
    public User create(UserForm user) {
        userValidations.checkUsernameUniq(user.getId(), model(userRepository.findOneByUsernameIgnoreCase(user.getUsername())));
        userValidations.checkUsername(user.getUsername());
        userValidations.checkEmail(user.getEmail());
        String password = (user.getPassword() != null) ? user.getPassword() : user.getTemporaryPassword();
        if (user.getPassword() != null)
            userValidations.checkPassword(password, user.getPasswordCheck(), user.getId());
        if (password == null) {
            password = passwordGenerator.generate();
            user.setPassword(password);
        }
        //сохраняем пароль в закодированном виде
        UserEntity entity = entityForm(new UserEntity(), user);
        String passwordHash = passwordEncoder.encode(password);
        entity.setPasswordHash(passwordHash);
        UserEntity savedUser = userRepository.save(entity);
        // получаем модель для сохранения SSO провайдером, ему надо передать незакодированный пароль
        if (provider.isSupports(savedUser.getExtSys())) {
            User ssoUser = model(savedUser);
            ssoUser.setPassword(password);
            ssoUser = provider.createUser(ssoUser);
            if (ssoUser != null) {
                UserEntity changedSsoUser = entity(ssoUser);
                changedSsoUser.setPasswordHash(passwordHash);
                savedUser = userRepository.save(changedSsoUser);
            }
        }
        mailService.sendWelcomeMail(user);
        return audit("audit.userCreate", model(savedUser));
    }

    @Override
    public User update(UserForm user) {
        userValidations.checkUsernameUniq(user.getId(), model(userRepository.findOneByUsernameIgnoreCase(user.getUsername())));
        userValidations.checkUsername(user.getUsername());
        userValidations.checkEmail(user.getEmail());
        if (user.getPassword() != null) {
            userValidations.checkPassword(user.getPassword(), user.getPasswordCheck(), user.getId());
        }
        UserEntity entityUser = userRepository.getOne(user.getId());
        entityUser = entityForm(entityUser, user);
        // кодируем пароль перед сохранением в бд если он изменился
        if (user.getPassword() != null)
            entityUser.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        UserEntity updatedUser = userRepository.save(entityUser);
        //в провайдер отправляем незакодированный пароль, если он изменился, и отправляем null, если не изменялся пароль
        if (provider.isSupports(updatedUser.getExtSys())) {
            User ssoUser = model(updatedUser);
            if (user.getPassword() == null) {
                ssoUser.setPassword(null);
            } else {
                ssoUser.setPassword(user.getPassword());
            }
            provider.updateUser(ssoUser);
        }
        return audit("audit.userUpdate", model(updatedUser));
    }

    @Override
    public void delete(Integer id) {
        User user = model(userRepository.findById(id).orElse(null));
        userRepository.deleteById(id);
        if (user != null) {
            audit("audit.userDelete", user);
            if (provider.isSupports(user.getExtSys())) provider.deleteUser(user);
        }
    }

    @Override
    public User getById(Integer id) {
        UserEntity entity = userRepository.findById(id).orElse(null);
        return model(entity);
    }

    @Override
    public Page<User> findAll(UserCriteria criteria) {
        final Specification<UserEntity> specification = new UserSpecifications(criteria);
        if (criteria.getOrders().stream().map(Sort.Order::getProperty).anyMatch("fio"::equals)) {
            Sort.Direction orderFio = criteria.getOrders().stream().filter(o -> o.getProperty().equals("fio")).findAny().get().getDirection();
            criteria.getOrders().add(new Sort.Order(orderFio, "surname"));
            criteria.getOrders().add(new Sort.Order(orderFio, "name"));
            criteria.getOrders().add(new Sort.Order(orderFio, "patronymic"));
            criteria.getOrders().removeIf(s -> s.getProperty().equals("fio"));
        }
        criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "id"));
        final Page<UserEntity> all = userRepository.findAll(specification, criteria);
        return all.map(this::model);
    }

    @Override
    public User changeActive(Integer id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        userEntity.setIsActive(!userEntity.getIsActive());
        User result = model(userRepository.save(userEntity));
        if (provider.isSupports(userEntity.getExtSys())) {
            provider.changeActivity(result);
        }
        return audit("audit.userChangeActive", result);
    }

    @Override
    public Boolean checkUniqueUsername(String username) {
        return userRepository.findOneByUsernameIgnoreCase(username) == null;
    }

    @Override
    public User loadSimpleDetails(Integer id) {
        User simpleUser = new User();

        if (id != null) {
            User user = getById(id);

            if (user != null) {
                simpleUser.setId(id);
                simpleUser.setUsername(user.getUsername());
                simpleUser.setEmail(user.getEmail());
            }
        }

        simpleUser.setTemporaryPassword(passwordGenerator.generate());
        return simpleUser;
    }

    @Override
    public void resetPassword(UserForm user) {
        // используем либо установленный пользователем, либо сгенерированный пароль
        String password = (user.getPassword() != null) ? user.getPassword() : user.getTemporaryPassword();

        if (user.getId() != null && password != null) {
            UserEntity userEntity = userRepository.getOne(user.getId());

            if (userEntity != null) {
                userEntity.setPasswordHash(passwordEncoder.encode(password));
                UserEntity updatedUser = userRepository.save(userEntity);

                //в провайдер отправляем незакодированный пароль
                if (provider.isSupports(updatedUser.getExtSys())) {
                    User ssoUser = model(updatedUser);
                    ssoUser.setPassword(password);
                    provider.updateUser(ssoUser);
                }

                if (Boolean.TRUE.equals(user.getSendOnEmail()))
                    mailService.sendResetPasswordMail(user);
            }
        }
    }

    private UserEntity entityForm(UserEntity entity, UserForm model) {
        entity.setExtUid(model.getExtUid());
        entity.setUsername(model.getUsername());
        entity.setName(model.getName());
        entity.setSurname(model.getSurname());
        entity.setPatronymic(model.getPatronymic());
        entity.setIsActive(model.getIsActive());
        entity.setExtSys(model.getExtSys());
        entity.setEmail(model.getEmail());
        entity.setUserLevel(nonNull(model.getUserLevel()) ? UserLevel.valueOf(model.getUserLevel()) : null);
        entity.setDepartment(nonNull(model.getDepartmentId()) ? new DepartmentEntity(model.getDepartmentId()) : null);
        entity.setOrganization(nonNull(model.getOrganizationId()) ? new OrganizationEntity(model.getOrganizationId()) : null);
        entity.setRegion(nonNull(model.getRegionId()) ? new RegionEntity(model.getRegionId()) : null);
        if (model.getRoles() != null)
            entity.setRoleList(model.getRoles().stream().map(RoleEntity::new).collect(Collectors.toList()));
        return entity;
    }

    private UserEntity entity(User model) {
        UserEntity entity = new UserEntity();
        entity.setId(model.getId());
        entity.setExtUid(model.getExtUid());
        entity.setUsername(model.getUsername());
        entity.setName(model.getName());
        entity.setSurname(model.getSurname());
        entity.setPatronymic(model.getPatronymic());
        entity.setIsActive(model.getIsActive());
        entity.setExtSys(model.getExtSys());
        entity.setEmail(model.getEmail());
        entity.setUserLevel(model.getUserLevel());
        if (nonNull(model.getDepartment()))
            entity.setDepartment(new DepartmentEntity(model.getDepartment().getId()));
        if (nonNull(model.getOrganization()))
            entity.setOrganization(new OrganizationEntity(model.getOrganization().getId()));
        if (nonNull(model.getRegion()))
            entity.setRegion(new RegionEntity(model.getRegion().getId()));
        if (model.getRoles() != null)
            entity.setRoleList(model.getRoles().stream().map(r -> new RoleEntity(r.getId())).collect(Collectors.toList()));
        return entity;
    }

    private User model(UserEntity entity) {
        if (entity == null) return null;
        User model = new User();
        model.setId(entity.getId());
        model.setExtUid(entity.getExtUid());
        model.setUsername(entity.getUsername());
        model.setName(entity.getName());
        model.setSurname(entity.getSurname());
        model.setPatronymic(entity.getPatronymic());
        model.setIsActive(entity.getIsActive());
        model.setExtSys(entity.getExtSys());
        model.setEmail(entity.getEmail());
        model.setPasswordHash(entity.getPasswordHash());
        model.setUserLevel(entity.getUserLevel());
        model.setDepartment(model(entity.getDepartment()));
        model.setOrganization(model(entity.getOrganization()));
        model.setRegion(model(entity.getRegion()));
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
                RoleEntity re = roleRepository.findById(e.getId()).get();
                return model(re);
            }).collect(Collectors.toList()));
        }
        return model;
    }

    private Role model(RoleEntity entity) {
        if (entity == null) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setNameWithSystem(entity.getName());
        if (entity.getSystemCode() != null)
            model.setNameWithSystem(model.getNameWithSystem() + "(" + entity.getSystemCode().getName() + ")");

        return model;
    }

    private Department model(DepartmentEntity entity) {
        if (entity == null) return null;
        Department model = new Department();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        return model;
    }

    private Organization model(OrganizationEntity entity) {
        if (entity == null) return null;
        Organization model = new Organization();
        model.setId(entity.getId());
        model.setFullName(entity.getFullName());
        model.setShortName(entity.getShortName());
        model.setCode(entity.getCode());
        model.setOgrn(entity.getOgrn());
        model.setOkpo(entity.getOkpo());
        return model;
    }

    private Region model(RegionEntity entity) {
        if (entity == null) return null;
        Region model = new Region();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setOkato(entity.getOkato());
        return model;
    }

    private User audit(String action, User user) {
        audit.audit(action, user, "" + user.getId(), user.getUsername());
        return user;
    }
}
