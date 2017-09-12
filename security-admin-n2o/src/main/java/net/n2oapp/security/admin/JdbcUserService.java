package net.n2oapp.security.admin;

import net.n2oapp.security.admin.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * crud пользователей с использованием jdbc
 */
@Service
public class JdbcUserService {

    private final static String INSERT_USER = "insert into sec.user(username, email, surname, name, patronymic, is_active) values(:username, :email, :surname, :name, :patronymic, :isActive);";
    private final static String UPDATE_USER = "update sec.user set username = :username, email = :email, surname = :surname, name = :name, patronymic = :patronymic, is_active = :isActive where id=:id;";
    private final static String DELETE_USER = "delete from sec.user where id = :id;";
    private final static String INSERT_USER_ROLE = "insert into sec.user_role(user_id, role_id) values(:userId, :roleId);";
    private final static String DELETE_USER_ROLE = "delete from sec.user_role where user_id = :id;";

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional
    public User create(User model) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("username", model.getUsername())
                        .addValue("email", model.getEmail())
                        .addValue("surname", model.getSurname())
                        .addValue("name", model.getName())
                        .addValue("patronymic", model.getPatronymic())
                        .addValue("isActive", model.getIsActive());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(INSERT_USER, namedParameters, keyHolder, new String[]{"id"});
        model.setId((Integer) keyHolder.getKey());
        saveRoles(model);
        return model;
    }

    private void saveRoles(User model) {
        if (model.getRoleIds() != null) {
            model.getRoleIds().forEach(roleId -> {
                SqlParameterSource params =
                        new MapSqlParameterSource("userId", model.getId())
                                .addValue("roleId", roleId);
                jdbcTemplate.update(INSERT_USER_ROLE, params);
            });
        }
    }

    @Transactional
    public User update(User model) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("id", model.getId())
                        .addValue("email", model.getEmail())
                        .addValue("username", model.getUsername())
                        .addValue("surname", model.getSurname())
                        .addValue("name", model.getName())
                        .addValue("patronymic", model.getPatronymic())
                        .addValue("isActive", model.getIsActive());
        jdbcTemplate.update(UPDATE_USER, namedParameters);
        jdbcTemplate.update(DELETE_USER_ROLE, namedParameters);
        saveRoles(model);
        return model;
    }

    @Transactional
    public void delete(Integer id) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("id", id);
        jdbcTemplate.update(DELETE_USER, namedParameters);
    }
}
