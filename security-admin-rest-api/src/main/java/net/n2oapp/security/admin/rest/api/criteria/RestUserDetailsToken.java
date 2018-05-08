package net.n2oapp.security.admin.rest.api.criteria;

import net.n2oapp.security.admin.api.model.UserDetailsToken;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Модель детальной информации о пользователе
 */
public class RestUserDetailsToken extends UserDetailsToken {

    public RestUserDetailsToken() {
    }

    public RestUserDetailsToken(UserDetailsToken userDetails) {
        setEmail(userDetails.getEmail());
        setUsername(userDetails.getUsername());
        setGuid(userDetails.getGuid());
        setName(userDetails.getName());
        setSurname(userDetails.getSurname());
        setRoleNames(userDetails.getRoleNames());
    }

    @QueryParam("giud")
    @Override
    public void setGuid(String guid) {
        super.setGuid(guid);
    }

    @QueryParam("username")
    @Override
    public void setUsername(String username) {
        super.setUsername(username);
    }

    @QueryParam("name")
    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @QueryParam("surname")
    @Override
    public void setSurname(String surname) {
        super.setSurname(surname);
    }

    @QueryParam("email")
    @Override
    public void setEmail(String email) {
        super.setEmail(email);
    }

    @QueryParam("rolenames")
    @Override
    public void setRoleNames(List<String> roleNames) {
        super.setRoleNames(roleNames);
    }
}
