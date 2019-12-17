package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
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

    @Value("${access.user.send-mail-delete-user:false}")
    private Boolean sendMailDelete;

    @Value("${access.user.send-mail-activate-user:false}")
    private Boolean sendMailActivate;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, SsoUserRoleProvider provider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.provider = provider;
    }

    @Override
    public User create(UserForm user) {
        userValidations.checkUsernameUniq(user.getId(), model(userRepository.findOneByUsernameIgnoreCase(user.getUsername())));
        userValidations.checkUsername(user.getUsername());
        if (nonNull(user.getEmail()))
            userValidations.checkEmail(user.getEmail());
        if (nonNull(user.getSnils()))
            userValidations.checkSnils(user.getSnils());
        String password = (user.getPassword() != null) ? user.getPassword() : user.getTemporaryPassword();
        if (nonNull(user.getPassword()))
            userValidations.checkPassword(password, user.getPasswordCheck(), user.getId());
        if (isNull(password)) {
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
            SsoUser ssoUser = model(savedUser);
            ssoUser.setPassword(password);
            // если пароль временный, то требуем смены пароля при следующем входе пользователя
            if (user.getTemporaryPassword() != null)
                ssoUser.setRequiredActions(Collections.singletonList("UPDATE_PASSWORD"));
            ssoUser = provider.createUser(ssoUser);
            if (nonNull(ssoUser)) {
                UserEntity changedSsoUser = entityProvider(ssoUser);
                changedSsoUser.setPasswordHash(passwordHash);
                savedUser = userRepository.save(changedSsoUser);
            }
        }
        if (Boolean.TRUE.equals(user.getSendOnEmail()) && user.getEmail() != null) {
            mailService.sendWelcomeMail(user);
        }
        return audit("audit.userCreate", model(savedUser));
    }

    @Override
    public User update(UserForm user) {
        userValidations.checkUsernameUniq(user.getId(), model(userRepository.findOneByUsernameIgnoreCase(user.getUsername())));
        userValidations.checkUsername(user.getUsername());
        if (nonNull(user.getEmail()))
            userValidations.checkEmail(user.getEmail());
        if (nonNull(user.getSnils()))
            userValidations.checkSnils(user.getSnils());
        if (nonNull(user.getPassword())) {
            userValidations.checkPassword(user.getPassword(), user.getPasswordCheck(), user.getId());
        }
        UserEntity entityUser = userRepository.getOne(user.getId());
        boolean isActiveChanged = !Objects.equals(entityUser.getIsActive(), user.getIsActive());
        if (entityUser.getUsername().equals(getContextUserName()) && isActiveChanged) {
            throw new UserException("exception.selfChangeActivity");
        }
        entityUser = entityForm(entityUser, user);
        // кодируем пароль перед сохранением в бд если он изменился
        if (nonNull(user.getPassword()))
            entityUser.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        UserEntity updatedUser = userRepository.save(entityUser);
        //в провайдер отправляем незакодированный пароль, если он изменился, и отправляем null, если не изменялся пароль
        if (provider.isSupports(updatedUser.getExtSys())) {
            SsoUser ssoUser = model(updatedUser);
            if (isNull(user.getPassword())) {
                ssoUser.setPassword(null);
            } else {
                ssoUser.setPassword(user.getPassword());
            }
            provider.updateUser(ssoUser);
        }
        User result = model(updatedUser);
        if (sendMailActivate && isActiveChanged) {
            mailService.sendChangeActivateMail(result);
        }
        return audit("audit.userUpdate", result);
    }

    @Override
    public void delete(Integer id) {
        SsoUser user = model(userRepository.findById(id).orElse(null));
        if (nonNull(user) && user.getUsername().equals(getContextUserName())) {
            throw new UserException("exception.selfDelete");
        }
        userRepository.deleteById(id);
        if (nonNull(user)) {
            if (sendMailDelete) {
                mailService.sendUserDeletedMail(user);
            }
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
        if (criteria.getRoleIds() != null)
            criteria.setRoleIds(criteria.getRoleIds().stream().filter(roleId -> roleId > 0).collect(Collectors.toList()));
        final Specification<UserEntity> specification = new UserSpecifications(criteria);
        if (criteria.getOrders() == null)
            criteria.setOrders(new ArrayList<>());
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
        if (nonNull(userEntity) && userEntity.getUsername().equals(getContextUserName())) {
            throw new UserException("exception.selfChangeActivity");
        }
        userEntity.setIsActive(!userEntity.getIsActive());
        SsoUser result = model(userRepository.save(userEntity));
        if (provider.isSupports(userEntity.getExtSys())) {
            provider.changeActivity(result);
        }
        if (sendMailActivate) {
            mailService.sendChangeActivateMail(model(userEntity));
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
        if (nonNull(user.getEmail())) {
            userValidations.checkEmail(user.getEmail());
        }
        // используем либо установленный пользователем, либо сгенерированный пароль
        String password = (user.getPassword() != null) ? user.getPassword() : user.getTemporaryPassword();
        if (nonNull(user.getPassword())) {
            userValidations.checkPassword(password, user.getPasswordCheck(), user.getId());
        }
        if (isNull(password)) {
            password = passwordGenerator.generate();
        }

        if (user.getId() != null) {
            UserEntity userEntity = userRepository.getOne(user.getId());

            if (userEntity != null) {
                userEntity.setPasswordHash(passwordEncoder.encode(password));
                UserEntity updatedUser = userRepository.save(userEntity);

                //в провайдер отправляем незакодированный пароль
                if (provider.isSupports(updatedUser.getExtSys())) {
                    SsoUser ssoUser = model(updatedUser);
                    ssoUser.setPassword(password);
                    // если пароль временный, то требуем смены пароля при следующем входе пользователя
                    if (user.getTemporaryPassword() != null)
                        ssoUser.setRequiredActions(Collections.singletonList("UPDATE_PASSWORD"));
                    provider.resetPassword(ssoUser);
                }

                if (Boolean.TRUE.equals(user.getSendOnEmail()) && nonNull(user.getEmail())) {
                    mailService.sendResetPasswordMail(user);
                }
            }
        }
    }

    private UserEntity entityForm(UserEntity entity, UserForm model) {
        entity.setUsername(model.getUsername());
        entity.setName(model.getName());
        entity.setSurname(model.getSurname());
        entity.setPatronymic(model.getPatronymic());
        entity.setIsActive(model.getIsActive());
        entity.setEmail(model.getEmail());
        entity.setSnils(model.getSnils());
        entity.setUserLevel(nonNull(model.getUserLevel()) ? UserLevel.valueOf(model.getUserLevel()) : null);
        entity.setDepartment(nonNull(model.getDepartmentId()) ? new DepartmentEntity(model.getDepartmentId()) : null);
        entity.setOrganization(nonNull(model.getOrganizationId()) ? new OrganizationEntity(model.getOrganizationId()) : null);
        entity.setRegion(nonNull(model.getRegionId()) ? new RegionEntity(model.getRegionId()) : null);
        if (nonNull(model.getRoles()))
            entity.setRoleList(model.getRoles().stream().filter(roleId -> roleId > 0).map(RoleEntity::new).collect(Collectors.toList()));
        return entity;
    }

    private UserEntity entityProvider(SsoUser modelFromProvider) {
        UserEntity entity = new UserEntity();
        entity.setId(modelFromProvider.getId());
        entity.setExtUid(modelFromProvider.getExtUid());
        entity.setUsername(modelFromProvider.getUsername());
        entity.setName(modelFromProvider.getName());
        entity.setSurname(modelFromProvider.getSurname());
        entity.setPatronymic(modelFromProvider.getPatronymic());
        entity.setIsActive(modelFromProvider.getIsActive());
        entity.setExtSys(modelFromProvider.getExtSys());
        entity.setEmail(modelFromProvider.getEmail());
        entity.setSnils(modelFromProvider.getSnils());
        entity.setUserLevel(modelFromProvider.getUserLevel());
        if (nonNull(modelFromProvider.getDepartment()))
            entity.setDepartment(new DepartmentEntity(modelFromProvider.getDepartment().getId()));
        if (nonNull(modelFromProvider.getOrganization()))
            entity.setOrganization(new OrganizationEntity(modelFromProvider.getOrganization().getId()));
        if (nonNull(modelFromProvider.getRegion()))
            entity.setRegion(new RegionEntity(modelFromProvider.getRegion().getId()));
        if (nonNull(modelFromProvider.getRoles()))
            entity.setRoleList(modelFromProvider.getRoles().stream().map(r -> new RoleEntity(r.getId())).collect(Collectors.toList()));
        return entity;
    }

    private String getContextUserName() {
        if (nonNull(SecurityContextHolder.getContext()) &&
                nonNull(SecurityContextHolder.getContext().getAuthentication()) &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                        instanceof org.springframework.security.core.userdetails.User) {
            return ((org.springframework.security.core.userdetails.User)
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        }

        return null;
    }

    private SsoUser model(UserEntity entity) {
        if (isNull(entity)) return null;
        SsoUser model = new SsoUser();
        model.setId(entity.getId());
        model.setUsername(entity.getUsername());
        model.setName(entity.getName());
        model.setSurname(entity.getSurname());
        model.setPatronymic(entity.getPatronymic());
        model.setIsActive(entity.getIsActive());
        model.setEmail(entity.getEmail());
        model.setSnils(entity.getSnils());
        model.setPasswordHash(entity.getPasswordHash());
        model.setUserLevel(entity.getUserLevel());
        model.setDepartment(model(entity.getDepartment()));
        model.setOrganization(model(entity.getOrganization()));
        model.setRegion(model(entity.getRegion()));
        model.setExtSys(entity.getExtSys());
        model.setExtUid(entity.getExtUid());
        StringBuilder builder = new StringBuilder();
        if (nonNull(entity.getSurname())) {
            builder.append(entity.getSurname()).append(" ");
        }
        if (nonNull(entity.getName())) {
            builder.append(entity.getName()).append(" ");
        }
        if (nonNull(entity.getPatronymic())) {
            builder.append(entity.getPatronymic());
        }
        model.setFio(builder.toString());
        if (nonNull(entity.getRoleList())) {
            model.setRoles(entity.getRoleList().stream().map(e -> {
                RoleEntity re = roleRepository.findById(e.getId()).get();
                return model(re);
            }).collect(Collectors.toList()));
        }
        return model;
    }

    private Role model(RoleEntity entity) {
        if (isNull(entity)) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setNameWithSystem(entity.getName());
        if (nonNull(entity.getSystemCode()))
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
