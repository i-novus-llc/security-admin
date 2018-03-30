package net.n2oapp.security.admin.n2o;

import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.properties.StaticProperties;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.n2o.util.PasswordGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
    private final static String UPDATE_PASSWORD = "update sec.user set password = :password where id=:id;";

    private TransactionTemplate transactionTemplate;
    private NamedParameterJdbcTemplate jdbcTemplate;

    private boolean generate;
    private PasswordGenerator passwordGenerator;
    private PasswordEncoder passwordEncoder;
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
        if (!model.getRoles().isEmpty()) {
            model.getRoles().forEach(role -> {
                SqlParameterSource params =
                        new MapSqlParameterSource("userId", model.getId())
                                .addValue("roleId", role.getId());
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

    public void updatePassword(User model) {
        transactionTemplate.execute(transactionStatus -> {
            SqlParameterSource namedParameters =
                    new MapSqlParameterSource("id", model.getId())
                            .addValue("password", passwordEncoder.encode(model.getPassword()));
            jdbcTemplate.update(UPDATE_PASSWORD, namedParameters);
            return model;
        });
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
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            Session session = (Session) envCtx.lookup(StaticProperties.getProperty("sec.password.mail.server"));
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(session.getProperty("mail.smtp.user")));
            InternetAddress to[] = new InternetAddress[1];
            to[0] = new InternetAddress(user.getEmail());
            message.setRecipients(Message.RecipientType.TO, to);
            message.setSubject(subject);
            message.setContent(body, "text/plain; charset=UTF-8");
            Transport.send(message);
        } catch (MessagingException | NamingException e) {
            throw new N2oException(e.getMessage(), e);
        }
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

}
