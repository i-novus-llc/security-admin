package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.specification.UserSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public User create(User user) {
        return model(userRepository.save(entity(user)));
    }

    @Override
    public User update(User user) {
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
        UserEntity entity = modelMapper.map(model, UserEntity.class);
        if (model.getRoleIds() != null)
            entity.setRoleSet(model.getRoleIds().stream().map(RoleEntity::new).collect(Collectors.toSet()));
        return entity;
    }

    private User model(UserEntity entity) {
        if (entity == null) return null;
        User model = modelMapper.map(entity, User.class);
        if (entity.getRoleSet() != null) {
            model.setRoleIds(entity.getRoleSet().stream().map(RoleEntity::getId).collect(Collectors.toList()));
            model.setRoleNames(entity.getRoleSet().stream().map(RoleEntity::getName).collect(Collectors.toList()));
        }
        return model;
    }
}
