package net.n2oapp.security.admin.impl.userinfo;

import net.n2oapp.security.admin.api.model.Department;
import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import net.n2oapp.security.admin.impl.entity.DepartmentEntity;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class DepartmentEnricher implements UserInfoEnricher<AccountEntity> {
    @Override
    public Object buildValue(AccountEntity source) {
        DepartmentEntity departmentEntity = source.getDepartment();
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
