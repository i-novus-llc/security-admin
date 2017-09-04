package net.n2oapp.security;

import static net.n2oapp.properties.StaticProperties.getProperty;

public class SecuritySchemaConfig {
    public static final String SCHEMA_NAME = getProperty("n2o.security.admin.schema");
    public static final String USER_TABLE = getProperty("n2o.security.admin.schema") + "." + getProperty("n2o.security.admin.user.table");
    public static final String USER_LOGIN_COLUMN = getProperty("n2o.security.admin.user.login");
    public static final String ROLE_TABLE = getProperty("n2o.security.admin.schema") + "." + getProperty("n2o.security.admin.role.table");
    public static final String USERROLE_TABLE = getProperty("n2o.security.admin.schema") + "." + getProperty("n2o.security.admin.userrole.table");
    public static final String USERROLE_ROLE_COLUMN = getProperty("n2o.security.admin.userrole.column.role");
    public static final String USERROLE_USER_COLUMN = getProperty("n2o.security.admin.userrole.column.user");
    public static final String USERROLE_PK = getProperty("n2o.security.admin.userrole.constraint.pk");
    public static final String USERROLE_USER_COLUMN_FK = getProperty("n2o.security.admin.userrole.user.constraint.fk");
    public static final String USERROLE_ROLE_COLUMN_FK = getProperty("n2o.security.admin.userrole.role.constraint.fk");
    public static final String PERMISSION_TABLE = getProperty("n2o.security.admin.schema") + "." + getProperty("n2o.security.admin.permission.table");
    public static final String ROLEPERMISSION_TABLE = getProperty("n2o.security.admin.schema") + "." + getProperty("n2o.security.admin.rolepermission.table");
    public static final String ROLEPERMISSION_ROLE_COLUMN = getProperty("n2o.security.admin.rolepermission.column.role");
    public static final String ROLEPERMISSION_PERMISSION_COLUMN = getProperty("n2o.security.admin.rolepermission.column.permission");
    public static final String ROLEPERMISSION_PK = getProperty("n2o.security.admin.rolepermission.constraint.pk");
    public static final String ROLEPERMISSION_PERMISSION_COLUMN_FK = getProperty("n2o.security.admin.rolepermission.permission.constraint.fk");
    public static final String ROLEPERMISSION_ROLE_COLUMN_FK = getProperty("n2o.security.admin.rolepermission.role.constraint.fk");
}
