CREATE  INDEX ${n2o.security.admin.user.username.index}
ON ${n2o.security.admin.schema}.${n2o.security.admin.user.table}(${n2o.security.admin.user.login});

CREATE INDEX ${n2o.security.admin.userrole.user.index}
ON ${n2o.security.admin.schema}.${n2o.security.admin.userrole.table}(${n2o.security.admin.userrole.column.user});

CREATE INDEX ${n2o.security.admin.userrole.role.index}
ON ${n2o.security.admin.schema}.${n2o.security.admin.userrole.table}(${n2o.security.admin.userrole.column.role});

