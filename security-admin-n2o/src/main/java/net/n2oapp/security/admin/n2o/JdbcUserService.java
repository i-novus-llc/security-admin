package net.n2oapp.security.admin.n2o;

import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.n2o.util.PasswordGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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

    private final static String INSERT_USER = "insert into sec.user(username, password, email, surname, name, patronymic, is_active) values(:username, :password, :email, :surname, :name, :patronymic, :isActive);";
    private final static String UPDATE_USER = "update sec.user set username = :username, email = :email, surname = :surname, name = :name, patronymic = :patronymic, is_active = :isActive where id=:id;";
    private final static String UPDATE_USER_ACTIVE = "update sec.user set is_active = :isActive where id=:id;";
    private final static String DELETE_USER = "delete from sec.user where id = :id;";
    private final static String INSERT_USER_ROLE = "insert into sec.user_role(user_id, role_id) values(:userId, :roleId);";
    private final static String DELETE_USER_ROLE = "delete from sec.user_role where user_id = :id;";

    private TransactionTemplate transactionTemplate;
    private NamedParameterJdbcTemplate jdbcTemplate;

    private boolean generate;
    private PasswordGenerator passwordGenerator;
    private PasswordEncoder passwordEncoder;
    private MailSender mailSender;
    private String mailApp;
    private String mailSubject;
    private Resource mailBody;


    public JdbcUserService(TransactionTemplate transactionTemplate, NamedParameterJdbcTemplate jdbcTemplate) {
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public User create(User model) {
        transactionTemplate.execute(transactionStatus -> {
            SqlParameterSource namedParameters =
                    new MapSqlParameterSource("username", model.getUsername())
                            .addValue("email", model.getEmail())
                            .addValue("surname", model.getSurname())
                            .addValue("name", model.getName())
                            .addValue("patronymic", model.getPatronymic())
                            .addValue("isActive", model.getIsActive());
            String password = null;
            String encodedPassword = null;
            if (generate) {
                password = passwordGenerator.generate();
                encodedPassword = passwordEncoder.encode(password);
            }
            ((MapSqlParameterSource) namedParameters).addValue("password", encodedPassword);
            model.setPassword(encodedPassword);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(INSERT_USER, namedParameters, keyHolder, new String[]{"id"});
            model.setId((Integer) keyHolder.getKey());
            saveRoles(model);
            if (generate) {
                try {
                    sendPassword(model, password);
                } catch (URISyntaxException | IOException e) {
                    throw new N2oException("failed send email", e);
                }
            }
            return model;
        });
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

    public User update(User model) {
        transactionTemplate.execute(transactionStatus -> {
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
        });
        return model;
    }

    public void delete(Integer id) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("id", id);
        jdbcTemplate.update(DELETE_USER, namedParameters);
    }

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
        data.put("patronymic", user.getPatronymic() == null ? "" : user.getPatronymic());
        data.put("password", password);
        data.put("email", user.getEmail());
        data.put("app", mailApp);
        String subjectTemplate = mailSubject;
        Resource bodyPathProperty = mailBody;
        String body;
        try (InputStream inputStream = bodyPathProperty.getInputStream()) {
            body = StrSubstitutor.replace(IOUtils.toString(inputStream, "UTF-8"), data);
        }
        String subject = StrSubstitutor.replace(subjectTemplate, data);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    public void setGenerate(boolean generate) {
        this.generate = generate;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public void setMailBody(Resource mailBody) {
        this.mailBody = mailBody;
    }

    public void setMailApp(String mailApp) {
        this.mailApp = mailApp;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }
}