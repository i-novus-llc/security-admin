CREATE UNIQUE INDEX ${n2o.security.admin.client.id.index}
ON ${n2o.security.admin.schema}.${n2o.security.admin.client.table} USING btree(${n2o.security.admin.client.column.client_id});