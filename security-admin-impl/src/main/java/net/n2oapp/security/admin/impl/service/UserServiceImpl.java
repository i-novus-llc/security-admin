package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.specification.UserSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления пользователями
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

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

    @Override
    public User create(UserForm user) {
        checkUsernameUniq(user.getId(), user.getUsername());
        checkUsername(user.getUsername());
        checkEmail(user.getEmail());
        checkPassword(user.getPassword(), user.getPasswordCheck(), user.getId());
        return model(userRepository.save(entityForm(user)));
    }

    @Override
    public User update(UserForm user) {
        checkUsernameUniq(user.getId(), user.getUsername());
        checkUsername(user.getUsername());
        checkEmail(user.getEmail());
        checkPassword(user.getPassword(), user.getPasswordCheck(), user.getId());
        return model(userRepository.save(entityForm(user)));

    }

    @Override
    public void delete(Integer id) {
        userRepository.delete(id);

    }

    @Override
    public User getById(Integer id) {
        UserEntity entity = userRepository.findOne(id);
        return model(entity);
    }

    @Override
    public Page<User> findAll(UserCriteria criteria) {
        final Specification<UserEntity> specification = new UserSpecifications(criteria);
        final Page<UserEntity> all = userRepository.findAll(specification, criteria);
        return all.map(this::model);
    }

    @Override
    public User changeActive(Integer id) {
        UserEntity userEntity = userRepository.findOne(id);
        userEntity.setIsActive(!userEntity.getIsActive());
        return model(userRepository.save(userEntity));

    }

    private UserEntity entityForm(UserForm model) {
        UserEntity entity = new UserEntity();
        entity.setId(model.getId());
        entity.setUsername(model.getUsername());
        entity.setName(model.getName());
        entity.setSurname(model.getSurname());
        entity.setPatronymic(model.getPatronymic());
        entity.setIsActive(model.getIsActive());
        entity.setPassword(model.getPassword());
        entity.setEmail(model.getEmail());
        if (model.getRoles() != null)
            entity.setRoleList(model.getRoles().stream().map(RoleEntity::new).collect(Collectors.toList()));
        return entity;
    }

    private User model(UserEntity entity) {
        if (entity == null) return null;
        User model = new User();
        model.setId(entity.getId());
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
            model.setRoles(entity.getRoleList().stream().map(this::model).collect(Collectors.toList()));
        }
        return model;
    }

    private Role model(RoleEntity entity) {
        if (entity == null) return null;
        Role model = new Role();
        model.setId(entity.getId());
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
            throw new IllegalArgumentException("User with such username already exists");
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
                throw new IllegalArgumentException("Wrong username format");
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
            throw new IllegalArgumentException("Wrong email");
        return true;
    }

    /**
     * Валидация на  ввод пароля согласно формату
     */
    public boolean checkPassword(String password, String passwordCheck, Integer id) {
        if (password.length() < validationPasswordLength)
            throw new IllegalArgumentException("Wrong password length");
        String regexp;
        Pattern pattern;
        Matcher matcher;
        if (validationPasswordUpperCaseLetters) {
            regexp = "^(?=.*[A-Z])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new IllegalArgumentException("Wrong password format. Password must contain at least one uppercase letter");
        }
        if (validationPasswordLowerCaseLetters) {
            regexp = "^(?=.*[a-z])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new IllegalArgumentException("Wrong password format. Password must contain at least one lowercase letter");
        }
        if (validationPasswordNumbers) {
            regexp = "^(?=.*[0-9])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new IllegalArgumentException("Wrong password format. Password must contain at least one number");
        }
        if (validationPasswordSpecialSymbols) {
            regexp = "(?=.*[@#$%^&+=])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new IllegalArgumentException("Wrong password format. Password must contain at least one special symbol");
        }
        if (((id == null) || (passwordCheck != null)) && (!password.equals(passwordCheck))) {
            throw new IllegalArgumentException("The password and confirm password fields do not match.");
        }
        return true;
    }

}
