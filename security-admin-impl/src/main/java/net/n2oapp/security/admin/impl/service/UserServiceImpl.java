package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.specification.UserSpecifications;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
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
    @Autowired
    private ModelMapper modelMapper;

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
    public User create(User user) {
        checkUsernameUniq(user.getId(), user.getUsername());
        checkUsername(user.getUsername());
        checkEmail(user.getEmail());
        checkPassword(user.getPassword(), user.getCheckPassword());
        return model(userRepository.save(entity(user)));
    }

    @Override
    public User update(User user) {
        checkUsernameUniq(user.getId(), user.getUsername());
        checkUsername(user.getUsername());
        checkEmail(user.getEmail());
        checkPassword(user.getPassword(), user.getCheckPassword());
        return model(userRepository.save(entity(user)));

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

    private UserEntity entity(User model) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity entity = modelMapper.map(model, UserEntity.class);
        if (model.getRoleIds() != null)
            entity.setRoleList(model.getRoleIds().stream().map(RoleEntity::new).collect(Collectors.toList()));
        return entity;
    }

    private User model(UserEntity entity) {
        if (entity == null) return null;
        User model = modelMapper.map(entity, User.class);
        if (entity.getRoleList() != null) {
            model.setRoleIds(entity.getRoleList().stream().map(RoleEntity::getId).collect(Collectors.toList()));
            model.setRoleNames(entity.getRoleList().stream().map(RoleEntity::getName).collect(Collectors.toList()));
        }
        return model;
    }

    /**
     * Валидация на уникальность юзернейма пользователя
     */
    public boolean checkUsernameUniq(Integer id, String username) {
        UserEntity userEntity = userRepository.findOneByUsername(username);
        Boolean result = id != null ? userEntity == null : ((userEntity == null) || (userEntity.getId().equals(id)));
        if (result) {
            return true;
        } else
            throw new IllegalArgumentException(" Пользователь с таким именем существует");
    }

    /**
     * Валидация на  ввод юзернейма согласно формату
     */
    private boolean checkUsername(String username) {
        if (!validationUsername) return true;
        String regexp = "^[a-zA-Z][a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches())
            throw new IllegalArgumentException("Неверный логин");
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
            throw new IllegalArgumentException("Неверный email");
        return true;
    }

    /**
     * Валидация на  ввод пароля согласно формату
     */
    public boolean checkPassword(String password, String passwordCheck) {
        if (password.length() < validationPasswordLength)
            throw new IllegalArgumentException("Пароль должен быть минимум " + validationPasswordLength + " символов");
        if (!password.equals(passwordCheck))
            throw new IllegalArgumentException("Введённые пароли не совпадат");
        String regexp;
        Pattern pattern;
        Matcher matcher;
        if (validationPasswordUpperCaseLetters) {
            regexp = "^(?=.*[A-Z])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new IllegalArgumentException("Неверный формат пароля. Пароль должен содержать как минимум одну заглавную букву");
        }
        if (validationPasswordLowerCaseLetters) {
            regexp = "^(?=.*[a-z])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new IllegalArgumentException("Неверный формат пароля. Пароль должен содержать как минимум одну строчную букву");
        }
        if (validationPasswordNumbers) {
            regexp = "^(?=.*[0-9])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new IllegalArgumentException("Неверный формат пароля. Пароль должен содержать как минимум одну цифру");
        }
        if (validationPasswordSpecialSymbols) {
            regexp = "(?=.*[@#$%^&+=])(?=\\S+$).*$";
            pattern = Pattern.compile(regexp);
            matcher = pattern.matcher(password);
            if (!matcher.matches())
                throw new IllegalArgumentException("Неверный формат пароля. Пароль должен содержать как минимум один специальный символ");
        }
        return true;
    }

}
