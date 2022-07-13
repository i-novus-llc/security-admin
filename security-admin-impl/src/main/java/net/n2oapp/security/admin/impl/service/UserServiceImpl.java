package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.audit.AuditService;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.MailService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
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
    private final UserRepository userRepository;
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

    public UserServiceImpl(UserRepository userRepository, SsoUserRoleProvider provider) {
        this.userRepository = userRepository;
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
        UserEntity entity = entityForm(new UserEntity(), user);
        String passwordHash = passwordEncoder.encode(password);
        //сохраняем пароль в закодированном виде
        entity.setPasswordHash(passwordHash);
        UserEntity savedUser = userRepository.save(entity);

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
        entityUser = entityForm(entityUser, user);
        // кодируем пароль перед сохранением в бд если он изменился
        if (nonNull(user.getPassword()))
            entityUser.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        UserEntity updatedUser = userRepository.save(entityUser);
        User result = model(updatedUser);
        if (sendMailActivate && isActiveChanged) {
            mailService.sendChangeActivateMail(result);
        }
        return audit("audit.userUpdate", result);
    }

    @Override
    public void delete(Integer id) {
        SsoUser user = model(userRepository.findById(id).orElseThrow(() -> new UserException("exception.userNotFound")));
        if (nonNull(user) && user.getUsername().equals(getContextUserName())) {
            throw new UserException("exception.selfDelete");
        }
        userRepository.deleteById(id);
        if (nonNull(user)) {
            if (sendMailDelete)
                mailService.sendUserDeletedMail(user);
            audit("audit.userDelete", user);
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

        List<AccountEntity> accounts = userEntity.getAccounts();
        if (nonNull(accounts) && accounts.stream().anyMatch(a -> provider.isSupports(a.getExternalSystem())))
            provider.changeActivity(result);

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
            userRepository.save(userEntity);

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
        entity.setExpirationDate(model.getExpirationDate());
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
        model.setExpirationDate(entity.getExpirationDate());

        if (nonNull(entity.getRegion()))
            model.setRegion(new Region(entity.getRegion().getId(), entity.getRegion().getName()));

        StringJoiner joiner = new StringJoiner(" ");
        if (nonNull(entity.getSurname()))
            joiner.add(entity.getSurname());
        if (nonNull(entity.getName()))
            joiner.add(entity.getName());
        if (nonNull(entity.getPatronymic()))
            joiner.add(entity.getPatronymic());
        String fio = joiner.toString();
        model.setFio(hasText(fio) ? fio : null);

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
        userForm.setExpirationDate((LocalDateTime) userInfo.getOrDefault("expirationDate", null));
        return userForm;
    }
}
