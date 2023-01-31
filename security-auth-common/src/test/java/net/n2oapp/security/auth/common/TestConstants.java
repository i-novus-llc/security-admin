package net.n2oapp.security.auth.common;

import lombok.NoArgsConstructor;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

@NoArgsConstructor
public final class TestConstants {

    public static final String PRINCIPIAL_USERNAME_ATTR = "name";
    public static final String PRINCIPIAL_SURNAME_ATTR = "surname";
    public static final String PRINCIPIAL_NAME_ATTR = "firstName";
    public static final String PRINCIPIAL_PATRONYMIC_ATTR = "patronymic";
    public static final String PRINCIPIAL_EMAIL_ATTR = "email";
    public static final String PRINCIPIAL_ID_ATTR = "oid";
    public static final String PRINCIPIAL_PERMISSIONS_ATTR = "permissions";
    public static final String PRINCIPIAL_OTHER_USERNAME_ATTR = "principialName";


    public static final String SOME_USERNAME = "someUserName";
    public static final String SOME_SURNAME = "someSurname";
    public static final String SOME_NAME = "someName";
    public static final String SOME_PATRONYMIC = "somePatronymic";
    public static final String SOME_ROLE = "ROLE_USER";
    public static final String SOME_PASSWORD = "pass";
    public static final String SOME_EMAIL = "someEmail";
    public static final String SOME_PERMISSION = "somePermission";
    public static final String OTHER_PERMISSION = "otherPermission";
    public static final String SOME_ROLE_NAME = "USER";
    public static final String OTHER_ROLE_NAME = "ADMIN";
    public static final String SOME_SYSTEM_NAME = "SYSTEM";
    public static final String OTHER_SYSTEM_NAME = "ARM";
    public static final String SOME_ORGANIZATION = "someOrganization";
    public static final String SOME_USER_LEVEL = UserLevel.PERSONAL.name();
    public static final String SOME_REGION = "someRegion";
    public static final String SOME_SYSTEM = "someSystem";
    public static final String SOME_DEPARTMENT = "someDepartment";
    public static final String SOME_DEPARTMENT_NAME = "someDepartmentName";

    public static final String SOME_REGION_CODE = UUID.randomUUID().toString();
    public static final String SOME_ORGANIZATION_CODE = UUID.randomUUID().toString();
    public static final String SOME_DEPARTMENT_CODE = UUID.randomUUID().toString();


    public static final GrantedAuthority SOME_GRANTED_AUTHORITY = new RoleGrantedAuthority(SOME_ROLE);
    public static final Set SOME_AUTHORITEIES = Collections.unmodifiableSet(Set.of(SOME_GRANTED_AUTHORITY));
    public static final List SOME_ROLE_LIST = Arrays.asList(SOME_ROLE);

    public static final String GRANTED_AUTHORITY_KEY = "GrantedAuthorityKey";
    public static final String VALUE_NOT_AVAILABLE = "N/A";

    private static final String USER_FULLNAME_FORMAT = "%s %s %s";
    private static final String USER_NAME_FULLNAME_FORMAT = "%s %s";
    private static final String USER_SHORTNAME_FORMAT = "%s %s.%s.";

    public static String getUserShortName() {
        return String.format(USER_SHORTNAME_FORMAT, SOME_SURNAME,
                SOME_NAME.substring(0, 1).toUpperCase(),
                SOME_PATRONYMIC.substring(0, 1).toUpperCase());
    }

    public static String getUserFullName() {
        return String.format(USER_FULLNAME_FORMAT, SOME_SURNAME, SOME_NAME, SOME_PATRONYMIC);
    }

    public static String getUserNameSurname() {
        return String.format(USER_NAME_FULLNAME_FORMAT, SOME_NAME, SOME_SURNAME);
    }
}
