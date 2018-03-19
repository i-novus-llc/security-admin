package net.n2oapp.security.auth.simple;

import net.n2oapp.framework.api.exception.N2oException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {

    private final static String GET_ROLE_ID_BY_CODE = "select id from sec.role where code = :code;";

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public Integer getRoleIdByCode(String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        List<Integer> roleIds = jdbcTemplate.query(GET_ROLE_ID_BY_CODE, params, (rs, i) -> {
            return rs.getInt(1);
        });
        if (roleIds.isEmpty()) {
            return null;
        }
        if (roleIds.size() > 1) {
            throw new N2oException(String.format("Several roles with code = %s", code));
        }
        return roleIds.get(0);
    }
}
