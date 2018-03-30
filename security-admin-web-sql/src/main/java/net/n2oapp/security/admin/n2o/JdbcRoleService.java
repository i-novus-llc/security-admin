package net.n2oapp.security.admin.n2o;

import net.n2oapp.security.admin.api.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * crud ролей с использованием jdbc
 */
@Service
public class JdbcRoleService {

    private final static String INSERT_ROLE = "insert into sec.role(name, code, description) values(:name, :code, :description);";
    private final static String UPDATE_ROLE = "update sec.role set name = :name, code = :code, description = :description where id = :id;";
    private final static String DELETE_ROLE = "delete from sec.role where id = :id;";
    private final static String INSERT_ROLE_PERMISSION = "insert into sec.role_permission(role_id, permission_id) values(:roleId, :permissionId);";
    private final static String DELETE_ROLE_PERMISSION = "delete from sec.role_permission where role_id = :id;";

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private TransactionTemplate transactionTemplate;

    public JdbcRoleService(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public Role create(Role model) {
        transactionTemplate.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                SqlParameterSource namedParameters =
                        new MapSqlParameterSource("name", model.getName())
                                .addValue("code", model.getCode())
                                .addValue("description", model.getDescription());
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(INSERT_ROLE, namedParameters, keyHolder, new String[]{"id"});
                model.setId((Integer) keyHolder.getKey());
                savePermissions(model);
                return model;
            }
        });
        return model;
    }

    private void savePermissions(Role model) {
        if (!model.getPermissions().isEmpty()) {
            model.getPermissions().forEach(permission -> {
                SqlParameterSource params =
                        new MapSqlParameterSource("roleId", model.getId())
                                .addValue("permissionId", permission.getId());
                jdbcTemplate.update(INSERT_ROLE_PERMISSION, params);
            });
        }
    }

    public Role update(Role model) {
        transactionTemplate.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                SqlParameterSource namedParameters =
                        new MapSqlParameterSource("id", model.getId())
                                .addValue("name", model.getName())
                                .addValue("code", model.getCode())
                                .addValue("description", model.getDescription());
                jdbcTemplate.update(UPDATE_ROLE, namedParameters);
                jdbcTemplate.update(DELETE_ROLE_PERMISSION, namedParameters);
                savePermissions(model);
                return model;
            }
        });
        return model;
    }

    public void delete(Integer id) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("id", id);
        jdbcTemplate.update(DELETE_ROLE, namedParameters);
    }
}
