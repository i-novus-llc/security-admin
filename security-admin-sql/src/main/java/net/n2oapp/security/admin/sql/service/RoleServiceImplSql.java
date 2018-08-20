package net.n2oapp.security.admin.sql.service;


import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Permission;
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

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    private final static String FIND_ALL = "sql/role/find_all.sql";
    private final static String FIND_ALL_COUNT = "sql/role/find_all_count.sql";

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
            return model(role);
        });
        return model(role);
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
            return model(role);
        });
        return model(role);
    }

    @Override
    public void delete(Integer id) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("id", id);
        jdbcTemplate.update(SqlUtil.getResourceFileAsString(DELETE_ROLE), namedParameters);
    }

    @Override
    public Role getById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(SqlUtil.getResourceFileAsString(GET_ROLE_BY_ID), new MapSqlParameterSource("id", id),(resultSet, i) -> {
                return model(resultSet);
            });
        } catch (EmptyResultDataAccessException e) {
            return  null;
        }
    }

    @Override
    public Page<Role> findAll(RoleCriteria criteria) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("name", criteria.getName())
                        .addValue("description", criteria.getDescription())
                        .addValue("permissionIds", criteria.getPermissionIds())
                        .addValue("limit", criteria.getPageSize())
                        .addValue("offset", criteria.getOffset());
        List<Role> roles = jdbcTemplate.query(SqlUtil.getResourceFileAsString(FIND_ALL), namedParameters, (resultSet, i) -> {
            return model(resultSet);
        });
        Integer count = jdbcTemplate.queryForObject(SqlUtil.getResourceFileAsString(FIND_ALL_COUNT), namedParameters,Integer.class);
        return new PageImpl<>(roles,criteria,count);

    }

    private Role model (ResultSet resultSet) throws SQLException {
        if (resultSet == null) return null;
        Role role = new Role();
        role.setId(resultSet.getInt("id"));
        role.setName(resultSet.getString("name"));
        role.setCode(resultSet.getString("code"));
        role.setDescription(resultSet.getString("description"));
        if (resultSet.getObject("ids") != null && resultSet.getObject("names") != null) {
            List<Permission> permissions = new ArrayList<>();
            Array a = resultSet.getArray("ids");
            Object idsObject = a.getArray();
            Integer[] ids;
            String[] names;
            String[] codes;
            // эта проверка нужна для поддержки различных реализаций для h2 и postrgesql
            // они возвращают разные объекты когда в запросе используется функция array_agg
            if (idsObject instanceof Integer[]) {
                ids = (Integer[]) a.getArray();
                a = resultSet.getArray("names");
                names = (String[]) a.getArray();
                a = resultSet.getArray("codes");
                codes = (String[]) a.getArray();
            } else {
                Object[]  idsObj = (Object[]) resultSet.getObject("ids");
                Object[] namesObj = (Object[]) resultSet.getObject("names");
                Object[] codesObj = (Object[]) resultSet.getObject("codes");
                ids = new Integer[idsObj.length];
                names = new String[namesObj.length];
                codes = new String[codesObj.length];
                for (int i = 0; i < idsObj.length; i++) {
                    ids[i] = (Integer)((Object[]) idsObj[i])[0];
                    names[i] = (String)((Object[])namesObj[i])[0];
                    codes[i] = (String)((Object[])codesObj[i])[0];
                }
            }
            for (int i = 0; i < ids.length && i < names.length && i < codes.length; i++) {
                Permission permission = new Permission();
                permission.setId(ids[i]);
                permission.setName(names[i]);
                permission.setCode(codes[i]);
                permissions.add(permission);
            }

            role.setPermissions(permissions);
        }
        return role;
    }

    private Role model(RoleForm form) {
        if (form == null) return null;
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
