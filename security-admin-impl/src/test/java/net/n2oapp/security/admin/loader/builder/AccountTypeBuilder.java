package net.n2oapp.security.admin.loader.builder;

import net.n2oapp.security.admin.api.model.AccountType;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.model.UserStatus;

import java.util.Arrays;

public class AccountTypeBuilder {

    public static AccountType accountType1() {
        AccountType accountType = new AccountType();
        accountType.setCode("code1");
        accountType.setName("name1");
        accountType.setDescription("description1");
        Role userRole = new Role();
        userRole.setCode("code1");
        accountType.setRoles(Arrays.asList(userRole));
        Role orgRole = new Role();
        orgRole.setCode("102");
        accountType.setOrgRoles(Arrays.asList(orgRole));
        accountType.setStatus(UserStatus.AWAITING_MODERATION);
        return accountType;
    }

    public static AccountType accountType2() {
        AccountType accountType = new AccountType();
        accountType.setCode("code2");
        accountType.setName("name2");
        accountType.setUserLevel(UserLevel.ORGANIZATION);
        Role userRole = new Role();
        userRole.setCode("code1");
        accountType.setRoles(Arrays.asList(userRole));
        Role orgRole = new Role();
        orgRole.setCode("102");
        accountType.setOrgRoles(Arrays.asList(orgRole));
        accountType.setStatus(UserStatus.AWAITING_MODERATION);
        return accountType;
    }

    public static AccountType accountType3() {
        AccountType accountType = new AccountType();
        accountType.setCode("code3");
        accountType.setName("name3");
        accountType.setUserLevel(UserLevel.ORGANIZATION);
        Role userRole = new Role();
        userRole.setCode("code1");
        accountType.setRoles(Arrays.asList(userRole));
        accountType.setStatus(UserStatus.AWAITING_MODERATION);
        return accountType;
    }

    public static AccountType accountType4() {
        AccountType accountType = new AccountType();
        accountType.setCode("code4");
        accountType.setName("name4");
        accountType.setUserLevel(UserLevel.ORGANIZATION);
        accountType.setStatus(UserStatus.AWAITING_MODERATION);
        return accountType;
    }

    public static AccountType accountType5() {
        AccountType accountType = new AccountType();
        accountType.setCode("code5");
        accountType.setName("name5");
        accountType.setUserLevel(UserLevel.ORGANIZATION);
        Role orgRole = new Role();
        orgRole.setCode("102");
        accountType.setOrgRoles(Arrays.asList(orgRole));
        accountType.setStatus(UserStatus.AWAITING_MODERATION);
        return accountType;
    }

    public static AccountType accountType6() {
        AccountType accountType = new AccountType();
        accountType.setCode("code6");
        accountType.setName("name6");
        Role userRole = new Role();
        userRole.setCode("code1");
        accountType.setRoles(Arrays.asList(userRole));
        Role orgRole = new Role();
        orgRole.setCode("102");
        Role orgRole2 = new Role();
        orgRole2.setCode("code1");
        accountType.setOrgRoles(Arrays.asList(orgRole, orgRole2));
        accountType.setStatus(UserStatus.AWAITING_MODERATION);
        return accountType;
    }

    public static AccountType accountType7() {
        AccountType accountType = new AccountType();
        accountType.setCode("testAccountTypeCode");
        accountType.setName("testAccountTypeCode");
        Role userRole = new Role();
        userRole.setCode("code1");
        accountType.setRoles(Arrays.asList(userRole));
        Role orgRole = new Role();
        orgRole.setCode("102");
        Role orgRole2 = new Role();
        orgRole2.setCode("code1");
        accountType.setOrgRoles(Arrays.asList(orgRole, orgRole2));
        accountType.setStatus(UserStatus.AWAITING_MODERATION);
        return accountType;
    }
}
