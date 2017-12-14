CREATE INDEX ${n2o.security.admin.rolepermission.permission.index}
ON ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table} USING btree(${n2o.security.admin.rolepermission.column.permission});

CREATE INDEX ${n2o.security.admin.rolepermission.role.index}
ON ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table} USING btree(${n2o.security.admin.rolepermission.column.role});

CREATE INDEX ${n2o.security.admin.permission.parent.index}
ON ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} USING btree(parent_id);
