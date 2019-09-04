package net.n2oapp.security.admin.rest.api.criteria;

import io.swagger.annotations.ApiParam;
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
        setExtUid(userDetails.getExtUid());
        setName(userDetails.getName());
        setSurname(userDetails.getSurname());
        setRoleNames(userDetails.getRoleNames());
    }

    @QueryParam("extUid")
    @Override
    @ApiParam(value = "Уникальный идентификатор")
    public void setExtUid(String extUid) {
        super.setExtUid(extUid);
    }

    @QueryParam("username")
    @Override
    @ApiParam(value = "Имя пользователя")
    public void setUsername(String username) {
        super.setUsername(username);
    }

    @QueryParam("name")
    @Override
    @ApiParam(value = "Имя")
    public void setName(String name) {
        super.setName(name);
    }

    @QueryParam("surname")
    @Override
    @ApiParam(value = "Фамилия")
    public void setSurname(String surname) {
        super.setSurname(surname);
    }

    @QueryParam("email")
    @Override
    @ApiParam(value = "Электронный адрес")
    public void setEmail(String email) {
        super.setEmail(email);
    }

    @QueryParam("rolenames")
    @Override
    @ApiParam(value = "Список имен ролей")
    public void setRoleNames(List<String> roleNames) {
        super.setRoleNames(roleNames);
    }
}
