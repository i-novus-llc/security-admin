package net.n2oapp.security.admin.sql.service;


import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.api.service.RoleService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления ролями для sql
 */
@Service
public class RoleServiceImplSql implements RoleService {

    private final static String INSERT_ROLE = "sql/role/insert_role.sql";
    private final static String UPDATE_ROLE = "sql/role/update_role.sql";
    private final static String DELETE_ROLE = "sql/role/delete_role.sql";
    private final static String INSERT_ROLE_PERMISSION = "sql/role/insert_role_permission.sql";
    private final static String DELETE_ROLE_PERMISSION = "sql/role/delete_role_permission.sql";
    private final static String GET_ROLE_BY_ID = "sql/role/get_role_by_id.sql";
    private final static String GET_PERMISSION_BY_ROLE_ID = "sql/role/get_permission_by_role_id.sql";
    private final static String SELECT_ALL = "sql/role/select_all.sql";
    private final static String SELECT_ALL_PERMISSIONS = "sql/role/select_all_permissions.sql";
    private final static String SELECT_ALL_PERMISSIONS_BY_CRITERIA = "sql/role/select_all_permissions_by_criteria.sql";

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private PermissionService service;

    @Override
    public Role create(RoleForm role) {
        transactionTemplate.execute(transactionStatus -> {
            SqlParameterSource namedParameters =
                    new MapSqlParameterSource("name", role.getName())
                            .addValue("code", role.getCode())
                            .addValue("description", role.getDescription());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(SqlUtil.getResourceFileAsString(INSERT_ROLE), namedParameters, keyHolder, new String[]{"id"});
            role.setId((Integer) keyHolder.getKey());
            savePermissions(role);
            return map(role);
        });
        return map(role);
    }

    private void savePermissions(RoleForm role) {
        if (role.getPermissions() != null) {
            role.getPermissions().forEach(permission -> {
                SqlParameterSource params =
                        new MapSqlParameterSource("roleId", role.getId())
                                .addValue("permissionId", permission);
                jdbcTemplate.update(SqlUtil.getResourceFileAsString(INSERT_ROLE_PERMISSION), params);
            });
        }
    }

    @Override
    public Role update(RoleForm role) {
        transactionTemplate.execute(transactionStatus -> {
            SqlParameterSource namedParameters =
                    new MapSqlParameterSource("id", role.getId())
                            .addValue("name", role.getName())
                            .addValue("code", role.getCode())
                            .addValue("description", role.getDescription());
            jdbcTemplate.update(SqlUtil.getResourceFileAsString(UPDATE_ROLE), namedParameters);
            jdbcTemplate.update(SqlUtil.getResourceFileAsString(DELETE_ROLE_PERMISSION), namedParameters);
            savePermissions(role);
            return map(role);
        });
        return map(role);
    }

    @Override
    public void delete(Integer id) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("id", id);
        jdbcTemplate.update(SqlUtil.getResourceFileAsString(DELETE_ROLE), namedParameters);
    }

    @Override
    public Role getById(Integer id) {
        List<Map<String, Object>> all = jdbcTemplate.queryForList(SqlUtil.getResourceFileAsString(GET_ROLE_BY_ID), new MapSqlParameterSource("id", id));
        if (all.isEmpty()) {
            return null;
        } else {
            Role role = mapQueryResult(all.get(0));
            try {
                role.setPermissions(jdbcTemplate.queryForList(SqlUtil.getResourceFileAsString(GET_PERMISSION_BY_ROLE_ID), new MapSqlParameterSource("role_id", id), Integer.class)
                        .stream().map(service::getById).collect(Collectors.toList()));
            } catch (EmptyResultDataAccessException e) {
                role.setPermissions(null);
                return role;
            }
            return role;
        }
    }

    @Override
    public Page<Role> findAll(RoleCriteria criteria) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("name", criteria.getName())
                        .addValue("description", criteria.getDescription());
        List<Role> roles = jdbcTemplate.queryForList(SqlUtil.getResourceFileAsString(SELECT_ALL), namedParameters).stream().map(this::mapQueryResult).collect(Collectors.toList());
        roles.stream().forEach(role -> role.setPermissions(jdbcTemplate.queryForList(SqlUtil.getResourceFileAsString(SELECT_ALL_PERMISSIONS),
                new MapSqlParameterSource().addValue("id", role.getId()), Integer.class).stream().map(service::getById).collect(Collectors.toList())));
        if (criteria.getPermissionIds() != null) {
            return new PageImpl<>(roles.stream().filter(role -> !jdbcTemplate.queryForObject(SqlUtil.getResourceFileAsString(SELECT_ALL_PERMISSIONS_BY_CRITERIA),
                    new MapSqlParameterSource()
                            .addValue("id", role.getId())
                            .addValue("permissionIds", criteria.getPermissionIds()), Integer.class).equals(0))
                    .collect(Collectors.toList()));
        }
        return new PageImpl<>(roles);
    }

    private Role mapQueryResult(Map map) {
        Role role = new Role();
        role.setId((Integer) map.get("ID"));
        role.setName((String) map.get("NAME"));
        role.setCode((String) map.get("CODE"));
        role.setDescription((String) map.get("DESCRIPTION"));
        return role;
    }


    private Role map(RoleForm form) {
        Role role = new Role();
        role.setId(form.getId());
        role.setCode(form.getCode());
        role.setName(form.getName());
        role.setDescription(form.getDescription());
        if(form.getPermissions() != null) {
            role.setPermissions(form.getPermissions().stream().map(service::getById).collect(Collectors.toList()));
        }
        return role;
    }

}
