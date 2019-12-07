ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} ADD CONSTRAINT permission_pkey PRIMARY KEY (code);

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} ADD CONSTRAINT ${n2o.security.admin.permission.parent.constraint.fk} FOREIGN KEY ( parent_code )
  REFERENCES ${n2o.security.admin.schema}.${n2o.security.admin.permission.table}( code );

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table} ADD CONSTRAINT role_permission_pk PRIMARY KEY (${n2o.security.admin.rolepermission.column.role},permission_code);

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table} ADD CONSTRAINT ${n2o.security.admin.rolepermission.permission.constraint.fk} FOREIGN KEY ( permission_code )
  REFERENCES ${n2o.security.admin.schema}.${n2o.security.admin.permission.table}( code );