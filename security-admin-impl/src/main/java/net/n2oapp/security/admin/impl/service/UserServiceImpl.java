package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.audit.AuditService;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.MailService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.impl.entity.*;
import net.n2oapp.security.admin.impl.repository.AccountTypeRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.specification.UserSpecifications;
import net.n2oapp.security.admin.impl.util.PasswordGenerator;
import net.n2oapp.security.admin.impl.util.UserValidations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

/**
 * Реализация сервиса управления пользователями
 */
@Transactional
public class UserServiceImpl implements UserService {
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String STATUS = "status";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final SsoUserRoleProvider provider;

    @Autowired
    private PasswordGenerator passwordGenerator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserValidations userValidations;
    @Autowired
    private AuditService auditService;

    @Value("${access.user.send-mail-delete-user:false}")
    private Boolean sendMailDelete;

    @Value("${access.user.send-mail-activate-user:false}")
    private Boolean sendMailActivate;

    @Value("${access.email-as-username:false}")
    private Boolean emailAsUsername;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           SsoUserRoleProvider provider, AccountTypeRepository accountTypeRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.accountTypeRepository = accountTypeRepository;
        this.provider = provider;
    }

    @Override
    public User create(UserForm user) {
        validateUsernameEmailSnils(user);
        String password = (user.getPassword() != null) ? user.getPassword() : user.getTemporaryPassword();
        if (nonNull(user.getPassword()))
            userValidations.checkPassword(password, user.getPasswordCheck(), user.getId());
        if (isNull(password)) {
            password = passwordGenerator.generate();
            user.setTemporaryPassword(password);
        }
        user.setRoles(excludeDummyRoles(user.getRoles()));
        UserEntity entity = entityForm(new UserEntity(), user);
        String passwordHash = passwordEncoder.encode(password);
        //сохраняем пароль в закодированном виде
        entity.setPasswordHash(passwordHash);

        if (user.getAccountTypeCode() != null) {
            AccountTypeEntity accountType = accountTypeRepository.findByCode(user.getAccountTypeCode())
                    .orElseThrow(() -> new UserException("exception.accountTypeNotFound"));
            List<RoleEntity> accountTypeRoles = null;
            if (accountType.getRoleList() != null) {
                accountTypeRoles = accountType.getRoleList().stream()
                        .filter(r -> r.getId() != null && r.getId().getRole() != null && r.getId().getRole().getId() != null)
                        .map(r -> {
                            RoleEntity roleEntity = new RoleEntity();
                            roleEntity.setId(r.getId().getRole().getId());
                            return roleEntity;
                        }).collect(Collectors.toList());
            }
            //        todo SECURITY-396
            if (accountTypeRoles != null) {
                if (user.getRoles() == null)
                    entity.getAccounts().get(0).setRoleList(accountTypeRoles);
                else
                    entity.getAccounts().get(0).getRoleList().addAll(accountTypeRoles);
            }
            entity.getAccounts().get(0).setUserLevel(accountType.getUserLevel());
            entity.setStatus(accountType.getStatus());
        }
        UserEntity savedUser = userRepository.save(entity);
        // получаем модель для сохранения SSO провайдером, ему надо передать незакодированный пароль
//               todo SECURITY-396
        if (provider.isSupports(savedUser.getAccounts().get(0).getExternalSystem())) {
            SsoUser ssoUser = model(savedUser);
            ssoUser.setPassword(password);
            // если пароль временный, то требуем смены пароля при следующем входе пользователя
            if (user.getTemporaryPassword() != null)
                ssoUser.setRequiredActions(Collections.singletonList("UPDATE_PASSWORD"));
            ssoUser = provider.createUser(ssoUser);
            if (nonNull(ssoUser)) {
                UserEntity changedSsoUser = entityProvider(ssoUser);
                changedSsoUser.setPasswordHash(passwordHash);
                changedSsoUser.setExpirationDate(savedUser.getExpirationDate());
                savedUser = userRepository.save(changedSsoUser);
            }
        }
        if (Boolean.TRUE.equals(user.getSendOnEmail()) && user.getEmail() != null) {
            mailService.sendWelcomeMail(user);
        }
        return audit("audit.userCreate", model(savedUser));
    }

    @Override
    public User register(UserRegisterForm user) {
        UserForm form = new UserForm();
        form.setEmail(user.getEmail());
        form.setUsername(user.getUsername());
        form.setPassword(user.getPassword());
        form.setPasswordCheck(user.getPassword());
        form.setAccountTypeCode(user.getAccountTypeCode());
        form.setName(user.getName());
        form.setSurname(user.getSurname());
        form.setPatronymic(user.getPatronymic());
        form.setSendOnEmail(user.getSendPasswordToEmail());
        form.setIsActive(user.getIsActive() != null ? user.getIsActive() : true);
        form.setExpirationDate(user.getExpirationDate());
        return create(form);
    }

    @Override
    public User update(UserForm user) {
        return doUpdate(user);
    }

    @Override
    public User patch(Map<String, Object> userInfo) {
        if (userInfo == null)
            throw new UserException("exception.wrongRequest");

        if (!userInfo.containsKey(ID) && !userInfo.containsKey(USERNAME))
            throw new UserException("exception.userWithoutIdAndUsername");

        UserEntity entity = null;
        if (userInfo.containsKey(ID) && nonNull(userInfo.get(ID))) {
            entity = userRepository.findById(Integer.parseInt(userInfo.get(ID).toString())).orElse(null);
        }
        if (isNull(entity) && userInfo.containsKey(USERNAME) && nonNull(userInfo.get(USERNAME))) {
            entity = userRepository.findOneByUsernameIgnoreCase(userInfo.get(USERNAME).toString());
        }

        if (isNull(entity))
            throw new NotFoundException();

        UserForm user = obtainUserForm(entity, userInfo);
        return doUpdate(user);
    }

    public User doUpdate(UserForm user) {
        validateUsernameEmailSnils(user);
        if (nonNull(user.getPassword())) {
            userValidations.checkPassword(user.getPassword(), user.getPasswordCheck(), user.getId());
        }
        UserEntity entityUser = userRepository.getOne(user.getId());
        boolean isActiveChanged = false;
        if (nonNull(user.getIsActive()))
            isActiveChanged = !Objects.equals(entityUser.getIsActive(), user.getIsActive());
        if (entityUser.getUsername().equals(getContextUserName()) && isActiveChanged) {
            throw new UserException("exception.selfChangeActivity");
        }
        user.setRoles(excludeDummyRoles(user.getRoles()));
        entityUser = entityForm(entityUser, user);
        // кодируем пароль перед сохранением в бд если он изменился
        if (nonNull(user.getPassword()))
            entityUser.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        UserEntity updatedUser = userRepository.save(entityUser);
        //в провайдер отправляем незакодированный пароль, если он изменился, и отправляем null, если не изменялся пароль
        //        todo SECURITY-396
        AccountEntity account = CollectionUtils.firstElement(updatedUser.getAccounts());
        if (nonNull(account) && provider.isSupports(account.getExternalSystem())) {
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
        SsoUser user = model(userRepository.findById(id).orElseThrow(() -> new UserException("exception.noUserWithSuchId")));
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
//                todo SECURITY-396
        AccountEntity account = CollectionUtils.firstElement(userEntity.getAccounts());
        if (nonNull(account) && provider.isSupports(account.getExternalSystem())) {
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

        UserEntity userEntity = null;
        if (user.getId() != null)
            userEntity = userRepository.getOne(user.getId());
        if (userEntity == null && user.getEmail() != null)
            userEntity = userRepository.findOneByEmailIgnoreCase(user.getEmail());
        if (userEntity == null && user.getUsername() != null)
            userEntity = userRepository.findOneByUsernameIgnoreCase(user.getUsername());
        if (userEntity == null && user.getSnils() != null)
            userEntity = userRepository.findOneBySnilsIgnoreCase(user.getSnils()).orElse(null);

        if (userEntity != null) {
            user.setEmail(userEntity.getEmail());
            user.setUsername(userEntity.getUsername());
            user.setName(userEntity.getName());
            user.setSurname(userEntity.getSurname());
            user.setPatronymic(userEntity.getPatronymic());
            if (isNull(password)) {
                password = passwordGenerator.generate();
            }
            userEntity.setPasswordHash(passwordEncoder.encode(password));
            UserEntity updatedUser = userRepository.save(userEntity);
//        todo SECURITY-396
            //в провайдер отправляем незакодированный пароль
            AccountEntity account = CollectionUtils.firstElement(updatedUser.getAccounts());
            if (nonNull(account) && provider.isSupports(account.getExternalSystem())) {
                SsoUser ssoUser = model(updatedUser);
                ssoUser.setPassword(password);
//             если пароль временный, то требуем смены пароля при следующем входе пользователя
                if (isNull(user.getPassword()) || user.getTemporaryPassword() != null) {
                    user.setPassword(password);
                    ssoUser.setRequiredActions(Collections.singletonList("UPDATE_PASSWORD"));
                }
                provider.resetPassword(ssoUser);
            }

            if (Boolean.TRUE.equals(user.getSendOnEmail()) && nonNull(user.getEmail())) {
                mailService.sendResetPasswordMail(user);
            }
        }
    }

    @Override
    public Password generatePassword() {
        Password result = new Password();
        result.password = passwordGenerator.generate();
        return result;
    }

    public void setEmailAsUsername(Boolean emailAsUsername) {
        this.emailAsUsername = emailAsUsername;
    }

    private UserEntity entityForm(UserEntity entity, UserForm model) {
        entity.setUsername(model.getUsername());
        entity.setName(model.getName());
        entity.setSurname(model.getSurname());
        entity.setPatronymic(model.getPatronymic());
        entity.setIsActive(model.getIsActive());
        entity.setEmail(model.getEmail());
        entity.setSnils(model.getSnils());
        entity.setStatus(model.getStatus());
        //        todo SECURITY-396
        if (isNull(CollectionUtils.firstElement(entity.getAccounts())))
            entity.getAccounts().add(new AccountEntity());
        entity.getAccounts().get(0).setUserLevel(nonNull(model.getUserLevel()) ? UserLevel.valueOf(model.getUserLevel()) : null);
        entity.getAccounts().get(0).setDepartment(nonNull(model.getDepartmentId()) ? new DepartmentEntity(model.getDepartmentId()) : null);
        entity.getAccounts().get(0).setOrganization(nonNull(model.getOrganizationId()) ? new OrganizationEntity(model.getOrganizationId()) : null);
        entity.getAccounts().get(0).setRegion(nonNull(model.getRegionId()) ? new RegionEntity(model.getRegionId()) : null);
        if (nonNull(model.getRoles()))
            entity.getAccounts().get(0).setRoleList(model.getRoles().stream().map(RoleEntity::new).collect(Collectors.toList()));
        entity.setExpirationDate(model.getExpirationDate());
        return entity;
    }

    private UserEntity entityProvider(SsoUser modelFromProvider) {
        UserEntity entity = new UserEntity();
        entity.setId(modelFromProvider.getId());
        entity.setUsername(modelFromProvider.getUsername());
        entity.setName(modelFromProvider.getName());
        entity.setSurname(modelFromProvider.getSurname());
        entity.setPatronymic(modelFromProvider.getPatronymic());
        entity.setIsActive(modelFromProvider.getIsActive());
        entity.setEmail(modelFromProvider.getEmail());
        entity.setSnils(modelFromProvider.getSnils());
        entity.setStatus(modelFromProvider.getStatus());
        //        todo SECURITY-396
        if (isNull(CollectionUtils.firstElement(entity.getAccounts())))
            entity.getAccounts().add(new AccountEntity());
        entity.getAccounts().get(0).setExternalUid(modelFromProvider.getExtUid());
        entity.getAccounts().get(0).setUserLevel(modelFromProvider.getUserLevel());
        entity.getAccounts().get(0).setExternalSystem(modelFromProvider.getExtSys());
        if (nonNull(modelFromProvider.getDepartment()))
            entity.getAccounts().get(0).setDepartment(new DepartmentEntity(modelFromProvider.getDepartment().getId()));
        if (nonNull(modelFromProvider.getOrganization()))
            entity.getAccounts().get(0).setOrganization(new OrganizationEntity(modelFromProvider.getOrganization().getId()));
        if (nonNull(modelFromProvider.getRegion()))
            entity.getAccounts().get(0).setRegion(new RegionEntity(modelFromProvider.getRegion().getId()));
        if (nonNull(modelFromProvider.getRoles()))
            entity.getAccounts().get(0).setRoleList(modelFromProvider.getRoles().stream().map(r -> new RoleEntity(r.getId())).collect(Collectors.toList()));
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
        model.setStatus(entity.getStatus());
        model.setExpirationDate(entity.getExpirationDate());
//        todo SECURITY-396 перенести в сервис аккаунтов
        if (!CollectionUtils.isEmpty(entity.getAccounts())) {
            AccountEntity accountEntity = entity.getAccounts().get(0);
            Account account = new Account();
            account.setUserLevel(accountEntity.getUserLevel());
            account.setDepartment(model(accountEntity.getDepartment()));
            account.setOrganization(model(accountEntity.getOrganization()));
            account.setRegion(model(accountEntity.getRegion()));
            account.setExtSys(accountEntity.getExternalSystem());
            account.setExtUid(accountEntity.getExternalUid());
            if (nonNull(accountEntity.getRoleList())) {
                account.setRoles(accountEntity.getRoleList().stream().map(e -> {
                    RoleEntity re = roleRepository.findById(e.getId()).get();
                    return model(re);
                }).collect(Collectors.toList()));
            }
            model.setAccount(account);
        }

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
        String fio = builder.toString().strip();
        model.setFio(hasText(fio) ? fio : null);
//        todo SECURITY-396
//        if (nonNull(entity.getRoleList())) {
//            model.setRoles(entity.getRoleList().stream().map(e -> {
//                RoleEntity re = roleRepository.findById(e.getId()).get();
//                return model(re);
//            }).collect(Collectors.toList()));
//        }
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
        model.setInn(entity.getInn());
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

    private void validateUsernameEmailSnils(UserForm user) {
        if (!Boolean.TRUE.equals(emailAsUsername)) {
            userValidations.checkUsernameUniq(user.getId(), model(userRepository.findOneByUsernameIgnoreCase(user.getUsername())));
            userValidations.checkUsername(user.getUsername());
        } else {
            userValidations.checkEmailUniq(user.getId(), model(userRepository.findOneByEmailIgnoreCase(user.getEmail())));
            user.setUsername(user.getEmail());
        }
        if (nonNull(user.getEmail()))
            userValidations.checkEmail(user.getEmail());
        if (nonNull(user.getSnils()))
            userValidations.checkSnils(user.getSnils());
    }

    private User audit(String action, User user) {
        auditService.audit(action, user, "" + user.getId(), "audit.user");
        return user;
    }

    private UserForm obtainUserForm(UserEntity entity, Map<String, Object> userInfo) {
        UserForm userForm = new UserForm();
        userForm.setId((Integer) userInfo.getOrDefault(ID, entity.getId()));
        userForm.setUsername((String) userInfo.getOrDefault(USERNAME, entity.getUsername()));
        userForm.setEmail((String) userInfo.getOrDefault("email", entity.getEmail()));
        userForm.setSurname((String) userInfo.getOrDefault("surname", entity.getSurname()));
        userForm.setName((String) userInfo.getOrDefault("name", entity.getName()));
        userForm.setPatronymic((String) userInfo.getOrDefault("patronymic", entity.getPatronymic()));
        userForm.setPassword((String) userInfo.getOrDefault("password", null));
        userForm.setPasswordCheck((String) userInfo.getOrDefault("passwordCheck", null));
        userForm.setTemporaryPassword((String) userInfo.getOrDefault("temporaryPassword", null));
        userForm.setSendOnEmail((Boolean) userInfo.getOrDefault("sendOnEmail", false));
        userForm.setIsActive((Boolean) userInfo.getOrDefault("isActive", entity.getIsActive()));
        userForm.setSnils((String) userInfo.getOrDefault("snils", entity.getSnils()));
        //            todo SECURITY-396
        AccountEntity account = CollectionUtils.firstElement(entity.getAccounts());
        if (nonNull(account)) {
            userForm.setRoles((List<Integer>) userInfo.getOrDefault("roles", account.getRoleList().stream().map(RoleEntity::getId).collect(Collectors.toList())));
            userForm.setUserLevel((String) userInfo.getOrDefault("userLevel", account.getUserLevel() != null ? account.getUserLevel().getName() : null));
            userForm.setDepartmentId((Integer) userInfo.getOrDefault("departmentId", account.getDepartment() != null ? account.getDepartment().getId() : null));
            userForm.setRegionId((Integer) userInfo.getOrDefault("regionId", account.getRegion() != null ? account.getRegion().getId() : null));
            userForm.setOrganizationId((Integer) userInfo.getOrDefault("organizationId", account.getOrganization() != null ? account.getOrganization().getId() : null));
            userForm.setExtSys((String) userInfo.getOrDefault("extSys", account.getExternalSystem()));
            userForm.setExtUid((String) userInfo.getOrDefault("extUid", account.getExternalUid()));
        }
        userForm.setStatus(userInfo.containsKey(STATUS) ? UserStatus.valueOf((String) userInfo.get(STATUS)) : entity.getStatus());
        userForm.setAccountTypeCode((String) userInfo.getOrDefault("accountTypeCode", null));
        userForm.setExpirationDate((LocalDateTime) userInfo.getOrDefault("expirationDate", null));
        return userForm;
    }

    /**
     * Значение roleId > 0, потому что в наборе могут присутствовать dummy роли, которые не должны быть учтены
     * и имеют отрицательное значение id.
     *
     * @param roles список ролей пользователя
     * @return отфильтрованный список ролей
     */
    private List<Integer> excludeDummyRoles(List<Integer> roles) {
        if (nonNull(roles))
            return roles.stream().filter(roleId -> roleId > 0).collect(Collectors.toList());
        return new ArrayList<>();
    }
}
