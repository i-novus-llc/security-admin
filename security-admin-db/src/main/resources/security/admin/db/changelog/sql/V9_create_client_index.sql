CREATE INDEX ${n2o.security.admin.clientrole.client.index}
ON ${n2o.security.admin.schema}.${n2o.security.admin.clientrole.table} USING btree(${n2o.security.admin.clientrole.column.client});

CREATE INDEX ${n2o.security.admin.clientrole.role.index}
ON ${n2o.security.admin.schema}.${n2o.security.admin.clientrole.table} USING btree(${n2o.security.admin.clientrole.column.role});