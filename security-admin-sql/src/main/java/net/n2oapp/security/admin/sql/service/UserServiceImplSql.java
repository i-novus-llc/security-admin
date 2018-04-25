package net.n2oapp.security.admin.sql.service;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.sql.util.MailServiceImpl;
import net.n2oapp.security.admin.sql.util.PasswordGenerator;
import net.n2oapp.security.admin.sql.util.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления пользователями для sql
 */
@Service
public class UserServiceImplSql implements UserService {

    private final static String INSERT_USER = "sql/user/insert_user.sql";
    private final static String UPDATE_USER = "sql/user/update_user.sql";
    private final static String DELETE_USER = "sql/user/delete_user.sql";
    private final static String INSERT_USER_ROLE = "sql/user/insert_user_role.sql";
    private final static String GET_USER_BY_ID = "sql/user/get_user_by_id.sql";
    private final static String GET_ROLE_BY_USER_ID = "sql/user/get_role_by_user_id.sql";
    private final static String UPDATE_USER_ACTIVE = "sql/user/update_user_active.sql";
    private final static String DELETE_USER_ROLE = "sql/user/delete_user_role.sql";
    private final static String SELECT_ALL = "sql/user/select_all.sql";
    private final static String SELECT_ALL_ROLES_BY_CRITERIA = "sql/user/select_all_roles_by_criteria.sql";
    private final static String SELECT_ALL_ROLES = "sql/user/select_all_roles.sql";

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailServiceImpl mailService;

    @Autowired
    private RoleService service;

    @Override
    public User create(UserForm user) {
        transactionTemplate.execute(transactionStatus -> {
            SqlParameterSource namedParameters =
                    new MapSqlParameterSource("username", user.getUsername())
                            .addValue("email", user.getEmail())
                            .addValue("surname", user.getSurname())
                            .addValue("name", user.getName())
                            .addValue("patronymic", user.getPatronymic())
                            .addValue("isActive", true)
                            .addValue("guid", user.getGuid());
            String password = null;
            String encodedPassword = null;
            Boolean generate = user.getPassword() == null;
            if (generate) {
                password = passwordGenerator.generate();
                encodedPassword = passwordEncoder.encode(password);
                ((MapSqlParameterSource) namedParameters).addValue("password", encodedPassword);
                user.setPassword(password);
            } else {
                ((MapSqlParameterSource) namedParameters).addValue("password", user.getPassword());
            }
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(SqlUtil.getResourceFileAsString(INSERT_USER), namedParameters, keyHolder, new String[]{"id"});
            user.setId((Integer) keyHolder.getKey());
            saveRoles(user);
            if (generate) {
                mailService.sendWelcomeMail(map(user));
            }
            return map(user);
        });
        return map(user);
    }


    private void saveRoles(UserForm user) {
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                SqlParameterSource params =
                        new MapSqlParameterSource("userId", user.getId())
                                .addValue("roleId", role);
                jdbcTemplate.update(SqlUtil.getResourceFileAsString(INSERT_USER_ROLE), params);
            });
        }
    }

    @Override
    public User update(UserForm user) {
        transactionTemplate.execute(transactionStatus -> {
            SqlParameterSource namedParameters =
                    new MapSqlParameterSource("id", user.getId())
                            .addValue("username", user.getUsername())
                            .addValue("password", user.getPassword())
                            .addValue("email", user.getEmail())
                            .addValue("surname", user.getSurname())
                            .addValue("name", user.getName())
                            .addValue("patronymic", user.getPatronymic())
                            .addValue("isActive", true)
                            .addValue("guid", user.getGuid());
            jdbcTemplate.update(SqlUtil.getResourceFileAsString(UPDATE_USER), namedParameters);
            jdbcTemplate.update(SqlUtil.getResourceFileAsString(DELETE_USER_ROLE), namedParameters);
            saveRoles(user);
            return map(user);
        });
        return map(user);
    }

    @Override
    public void delete(Integer id) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("id", id);
        jdbcTemplate.update(SqlUtil.getResourceFileAsString(DELETE_USER), namedParameters);
    }

    @Override
    public User getById(Integer id) {
        List<Map<String, Object>> all = jdbcTemplate.queryForList(SqlUtil.getResourceFileAsString(GET_USER_BY_ID), new MapSqlParameterSource("id", id));
        if (all.isEmpty()) {
            return null;
        } else {
            User user = mapQueryResult(all.get(0));
            try {
                user.setRoles(jdbcTemplate.queryForList(SqlUtil.getResourceFileAsString(GET_ROLE_BY_USER_ID), new MapSqlParameterSource("user_id", id), Integer.class)
                        .stream().map(service::getById).collect(Collectors.toList()));
            } catch (EmptyResultDataAccessException e) {
                user.setRoles(null);
                return user;
            }
            return user;
        }
    }

    @Override
    public Page<User> findAll(UserCriteria criteria) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("username", criteria.getUsername())
                        .addValue("isActive", criteria.getIsActive())
                        .addValue("fio", criteria.getFio())
                        .addValue("password", criteria.getPassword());
        List<User> users = jdbcTemplate.queryForList(SqlUtil.getResourceFileAsString(SELECT_ALL), namedParameters).stream().map(this::mapQueryResult).collect(Collectors.toList());
        users.stream().forEach(user -> user.setRoles(jdbcTemplate.queryForList(SqlUtil.getResourceFileAsString(SELECT_ALL_ROLES),
                new MapSqlParameterSource().addValue("id", user.getId()), Integer.class).stream().map(service::getById).collect(Collectors.toList())));
        if (criteria.getRoleIds() != null) {
            return new PageImpl<>(users.stream().filter(user -> !jdbcTemplate.queryForObject(SqlUtil.getResourceFileAsString(SELECT_ALL_ROLES_BY_CRITERIA),
                    new MapSqlParameterSource()
                            .addValue("id", user.getId())
                            .addValue("roleIds", criteria.getRoleIds()), Integer.class).equals(0))
                    .collect(Collectors.toList()));
        }
        return new PageImpl<>(users);
    }

    @Override
    public User changeActive(Integer id) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("id", id)
                        .addValue("isActive", !getById(id).getIsActive());
        jdbcTemplate.update(SqlUtil.getResourceFileAsString(UPDATE_USER_ACTIVE), namedParameters);
        return getById(id);
    }

    private User mapQueryResult(Map map) {
        User user = new User();
        user.setId((Integer) map.get("ID"));
        user.setGuid(map.get("GUID") == null ? null : map.get("GUID").toString());
        user.setUsername((String) map.get("USERNAME"));
        user.setName((String) map.get("NAME"));
        user.setSurname((String) map.get("SURNAME"));
        user.setPatronymic((String) map.get("PATRONYMIC"));
        user.setFio(getFio(user.getSurname(), user.getName(), user.getPatronymic()));
        user.setEmail((String) map.get("EMAIL"));
        user.setPassword((String) map.get("PASSWORD"));
        user.setIsActive((Boolean) map.get("IS_ACTIVE"));
        return user;
    }

    private User map(UserForm form) {
        User user = new User();
        user.setId(form.getId());
        user.setGuid(form.getGuid());
        user.setUsername(form.getUsername());
        user.setName(form.getName());
        user.setSurname(form.getSurname());
        user.setPatronymic(form.getPatronymic());
        user.setFio(getFio(user.getSurname(), user.getName(), user.getPatronymic()));
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());
        user.setIsActive(form.getIsActive());
        if(form.getRoles() != null) {
            user.setRoles(form.getRoles().stream().map(service::getById).collect(Collectors.toList()));
        }
        return user;
    }

    private String getFio(String surname, String name, String patronymic) {
        StringBuilder builder = new StringBuilder();
        if (surname != null) {
            builder.append(surname).append(" ");
        }
        if (name != null) {
            builder.append(name).append(" ");
        }
        if (patronymic != null) {
            builder.append(patronymic);
        }
        return builder.toString();
    }


}
