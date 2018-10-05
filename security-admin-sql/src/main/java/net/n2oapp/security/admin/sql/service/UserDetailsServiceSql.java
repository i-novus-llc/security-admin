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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserDetailsServiceSql implements UserDetailsService {

    private final static String LOAD_USER_DETAILS = "sql/user/load_user_details.sql";
    private final static String FIND_ROLES_WITH_PERMISSIONS = "sql/user/find_roles_with_permissions.sql";

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
                u.setRoles(findAllRolesWithPermissions(u.getId()));
                return u;
            });
        } catch (EmptyResultDataAccessException ignored) {}

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
            p.setId((Integer) row.get("p_id"));
            p.setName((String) row.get("p_name"));
            p.setCode((String) row.get("p_code"));
            p.setParentId((Integer) row.get("p_parent_id"));
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
            if (p.getId() != null)
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
        user.setGuid(resultSet.getString("guid"));
        user.setSurname(resultSet.getString("surname"));
        user.setName(resultSet.getString("name"));
        user.setPatronymic(resultSet.getString("patronymic"));
        user.setFio(getFio(user.getSurname(), user.getName(), user.getPatronymic()));
        return user;
    }
    
    private String getFio(String surname, String name, String patronymic) {
        return (surname != null ? surname + " " : "")
                + (name != null ? name + " " : "")
                + (patronymic!= null ? patronymic + " " : "").trim();
    }
}
