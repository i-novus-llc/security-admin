package net.n2oapp.security.admin.loader.builder;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserLevel;

import java.util.List;

public class UserBuilder {
    public static User buildUser1() {
        User User = new User();
        User.setUsername("username1");
        User.setName("name1");
        User.setEmail("email1");
        User.setUserLevel(UserLevel.FEDERAL);
        Role role = new Role();
        role.setCode("101");
        User.setRoles(List.of(role));
        return User;
    }

    public static User buildUser2() {
        User User = new User();
        User.setUsername("username2");
        User.setName("name2");
        User.setEmail("email2");
        User.setUserLevel(UserLevel.FEDERAL);
        Role role = new Role();
        role.setCode("101");
        User.setRoles(List.of(role));
        return User;
    }

    public static User buildUser2Updated() {
        User User = new User();
        User.setUsername("username2-new");
        User.setName("name2-new");
        User.setEmail("email2-new");
        User.setUserLevel(UserLevel.REGIONAL);
        Role role = new Role();
        role.setCode("102");
        User.setRoles(List.of(role));
        return User;
    }

    public static User buildUser3() {
        User User = new User();
        User.setUsername("username3");
        User.setName("name3");
        User.setEmail("email3");
        User.setUserLevel(UserLevel.ORGANIZATION);
        Role role = new Role();
        role.setCode("102");
        User.setRoles(List.of(role));
        return User;
    }
}
