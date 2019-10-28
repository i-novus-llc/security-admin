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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Реализация сервиса предоставления информации о пользователе (ролей/пермишенов)
 */
@Service
public class UserDetailsServiceSql implements UserDetailsService {

    private final static String LOAD_USER_DETAILS = "sql/user/load_user_details.sql";
    private final static String FIND_ROLES_WITH_PERMISSIONS = "sql/user/find_roles_with_permissions.sql";
    private final static String INSERT_USER = "sql/user/insert_user.sql";
    private final static String GET_ROLES_BY_NAMES = "sql/user/get_roles_by_names.sql";
    private final static String INSERT_USER_ROLE = "sql/user/insert_user_role.sql";

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public User loadUserDetails(UserDetailsToken token) {
        User user = getUser(token);
        return user != null ? user : createUser(token);
    }

    private User createUser(UserDetailsToken token) {
        User u = new User();
        u.setName(token.getName());
        u.setUsername(token.getUsername());
        u.setSurname(token.getSurname());
        u.setEmail(token.getEmail());

        MapSqlParameterSource namedParameters =
                new MapSqlParameterSource("username", u.getUsername())
                        .addValue("name", u.getSurname())
                        .addValue("surname", u.getName())
                        .addValue("email", u.getEmail())
                        .addValue("isActive", true)
                        .addValue("password", null)
                        .addValue("patronymic", null);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(SqlUtil.getResourceFileAsString(INSERT_USER), namedParameters, keyHolder, new String[]{
                "id"});
        u.setId((Integer) keyHolder.getKey());
        saveRoles(token, u);
        return u;
    }

    private void saveRoles(UserDetailsToken token, User user) {
        if (token.getRoleNames() != null) {
            MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("names", token.getRoleNames());
            List<Map<String, Object>> rows = jdbcTemplate
                    .queryForList(SqlUtil.getResourceFileAsString(GET_ROLES_BY_NAMES), namedParameters);
            for (Map<String, Object> row : rows) {
                Integer roleId = (Integer) row.get("id");
                namedParameters = new MapSqlParameterSource()
                        .addValue("userId", user.getId())
                        .addValue("roleId", roleId);
                jdbcTemplate.update(SqlUtil.getResourceFileAsString(INSERT_USER_ROLE), namedParameters);
            }
            user.setRoles(findAllRolesWithPermissions(user.getId()));
        }
    }

    private User getUser(UserDetailsToken token) {
        MapSqlParameterSource parameters =
                new MapSqlParameterSource()
                        .addValue("surname", token.getSurname())
                        .addValue("name", token.getName())
                        .addValue("username", token.getUsername())
                        .addValue("email", token.getEmail());
        User user = null;
        try {
            user = jdbcTemplate.queryForObject(SqlUtil.getResourceFileAsString(LOAD_USER_DETAILS), parameters,
                    (resultSet, i) -> {
                        User u = userModel(resultSet);
                        u.setRoles(findAllRolesWithPermissions(u.getId()));
                        return u;
                    });
        } catch (EmptyResultDataAccessException ignored) {
        }

        return user;
    }

    private List<Role> findAllRolesWithPermissions(Integer userId) {
        List<Role> roles = new ArrayList<>();
        MapSqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);

        List<Map<String, Object>> rows = jdbcTemplate
                .queryForList(SqlUtil.getResourceFileAsString(FIND_ROLES_WITH_PERMISSIONS), namedParameters);
        Integer currentRoleId = null;
        Role role = null;
        for (Map<String, Object> row : rows) {
            Permission p = new Permission();
            p.setName((String) row.get("p_name"));
            p.setCode((String) row.get("p_code"));
            p.setParentCode((String) row.get("p_parent_code"));
            Integer roleId = (Integer) row.get("r_id");
            if (!roleId.equals(currentRoleId)) {
                currentRoleId = roleId;
                role = new Role();
                role.setPermissions(new ArrayList<>());
                role.setId(roleId);
                role.setName((String) row.get("r_name"));
                role.setCode((String) row.get("r_code"));
                role.setDescription((String) row.get("r_description"));
                roles.add(role);
            }
            if (p.getCode() != null)
                role.getPermissions().add(p);
        }
        return roles;
    }

    private User userModel(ResultSet resultSet) throws SQLException {
        if (resultSet == null) return null;
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setIsActive(resultSet.getBoolean("is_active"));
        user.setSurname(resultSet.getString("surname"));
        user.setName(resultSet.getString("name"));
        user.setPatronymic(resultSet.getString("patronymic"));
        user.setFio(getFio(user.getSurname(), user.getName(), user.getPatronymic()));
        return user;
    }

    private String getFio(String surname, String name, String patronymic) {
        return (surname != null ? surname + " " : "")
                + (name != null ? name + " " : "")
                + (patronymic != null ? patronymic + " " : "").trim();
    }
}
