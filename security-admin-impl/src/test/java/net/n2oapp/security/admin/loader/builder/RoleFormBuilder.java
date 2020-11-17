package net.n2oapp.security.admin.loader.builder;

import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.model.UserLevel;

import java.util.Collections;

public class RoleFormBuilder {
    public static RoleForm buildRoleForm1() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("rcode1");
        roleForm.setName("rname1");
        roleForm.setDescription("desc1");
        roleForm.setPermissions(Collections.singletonList("test-code1"));
        roleForm.setUserLevel(UserLevel.FEDERAL.toString());
        return roleForm;
    }

    public static RoleForm buildRoleForm2() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("rcode2");
        roleForm.setName("rname2");
        roleForm.setDescription("desc2");
        roleForm.setPermissions(Collections.singletonList("test-code2"));
        roleForm.setUserLevel(UserLevel.REGIONAL.toString());
        return roleForm;
    }

    public static RoleForm buildRoleForm2Updated() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("rcode2");
        roleForm.setName("rname2-new");
        roleForm.setDescription("desc2-new");
        roleForm.setPermissions(Collections.singletonList("test-code1"));
        roleForm.setUserLevel(UserLevel.ORGANIZATION.toString());
        return roleForm;
    }
    public static RoleForm buildRoleForm3() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("rcode3");
        roleForm.setName("rname3");
        roleForm.setDescription("desc3");
        roleForm.setPermissions(Collections.singletonList("test-code1"));
        roleForm.setUserLevel(UserLevel.ORGANIZATION.toString());
        return roleForm;
    }

    public static RoleForm buildRoleForm4() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("rcode4");
        roleForm.setName("rname4");
        roleForm.setDescription("desc4");
        roleForm.setPermissions(Collections.singletonList("test-code1"));
        roleForm.setUserLevel(UserLevel.ORGANIZATION.toString());
        return roleForm;
    }

    public static RoleForm buildRoleForm5() {
        RoleForm roleForm = new RoleForm();
        roleForm.setCode("rcode5");
        roleForm.setName("rname5");
        roleForm.setDescription("desc5");
        roleForm.setPermissions(Collections.singletonList("test-code2"));
        roleForm.setUserLevel(UserLevel.ORGANIZATION.toString());
        return roleForm;
    }
}
