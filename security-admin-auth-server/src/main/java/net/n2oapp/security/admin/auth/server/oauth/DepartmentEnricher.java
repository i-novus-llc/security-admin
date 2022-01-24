package net.n2oapp.security.admin.auth.server.oauth;

import net.n2oapp.security.admin.api.model.Department;
import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.DepartmentEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class DepartmentEnricher implements UserInfoEnricher<UserEntity> {
    @Override
    public Object buildValue(UserEntity source) {
        //        todo SECURITY-396
        DepartmentEntity departmentEntity = null;
//        departmentEntity = source.getDepartment();
        if (isNull(departmentEntity)) return null;

        Department department = new Department();
        department.setId(departmentEntity.getId());
        department.setCode(departmentEntity.getCode());
        department.setName(departmentEntity.getName());

        return department;
    }

    @Override
    public String getAlias() {
        return "department";
    }
}
