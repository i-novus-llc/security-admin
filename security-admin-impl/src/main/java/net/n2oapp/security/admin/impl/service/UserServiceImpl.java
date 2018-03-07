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

import java.util.function.Function;
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

    private final Function<UserEntity, User> funcEntityToModel = this::convertToUser;

    @Override
    public User create(User user) {
        return convertToUser(userRepository.save(convertToUserEntity(user)));
    }

    @Override
    public User  update(User user) {
        return convertToUser(userRepository.save(convertToUserEntity(user)));
    }

    @Override
    public void delete(Integer id) {
        userRepository.delete(id);

    }

    @Override
    public User getById(Integer id) {
        UserEntity userEntity=userRepository.findOne(id);
        return convertToUser(userEntity);
    }

    @Override
    public Page<User> findAll(UserCriteria criteria) {
        final Specification<UserEntity> specification = new UserSpecifications(criteria);
        final Page<UserEntity> all = userRepository.findAll(specification, criteria);
        return all.map(this::convertToUser);
    }

    private UserEntity convertToUserEntity(User user){
        UserEntity userEntity = modelMapper.map(user,UserEntity.class);
        userEntity.setRoleSet(user.getRoleIds().stream().map(RoleEntity::new).collect(Collectors.toSet()));
        return userEntity;
    }

    private User convertToUser (UserEntity userEntity){
        if (userEntity == null) return null;
        User user = modelMapper.map(userEntity, User.class);
        user.setRoleIds(userEntity.getRoleSet().stream().map(RoleEntity::getId).collect(Collectors.toList()));
        user.setRoleNames(userEntity.getRoleSet().stream().map(RoleEntity::getName).collect(Collectors.toList()));
        return user;

    }
}
