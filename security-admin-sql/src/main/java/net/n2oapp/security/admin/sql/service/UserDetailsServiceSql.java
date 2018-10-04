package net.n2oapp.security.admin.sql.service;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.sql.util.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserDetailsServiceSql implements UserDetailsService {

    private final static String LOAD_USER_DETAILS = "sql/user/load_user_details.sql";
    private final static String GET_ROLES = "sql/user/get_roles.sql";
    private final static String GET_PERMISSIONS = "sql/user/get_permissions.sql";

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public User loadUserDetails(UserDetailsToken userDetails) {
        MapSqlParameterSource namedParameters =
                new MapSqlParameterSource("guid", userDetails.getGuid())
                        .addValue("surname", userDetails.getSurname())
                        .addValue("name", userDetails.getName())
                        .addValue("username", userDetails.getUsername())
                        .addValue("email", userDetails.getEmail());
        User user = null;
        try {
            user = jdbcTemplate.queryForObject(SqlUtil.getResourceFileAsString(LOAD_USER_DETAILS), namedParameters,
                    (resultSet, i) -> {
                User u = userModel(resultSet);
                u.setRoles(getRoles(u.getId()));
                return u;
            });
        } catch (EmptyResultDataAccessException ignored) {}

        return user;
    }

    private List<Role> getRoles(Integer userId) {
        MapSqlParameterSource namedParameters =
                new MapSqlParameterSource("userId", userId);
        List<Role> roles = null;
        try {
            roles = jdbcTemplate.query(SqlUtil.getResourceFileAsString(GET_ROLES), namedParameters, (resultSet, i) -> {
                Role r = roleModel(resultSet);
                r.setPermissions(getPermissions(r.getId()));
                return r;
            });
        } catch (EmptyResultDataAccessException ignored) {}

        return roles;
    }

    private List<Permission> getPermissions(Integer roleId) {
        MapSqlParameterSource namedParameters =
                new MapSqlParameterSource("roleId", roleId);
        List<Permission> permissions = null;
        try {
            permissions = jdbcTemplate.query(SqlUtil.getResourceFileAsString(GET_PERMISSIONS), namedParameters, (resultSet, i) -> {
                Permission r = permissionModel(resultSet);
                return r;
            });
        } catch (EmptyResultDataAccessException ignored) {}

        return permissions;
    }

    private User userModel(ResultSet resultSet) throws SQLException {
        if (resultSet == null) return null;
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setIsActive(resultSet.getBoolean("is_active"));
        user.setGuid(resultSet.getString("guid"));
        user.setSurname(resultSet.getString("surname"));
        user.setName(resultSet.getString("name"));
        user.setPatronymic(resultSet.getString("patronymic"));
        user.setFio(getFio(user));
        return user;
    }

    private Role roleModel(ResultSet resultSet) throws SQLException {
        if (resultSet == null) return null;
        Role role = new Role();
        role.setId(resultSet.getInt("id"));
        role.setName(resultSet.getString("name"));
        role.setCode(resultSet.getString("code"));
        role.setDescription(resultSet.getString("description"));
        return role;
    }

    private Permission permissionModel(ResultSet resultSet) throws SQLException {
        if (resultSet == null) return null;
        Permission permission = new Permission();
        permission.setId(resultSet.getInt("id"));
        permission.setName(resultSet.getString("name"));
        permission.setCode(resultSet.getString("code"));
        Integer parentId = resultSet.getInt("parent_id");
        permission.setParentId(
                resultSet.wasNull() ? null : parentId
        );
        if (resultSet.wasNull()) permission.setParentId(null);
        return permission;
    }

    private String getFio(User user) {
        return (user.getSurname() != null ? user.getSurname() : "")
                + (user.getName() != null ? " " + user.getName() : "")
                + (user.getPatronymic() != null ? " " + user.getPatronymic() : "");
    }
}
