package net.n2oapp.security.admin.loader.builder;

import net.n2oapp.security.admin.api.model.UserForm;

import java.util.List;

public class UserFormBuilder {
    public static UserForm buildUserForm1(Integer id) {
        UserForm userForm = new UserForm();
        userForm.setId(id);
        userForm.setUsername("username1");
        userForm.setName("name1");
        userForm.setEmail("email1");
        userForm.setUserLevel("FEDERAL");
        userForm.setRoles(List.of(101));
        return userForm;
    }

    public static UserForm buildUserForm2(Integer id) {
        UserForm userForm = new UserForm();
        userForm.setId(id);
        userForm.setUsername("username2");
        userForm.setName("name2");
        userForm.setEmail("email2");
        userForm.setUserLevel("FEDERAL");
        userForm.setRoles(List.of(101));
        return userForm;
    }

    public static UserForm buildUserForm2Updated(Integer id) {
        UserForm userForm = new UserForm();
        userForm.setId(id);
        userForm.setUsername("username2-new");
        userForm.setName("name2-new");
        userForm.setEmail("email2-new");
        userForm.setUserLevel("REGIONAL");
        userForm.setRoles(List.of(102));
        return userForm;
    }

    public static UserForm buildUserForm3(Integer id) {
        UserForm userForm = new UserForm();
        userForm.setId(id);
        userForm.setUsername("username3");
        userForm.setName("name3");
        userForm.setEmail("email3");
        userForm.setUserLevel("ORGANIZATION");
        userForm.setRoles(List.of(102));
        return userForm;
    }
}
