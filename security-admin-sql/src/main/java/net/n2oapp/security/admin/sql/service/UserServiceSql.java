package net.n2oapp.security.admin.sql.service;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.service.MailService;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.commons.util.MailServiceImpl;
import net.n2oapp.security.admin.commons.util.PasswordGenerator;
import net.n2oapp.security.admin.commons.util.UserValidations;
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

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления пользователями для sql
 */
@Service
public class UserServiceSql implements UserService {

    private final static String INSERT_USER = "sql/user/insert_user.sql";
    private final static String UPDATE_USER = "sql/user/update_user.sql";
    private final static String UPDATE_USER_WITHOUT_PASS = "sql/user/update_user_without_password.sql";
    private final static String DELETE_USER = "sql/user/delete_user.sql";
    private final static String INSERT_USER_ROLE = "sql/user/insert_user_role.sql";
    private final static String GET_USER_BY_USERNAME = "sql/user/get_user_by_username.sql";
    private final static String GET_USER_BY_ID = "sql/user/get_user_by_id.sql";
    private final static String UPDATE_USER_ACTIVE = "sql/user/update_user_active.sql";
    private final static String DELETE_USER_ROLE = "sql/user/delete_user_role.sql";
    private final static String FIND_ALL = "sql/user/find_all.sql";
    private final static String FIND_ALL_WITHOUT_ROLE_CONDITION = "sql/user/find_all_without_role_condition.sql";
    private final static String FIND_ALL_COUNT = "sql/user/find_all_count.sql";
    private final static String FIND_ALL_COUNT_WITHOUT_ROLE_CONDITION = "sql/user/find_all_count_without_role_condition.sql";
    private final static String CHECK_UNIQUE_USERNAME = "sql/user/check_unique_username.sql";

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private RoleService service;

    @Autowired
    private UserValidations userValidations;

    @Override
    public User create(UserForm user) {
        userValidations.checkUsernameUniq(user.getId(), getByUsername(user.getUsername()));
        userValidations.checkUsername(user.getUsername());
        userValidations.checkEmail(user.getEmail());
        String password = user.getPassword();
        if (password != null) {
            userValidations.checkPassword(password, user.getPasswordCheck(), user.getId());
        }
        if (password == null) {
            password = passwordGenerator.generate();
            user.setPassword(password);
        }
        String passwordHash = passwordEncoder.encode(password);
        transactionTemplate.execute(transactionStatus -> {
            SqlParameterSource namedParameters =
                    new MapSqlParameterSource("username", user.getUsername())
                            .addValue("email", user.getEmail())
                            .addValue("surname", user.getSurname())
                            .addValue("name", user.getName())
                            .addValue("patronymic", user.getPatronymic())
                            .addValue("isActive", true)
                            .addValue("guid", user.getGuid());
            ((MapSqlParameterSource) namedParameters).addValue("password", passwordHash);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(SqlUtil.getResourceFileAsString(INSERT_USER), namedParameters, keyHolder, new String[]{"id"});
            user.setId((Integer) keyHolder.getKey());
            saveRoles(user);
            mailService.sendWelcomeMail(user);
            return model(user);
        });
        return model(user);
    }


    @Override
    public User update(UserForm user) {
        userValidations.checkUsernameUniq(user.getId(), getByUsername(user.getUsername()));
        userValidations.checkUsername(user.getUsername());
        userValidations.checkEmail(user.getEmail());
        if (user.getNewPassword() != null) {
            userValidations.checkPassword(user.getNewPassword(), user.getPasswordCheck(), user.getId());
        }
        transactionTemplate.execute(transactionStatus -> {
            MapSqlParameterSource namedParameters =
                    new MapSqlParameterSource("id", user.getId())
                            .addValue("username", user.getUsername())
                            .addValue("email", user.getEmail())
                            .addValue("surname", user.getSurname())
                            .addValue("name", user.getName())
                            .addValue("patronymic", user.getPatronymic())
                            .addValue("isActive", user.getIsActive())
                            .addValue("guid", user.getGuid());
            if (user.getNewPassword() == null) {
                jdbcTemplate.update(SqlUtil.getResourceFileAsString(UPDATE_USER_WITHOUT_PASS), namedParameters);
            } else {
                namedParameters.addValue("password", passwordEncoder.encode(user.getNewPassword()));
                jdbcTemplate.update(SqlUtil.getResourceFileAsString(UPDATE_USER), namedParameters);
            }
            jdbcTemplate.update(SqlUtil.getResourceFileAsString(DELETE_USER_ROLE), namedParameters);
            saveRoles(user);
            return model(user);
        });
        return model(user);
    }

    @Override
    public void delete(Integer id) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("id", id);
        jdbcTemplate.update(SqlUtil.getResourceFileAsString(DELETE_USER), namedParameters);
    }

    @Override
    public User getById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(SqlUtil.getResourceFileAsString(GET_USER_BY_ID), new MapSqlParameterSource("id", id), (resultSet, i) -> {
                return model(resultSet);
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Page<User> findAll(UserCriteria criteria) {
        String sorting = criteria.getOrders() == null || criteria.getOrders().size() == 0
                ? "id" : criteria.getOrders().get(0).getProperty();
        String direction = criteria.getOrders() == null || criteria.getOrders().size() == 0
                ? "ASC" : criteria.getOrders().get(0).getDirection().name();
        MapSqlParameterSource namedParameters =
                new MapSqlParameterSource("username", criteria.getUsername())
                        .addValue("isActive", criteria.getIsActive())
                        .addValue("fio", criteria.getFio())
                        .addValue("password", criteria.getPassword())
                        .addValue("limit", criteria.getPageSize())
                        .addValue("offset", criteria.getOffset())
                        .addValue("bank", UUID.fromString(criteria.getBank()))
                        .addValue("sorting", sorting)
                        .addValue("direction", direction);
        if (criteria.getRoleIds() == null || criteria.getRoleIds().size() == 0) {
            List<User> users = jdbcTemplate.query(SqlUtil.getResourceFileAsString(FIND_ALL_WITHOUT_ROLE_CONDITION), namedParameters, (resultSet, i) -> {
                return model(resultSet);
            });
            Integer count = jdbcTemplate.queryForObject(SqlUtil.getResourceFileAsString(FIND_ALL_COUNT_WITHOUT_ROLE_CONDITION), namedParameters, Integer.class);
            return new PageImpl<>(users, criteria, count);
        } else {
            namedParameters.addValue("roleIds", criteria.getRoleIds());
            List<User> users = jdbcTemplate.query(SqlUtil.getResourceFileAsString(FIND_ALL), namedParameters, (resultSet, i) -> {
                return model(resultSet);
            });
            Integer count = jdbcTemplate.queryForObject(SqlUtil.getResourceFileAsString(FIND_ALL_COUNT), namedParameters, Integer.class);
            return new PageImpl<>(users, criteria, count);
        }
    }

    @Override
    public User changeActive(Integer id) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("id", id)
                        .addValue("isActive", !getById(id).getIsActive());
        jdbcTemplate.update(SqlUtil.getResourceFileAsString(UPDATE_USER_ACTIVE), namedParameters);
        return getById(id);
    }

    @Override
    public Boolean checkUniqueUsername(String username) {
        return jdbcTemplate.queryForObject(SqlUtil.getResourceFileAsString(CHECK_UNIQUE_USERNAME),
                new MapSqlParameterSource("username", username),
                Integer.class) == 0;
    }

    private User getByUsername(String username) {
        List<User> result = jdbcTemplate.query(SqlUtil.getResourceFileAsString(GET_USER_BY_USERNAME),
                new MapSqlParameterSource("username", username), (resultSet, i) -> model(resultSet));
        return result.isEmpty() ? null : result.get(0);
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


    private User model(UserForm form) {
        if (form == null) return null;
        User user = new User();
        user.setId(form.getId());
        user.setGuid(form.getGuid());
        user.setUsername(form.getUsername());
        user.setName(form.getName());
        user.setSurname(form.getSurname());
        user.setPatronymic(form.getPatronymic());
        user.setFio(getFio(user.getSurname(), user.getName(), user.getPatronymic()));
        user.setEmail(form.getEmail());
        user.setPasswordHash(form.getPassword());
        user.setIsActive(form.getIsActive());
        if (form.getRoles() != null) {
            user.setRoles(form.getRoles().stream().map(service::getById).collect(Collectors.toList()));
        }
        return user;
    }

    private User model(ResultSet resultSet) throws SQLException {
        if (resultSet == null) return null;
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setGuid(resultSet.getString("guid"));
        user.setUsername(resultSet.getString("username"));
        user.setName(resultSet.getString("name"));
        user.setSurname(resultSet.getString("surname"));
        user.setPatronymic(resultSet.getString("patronymic"));
        user.setFio(getFio(user.getSurname(), user.getName(), user.getPatronymic()));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password"));
        user.setIsActive(resultSet.getBoolean("is_active"));
        user.setRoles(new ArrayList<>());
        if (resultSet.getObject("ids") != null && resultSet.getObject("names") != null) {
            Array a = resultSet.getArray("ids");
            Object idsObject = a.getArray();
            Integer[] ids;
            String[] names;
            // эта проверка нужна для поддержки различных реализаций для h2 и postrgesql
            // они возвращают разные объекты когда в запросе используется функция array_agg
            if (idsObject instanceof Integer[]) {
                ids = (Integer[]) a.getArray();
                a = resultSet.getArray("names");
                names = (String[]) a.getArray();
            } else {
                Object[] idsObj = (Object[]) resultSet.getObject("ids");
                Object[] namesObj = (Object[]) resultSet.getObject("names");
                ids = new Integer[idsObj.length];
                names = new String[idsObj.length];
                for (int i = 0; i < idsObj.length; i++) {
                    ids[i] = (Integer) ((Object[]) idsObj[i])[0];
                    names[i] = (String) ((Object[]) namesObj[i])[0];
                }
            }
            if (ids != null && names != null) {
                for (int i = 0; i < ids.length && i < names.length; i++) {
                    Role role = new Role();
                    role.setId(ids[i]);
                    role.setName(names[i]);
                    user.getRoles().add(role);
                }
            }
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
