package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.specification.UserSpecifications;
import net.n2oapp.security.admin.impl.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления пользователями
 */
@Transactional
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SsoUserRoleProvider provider;
    private PasswordGenerator passwordGenerator;
    private PasswordEncoder passwordEncoder;

    @Value("${n2o.security.validation.username:true}")
    private Boolean validationUsername;

    @Value("${n2o.security.validation.password.length}")
    private Integer validationPasswordLength;

    @Value("${n2o.security.validation.password.upper.case.letters}")
    private Boolean validationPasswordUpperCaseLetters;

    @Value("${n2o.security.validation.password.lower.case.letters}")
    private Boolean validationPasswordLowerCaseLetters;

    @Value("${n2o.security.validation.password.numbers}")
    private Boolean validationPasswordNumbers;

    @Value("${n2o.security.validation.password.special.symbols}")
    private Boolean validationPasswordSpecialSymbols;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, SsoUserRoleProvider provider, PasswordGenerator passwordGenerator, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.provider = provider;
        this.passwordGenerator = passwordGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(UserForm user) {
        checkUsernameUniq(user.getId(), user.getUsername());
        checkUsername(user.getUsername());
        checkEmail(user.getEmail());
        if (user.getPassword() != null) {
            checkPassword(user.getPassword(), user.getPasswordCheck(), user.getId());
        }
        UserEntity entity = entityForm(new UserEntity(), user);
        if (entity.getPassword() == null) {
            entity.setPassword(passwordGenerator.generate());
        }
        //сохраняем пароль в закодированном виде
        String password = entity.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        entity.setPassword(encodedPassword);
        UserEntity savedUser = userRepository.save(entity);
        // получаем модель для сохранения провайдером, ему надо передать незакодированный пароль
        User result = model(savedUser);
        result.setPassword(password);
        User providerResult = provider.createUser(result);
        if (providerResult != null) {
            providerResult.setPassword(encodedPassword);
            savedUser = userRepository.save(entity(providerResult));
        }

        return model(savedUser);
    }

    @Override
    public User update(UserForm user) {
        checkUsernameUniq(user.getId(), user.getUsername());
        checkUsername(user.getUsername());
        checkEmail(user.getEmail());
        if (user.getNewPassword() != null) {
            checkPassword(user.getNewPassword(), user.getPasswordCheck(), user.getId());
        }
        UserEntity entity = userRepository.getOne(user.getId());
        UserEntity ent = entityForm(entity, user);
        // кодируем пароль перед сохранением в бд если он изменился
        if (user.getNewPassword() != null) {
            ent.setPassword(passwordEncoder.encode(ent.getPassword()));
        }
        UserEntity resultEntity = userRepository.save(ent);
        //в провайдер отправляем незакодированный пароль, если он изменился. и отправляем null если не изменялся пароль
        User result = model(resultEntity);
        if (user.getNewPassword() == null) {
            result.setPassword(null);
        } else {
            result.setPassword(user.getPassword());
        }
        provider.updateUser(result);

        return model(resultEntity);
    }

    @Override
    public void delete(Integer id) {
        UserEntity user = userRepository.findOne(id);
        userRepository.delete(id);
        provider.deleteUser(model(user));
    }

    @Override
    public User getById(Integer id) {
        UserEntity entity = userRepository.findOne(id);
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
        if (!criteria.getOrders().stream().map(Sort.Order::getProperty).anyMatch("id"::equals)) {
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "id"));
        }
        final Page<UserEntity> all = (userRepository.findAll(specification, criteria));
        return all.map(this::model);
    }

    @Override
    public User changeActive(Integer id) {
        UserEntity userEntity = userRepository.findOne(id);
        userEntity.setIsActive(!userEntity.getIsActive());
        User result = model(userRepository.save(userEntity));
        provider.changeActivity(result);
        return result;

    }

    private UserEntity entityForm(UserEntity entity, UserForm model) {
        entity.setGuid(model.getGuid() == null ? null : UUID.fromString(model.getGuid()));
        entity.setUsername(model.getUsername());
        entity.setName(model.getName());
        entity.setSurname(model.getSurname());
        entity.setPassword(model.getPassword());
        entity.setPatronymic(model.getPatronymic());
        entity.setIsActive(model.getIsActive());
        if (model.getNewPassword() != null) {
            entity.setPassword(model.getNewPassword());
        }
        entity.setEmail(model.getEmail());
        if (model.getRoles() != null)
            entity.setRoleList(model.getRoles().stream().map(RoleEntity::new).collect(Collectors.toList()));
        return entity;
    }

    private UserEntity entity(User model) {
        UserEntity entity = new UserEntity();
        entity.setId(model.getId());
        entity.setGuid(model.getGuid() == null ? null : UUID.fromString(model.getGuid()));
        entity.setUsername(model.getUsername());
        entity.setName(model.getName());
        entity.setSurname(model.getSurname());
        entity.setPatronymic(model.getPatronymic());
        entity.setIsActive(model.getIsActive());
        entity.setPassword(model.getPassword());
        entity.setEmail(model.getEmail());
        if (model.getRoles() != null)
            entity.setRoleList(model.getRoles().stream().map(r -> new RoleEntity(r.getId())).collect(Collectors.toList()));
        return entity;
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
        model.setPassword(entity.getPassword());
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

    private Role model(RoleEntity entity) {
        if (entity == null) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        return model;
    }

    /**
     * Валидация на уникальность юзернейма пользователя
     */
    public boolean checkUsernameUniq(Integer id, String username) {
        UserEntity userEntity = userRepository.findOneByUsername(username);
        Boolean result = id == null ? userEntity == null : ((userEntity == null) || (userEntity.getId().equals(id)));
        if (result) {
            return true;
        } else
            throw new UserException("exception.uniqueUsername");
    }

    /**
     * Валидация на  ввод юзернейма согласно формату
     */
    private boolean checkUsername(String username) {
        if (validationUsername) {
            String regexp = "^[a-zA-Z][a-zA-Z0-9]+$";
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(username);
            if (!matcher.matches())
                throw new UserException("exception.wrongUsername");
        }
        return true;
    }

    /**
     * Валидация на  ввод email согласно формату
     */
    private boolean checkEmail(String email) {
        String regexp = "[A-Za-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@(?:[A-Za-z0-9]" +
                "(?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches())
            throw new UserException("exception.wrongEmail");
        return true;
    }

    /**
     * Валидация на  ввод пароля согласно формату
     */
    public boolean checkPassword(String password, String passwordCheck, Integer id) {
        if (password.length() < validationPasswordLength)
            throw new UserException("exception.passwordLength");
        String regexp;
        Pattern pattern;
        Matcher matcher;
        if (validationPasswordUpperCaseLetters) {
            regexp = "^(?=.*[A-Z])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new UserException("exception.uppercaseLetters");
        }
        if (validationPasswordLowerCaseLetters) {
            regexp = "^(?=.*[a-z])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new UserException("exception.lowercaseLetters");
        }
        if (validationPasswordNumbers) {
            regexp = "^(?=.*[0-9])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new UserException("exception.numbers");
        }
        if (validationPasswordSpecialSymbols) {
            regexp = "(?=.*[@#$%^&+=])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new UserException("exception.specialSymbols");
        }
        if (((id == null) || (passwordCheck != null)) && (!password.equals(passwordCheck))) {
            throw new UserException("exception.passwordsMatch");
        }
        return true;
    }

}
