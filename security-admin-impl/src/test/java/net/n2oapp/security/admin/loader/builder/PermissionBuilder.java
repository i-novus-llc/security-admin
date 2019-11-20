package net.n2oapp.security.admin.loader.builder;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.UserLevel;

public class PermissionBuilder {

    public static Permission buildPermission1() {
        Permission permission = new Permission();
        permission.setCode("pcode1");
        permission.setName("name1");
        permission.setParentCode(null);
        permission.setUserLevel(UserLevel.FEDERAL);
        return permission;
    }

    public static Permission buildPermission2() {
        Permission permission = new Permission();
        permission.setCode("pcode2");
        permission.setName("name2");
        permission.setParentCode("pcode1");
        permission.setUserLevel(UserLevel.REGIONAL);
        return permission;
    }

    public static Permission buildPermission2Updated() {
        Permission permission = new Permission();
        permission.setCode("pcode2");
        permission.setName("name2-new");
        permission.setParentCode("pcode3");
        permission.setUserLevel(UserLevel.ORGANIZATION);
        return permission;
    }

    public static Permission buildPermission3() {
        Permission permission = new Permission();
        permission.setCode("pcode3");
        permission.setName("name3");
        permission.setParentCode(null);
        permission.setUserLevel(UserLevel.FEDERAL);
        return permission;
    }

    public static Permission buildPermission4() {
        Permission permission = new Permission();
        permission.setCode("pcode4");
        permission.setName("name4");
        permission.setParentCode(null);
        permission.setUserLevel(UserLevel.FEDERAL);
        return permission;
    }

    public static Permission buildPermission5() {
        Permission permission = new Permission();
        permission.setCode("pcode5");
        permission.setName("name5");
        permission.setParentCode("pcode4");
        permission.setUserLevel(UserLevel.REGIONAL);
        return permission;
    }
}
