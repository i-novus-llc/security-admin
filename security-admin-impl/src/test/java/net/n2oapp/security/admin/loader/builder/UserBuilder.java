package net.n2oapp.security.admin.loader.builder;

import net.n2oapp.security.admin.api.model.Account;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserLevel;

import java.util.List;

public class UserBuilder {
    public static User buildUser1() {
        User user = new User();
        user.setUsername("username1");
        user.setName("name1");
        user.setEmail("email1");
        Account account = new Account();
        account.setName("account1");
        account.setUserLevel(UserLevel.FEDERAL);
        Role role = new Role();
        role.setCode("101");
        account.setRoles(List.of(role));
        user.setAccounts(List.of(account));
        return user;
    }

    public static User buildUser2() {
        User user = new User();
        user.setUsername("username2");
        user.setName("name2");
        user.setEmail("email2");
        Account account = new Account();
        account.setName("account2");
        account.setUserLevel(UserLevel.FEDERAL);
        Role role = new Role();
        role.setCode("101");
        account.setRoles(List.of(role));
        user.setAccounts(List.of(account));
        return user;
    }

    public static User buildUser2Updated() {
        User user = new User();
        user.setUsername("username2-new");
        user.setName("name2-new");
        user.setEmail("email2-new");
        Account account = new Account();
        account.setName("account2-new");
        account.setUserLevel(UserLevel.REGIONAL);
        Role role = new Role();
        role.setCode("102");
        account.setRoles(List.of(role));
        user.setAccounts(List.of(account));
        return user;
    }

    public static User buildUser3() {
        User user = new User();
        user.setUsername("username3");
        user.setName("name3");
        user.setEmail("email3");
        return user;
    }
}
