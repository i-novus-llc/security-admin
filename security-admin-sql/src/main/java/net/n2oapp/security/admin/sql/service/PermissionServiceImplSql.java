package net.n2oapp.security.admin.sql.service;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.sql.util.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Реализация сервиса управления правами доступа для sql
 */
@Service
public class PermissionServiceImplSql implements PermissionService{

    private final static String INSERT_PERMISSION = "sql/permission/insert_permission.sql";
    private final static String UPDATE_PERMISSION = "sql/permission/update_permission.sql";
    private final static String DELETE_PERMISSION = "sql/permission/delete_permission.sql";
    private final static String SELECT_ALL = "sql/permission/select_all.sql";
    private final static String SELECT_ALL_BY_PARENT_ID = "sql/permission/select_all_by_parent_id.sql";
    private final static String GET_PERMISSION_BY_ID = "sql/permission/get_permission_by_id.sql";
    private final static String SELECT_ALL_BY_PARENT_ID_IS_NULL = "sql/permission/select_all_by_parent_id_is_null.sql";

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Permission create(Permission permission) {
        transactionTemplate.execute(transactionStatus -> {
            SqlParameterSource namedParameters =
                    new MapSqlParameterSource("name", permission.getName())
                            .addValue("code", permission.getCode())
                            .addValue("parentId", permission.getParentId());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(SqlUtil.readFileSql(INSERT_PERMISSION), namedParameters, keyHolder, new String[]{"id"});
            permission.setId((Integer) keyHolder.getKey());
            return permission;
        });
        return permission;
    }

    @Override
    public Permission update(Permission permission) {
        transactionTemplate.execute(transactionStatus -> {
            SqlParameterSource namedParameters =
                    new MapSqlParameterSource("id", permission.getId())
                            .addValue("name", permission.getName())
                            .addValue("code", permission.getCode())
                            .addValue("parentId", permission.getParentId());
            jdbcTemplate.update(SqlUtil.readFileSql(UPDATE_PERMISSION), namedParameters);
            return permission;
        });
        return permission;
    }

    @Override
    public void delete(Integer id) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("id", id);
        jdbcTemplate.update(SqlUtil.readFileSql(DELETE_PERMISSION), namedParameters);
    }

    @Override
    public Permission getById(Integer id) {
        try {
            return mapQueryResult(jdbcTemplate.queryForList(SqlUtil.readFileSql(GET_PERMISSION_BY_ID), new MapSqlParameterSource("id", id)).get(0));
        }catch(IndexOutOfBoundsException e){
            return null;
        }

    }

    @Override
    public List<Permission> getAll() {
        return jdbcTemplate.queryForList(SqlUtil.readFileSql(SELECT_ALL), new MapSqlParameterSource()).stream().map(this::mapQueryResult).collect(Collectors.toList());
    }

    @Override
    public List<Permission> getAllByParentId(Integer parentId) {
        return jdbcTemplate.queryForList(SqlUtil.readFileSql(SELECT_ALL_BY_PARENT_ID), new MapSqlParameterSource("parentId", parentId))
                .stream().map(this::mapQueryResult).collect(Collectors.toList());

    }

    @Override
    public List<Permission> getAllByParentIdIsNull() {
        return jdbcTemplate.queryForList(SqlUtil.readFileSql(SELECT_ALL_BY_PARENT_ID_IS_NULL),  new MapSqlParameterSource())
                .stream().map(this::mapQueryResult).collect(Collectors.toList());
    }

    private Permission mapQueryResult(Map map) {
        Permission permission = new Permission();
        permission.setId((Integer) map.get("ID"));
        permission.setName((String) map.get("NAME"));
        permission.setCode((String) map.get("CODE"));
        permission.setParentId((Integer) map.get("PARENT_ID"));
        return permission;
    }


}
