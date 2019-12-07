ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table} DROP CONSTRAINT ${n2o.security.admin.rolepermission.permission.constraint.fk};

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table} DROP CONSTRAINT role_permission_pk;

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} DROP CONSTRAINT ${n2o.security.admin.permission.parent.constraint.fk};

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} DROP CONSTRAINT permission_pkey;