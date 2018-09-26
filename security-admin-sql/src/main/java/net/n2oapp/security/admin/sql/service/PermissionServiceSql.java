package net.n2oapp.security.admin.sql.service;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.sql.util.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * Реализация сервиса управления правами доступа для sql
 */
@Service
public class PermissionServiceSql implements PermissionService{

    private final static String INSERT_PERMISSION = "sql/permission/insert_permission.sql";
    private final static String UPDATE_PERMISSION = "sql/permission/update_permission.sql";
    private final static String DELETE_PERMISSION = "sql/permission/delete_permission.sql";
    private final static String GET_ALL = "sql/permission/get_all.sql";
    private final static String GET_ALL_BY_PARENT_ID = "sql/permission/get_all_by_parent_id.sql";
    private final static String GET_PERMISSION_BY_ID = "sql/permission/get_permission_by_id.sql";
    private final static String GET_ALL_BY_PARENT_ID_IS_NULL = "sql/permission/get_all_by_parent_id_is_null.sql";
    private final static String HAS_CHILDREN = "sql/permission/has_children.sql";

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
            jdbcTemplate.update(SqlUtil.getResourceFileAsString(INSERT_PERMISSION), namedParameters, keyHolder, new String[]{"id"});
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
            jdbcTemplate.update(SqlUtil.getResourceFileAsString(UPDATE_PERMISSION), namedParameters);
            return permission;
        });
        return permission;
    }

    @Override
    public void delete(Integer id) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("id", id);
        jdbcTemplate.update(SqlUtil.getResourceFileAsString(DELETE_PERMISSION), namedParameters);
    }

    @Override
    public Permission getById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(SqlUtil.getResourceFileAsString(GET_PERMISSION_BY_ID), new MapSqlParameterSource("id", id), (resultSet, i) -> {
                return model(resultSet);
            });
        }catch(EmptyResultDataAccessException e){
            return null;
        }

    }

    @Override
    public List<Permission> getAll() {
        return jdbcTemplate.query(SqlUtil.getResourceFileAsString(GET_ALL), new MapSqlParameterSource(), (resultSet, i) -> {
            return model(resultSet);
        });
    }

    @Override
    public List<Permission> getAllByParentId(Integer parentId) {
        return jdbcTemplate.query(SqlUtil.getResourceFileAsString(GET_ALL_BY_PARENT_ID), new MapSqlParameterSource("parentId", parentId), (resultSet, i) -> {
            return model(resultSet);
        });

    }

    @Override
    public List<Permission> getAllByParentIdIsNull() {
        return jdbcTemplate.query(SqlUtil.getResourceFileAsString(GET_ALL_BY_PARENT_ID_IS_NULL), new MapSqlParameterSource(), (resultSet, i) -> {
            return model(resultSet);
        });
    }

    private Permission model(ResultSet resultSet) throws SQLException {
        if (resultSet == null) return null;
        Permission permission = new Permission();
        permission.setId(resultSet.getInt("id"));
        permission.setName(resultSet.getString("name"));
        permission.setCode(resultSet.getString("code"));
        permission.setParentId(resultSet.getInt("parent_id"));
        permission.setHasChildren(jdbcTemplate.queryForObject(SqlUtil.getResourceFileAsString(HAS_CHILDREN), new MapSqlParameterSource("id", permission.getId()), Boolean.class));
        return permission;
    }


}
