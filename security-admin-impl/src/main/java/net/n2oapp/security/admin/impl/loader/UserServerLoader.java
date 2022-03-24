package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.platform.loader.server.ServerLoaderSettings;
import net.n2oapp.security.admin.api.model.Account;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.impl.entity.*;
import net.n2oapp.security.admin.impl.repository.AccountRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class UserServerLoader extends ServerLoaderSettings<User> implements ServerLoader<User> {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public void load(List<User> data, String subject) {
        List<AccountEntity> oldAccounts = new ArrayList<>();
        List<AccountEntity> freshAccounts = new ArrayList<>();
        List<UserEntity> freshUsers = map(oldAccounts, freshAccounts, data);
        if (isCreateRequired()) {
            userRepository.saveAll(freshUsers);
            accountRepository.saveAll(freshAccounts);
        }
        if (isDeleteRequired())
            accountRepository.deleteInBatch(oldAccounts);
    }

    private List<UserEntity> map(List<AccountEntity> oldAccounts, List<AccountEntity> freshAccounts, List<User> uploadedUsers) {
        List<UserEntity> freshUsers = new ArrayList<>();
        List<UserEntity> oldUsers = userRepository.findByUsernameIn(uploadedUsers.stream().map(User::getUsername).collect(Collectors.toList()));
        for (User model : uploadedUsers) {
            UserEntity oldUser = oldUsers.stream().filter(userEntity -> userEntity.getUsername().equals(model.getUsername())).findFirst().orElse(null);
            UserEntity userEntity = new UserEntity();
            if (oldUser != null) {
                userEntity = oldUser;
                if (oldUser.getAccounts() != null)
                    oldAccounts.addAll(oldUser.getAccounts());
            }
            userEntity.setUsername(model.getUsername());
            userEntity.setName(model.getName());
            userEntity.setSurname(model.getSurname());
            userEntity.setPatronymic(model.getPatronymic());
            userEntity.setIsActive(model.getIsActive());
            userEntity.setEmail(model.getEmail());
            userEntity.setSnils(model.getSnils());
            if (model.getAccounts() != null) {
                for (Account account : model.getAccounts()) {
                    AccountEntity accountEntity = new AccountEntity();
                    accountEntity.setUser(userEntity);
                    accountEntity.setName(account.getName());
                    accountEntity.setUserLevel(account.getUserLevel());
                    accountEntity.setDepartment(nonNull(account.getDepartment()) ? new DepartmentEntity(account.getDepartment().getId()) : null);
                    accountEntity.setOrganization(nonNull(account.getOrganization()) ? new OrganizationEntity(account.getOrganization().getId()) : null);
                    accountEntity.setRegion(nonNull(account.getRegion()) ? new RegionEntity(account.getRegion().getId()) : null);
                    if (nonNull(account.getRoles()))
                        accountEntity.setRoleList(account.getRoles().stream().map(role -> roleRepository.findOneByCode(role.getCode())).collect(Collectors.toList()));
                    freshAccounts.add(accountEntity);
                }
            } else {
                userEntity.setAccounts(null);
            }
            freshUsers.add(userEntity);
        }
        return freshUsers;
    }

    @Override
    public String getTarget() {
        return "users";
    }

    @Override
    public Class<User> getDataType() {
        return User.class;
    }
}
