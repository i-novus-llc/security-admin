package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.Criteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.stream.Collectors;


/**
 * Created by otihonova on 31.10.2017.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Integer create(User user) {
        return userRepository.save(convertToUserEntity(user)).getId();
    }

    @Override
    public Integer update(User user) {
        return userRepository.save(convertToUserEntity(user)).getId();
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

  //  @Override
 //   public Page<User> findAll(Criteria criteria) {
        //criteria-> Spe
        //return userRepository.findAll(criteria).map(userEntity, User.class);

   // }

    private UserEntity convertToUserEntity(User user){
        /*UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setName(user.getName());
        userEntity.setSurname(user.getSurname());
        userEntity.setPatronymic(user.getPatronymic());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setIsActive(user.getIsActive());
        userEntity.setRoleSet(user.getRoleIds().stream().map(RoleEntity::new).collect(Collectors.toSet()));*/
        UserEntity userEntity =modelMapper.map(user,UserEntity.class); //TODO:возникнет проблема с ролями
        userEntity.setRoleSet(user.getRoleIds().stream().map(RoleEntity::new).collect(Collectors.toSet()));
        return userEntity;
    }

    private User convertToUser (UserEntity userEntity){
        User user = modelMapper.map(userEntity, User.class); //TODO:возникнет проблема с ролями
        user.setRoleIds(userEntity.getRoleSet().stream().map(RoleEntity::getId).collect(Collectors.toSet()));
        return user;

    }
}
