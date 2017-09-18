package net.n2oapp.security.admin;

import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.properties.StaticProperties;
import net.n2oapp.security.admin.model.User;
import net.n2oapp.security.admin.util.ApplicationMailer;
import net.n2oapp.security.admin.util.PasswordGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * crud пользователей с использованием jdbc
 */
@Service
public class JdbcUserService {

    private final static String INSERT_USER = "insert into sec.user(username, email, surname, name, patronymic, is_active) values(:username, :email, :surname, :name, :patronymic, :isActive);";
    private final static String UPDATE_USER = "update sec.user set username = :username, email = :email, surname = :surname, name = :name, patronymic = :patronymic, is_active = :isActive where id=:id;";
    private final static String UPDATE_USER_ACTIVE = "update sec.user set is_active = :isActive where id=:id;";
    private final static String DELETE_USER = "delete from sec.user where id = :id;";
    private final static String INSERT_USER_ROLE = "insert into sec.user_role(user_id, role_id) values(:userId, :roleId);";
    private final static String DELETE_USER_ROLE = "delete from sec.user_role where user_id = :id;";

    private PasswordGenerator passwordGenerator;

    @Autowired
    private ApplicationMailer applicationMailer;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcUserService(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Transactional
    public User create(User model) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("username", model.getUsername())
                        .addValue("email", model.getEmail())
                        .addValue("surname", model.getSurname())
                        .addValue("name", model.getName())
                        .addValue("patronymic", model.getPatronymic())
                        .addValue("isActive", model.getIsActive());
        boolean passEnabled = StaticProperties.getBoolean("sec.password.enabled");
        String password = "";
        if (passEnabled) {
            password = passwordGenerator.generate();
            String encodedPassword = passwordGenerator.encode(password);
            ((MapSqlParameterSource)namedParameters).addValue("password", encodedPassword);
            model.setPassword(encodedPassword);
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(INSERT_USER, namedParameters, keyHolder, new String[]{"id"});
        model.setId((Integer) keyHolder.getKey());
        saveRoles(model);
        if (passEnabled) {
            try {
                sendPassword(model, password);
            } catch (URISyntaxException | IOException e) {
                throw new N2oException("failed send email", e);
            }
        }
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

    @Transactional
    public void changeUserActive(Integer id, Boolean isActive) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("id", id)
                .addValue("isActive", isActive);
        jdbcTemplate.update(UPDATE_USER_ACTIVE, namedParameters);
    }

    private void sendPassword(User user, String password) throws URISyntaxException, IOException {
        Map<String, String> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("surname", user.getSurname());
        data.put("name", user.getName());
        data.put("patronymic", user.getPatronymic()==null? "":user.getPatronymic());
        data.put("password", password);
        data.put("email", user.getEmail());
        data.put("system.url", StaticProperties.getProperty("n2o.ui.url"));
        String subjectTemplate = StaticProperties.getProperty("sec.password.mail.subject");
        String bodyPathProperty = StaticProperties.getProperty("sec.password.mail.body.path");
        StringBuilder body = new StringBuilder();
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(bodyPathProperty)){
            body.append(StrSubstitutor.replace(IOUtils.toString(inputStream, "UTF-8"), data));
        }
        String subject = StrSubstitutor.replace(subjectTemplate, data);
        applicationMailer.sendMail(user.getEmail(), subject, body.toString());
    }
}
