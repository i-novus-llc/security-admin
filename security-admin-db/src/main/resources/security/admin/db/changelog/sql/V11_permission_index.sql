CREATE INDEX ${n2o.security.admin.rolepermission.permission.index}
ON ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table} USING btree(permission_code);

CREATE INDEX ${n2o.security.admin.permission.parent.index}
ON ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} USING btree(parent_code);
