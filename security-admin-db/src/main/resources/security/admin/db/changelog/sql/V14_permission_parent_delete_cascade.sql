ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table}
    DROP CONSTRAINT ${n2o.security.admin.permission.parent.constraint.fk};
ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table}
    ADD CONSTRAINT ${n2o.security.admin.permission.parent.constraint.fk} FOREIGN KEY ( parent_code )
    REFERENCES ${n2o.security.admin.schema}.${n2o.security.admin.permission.table}( code ) ON DELETE CASCADE ON UPDATE RESTRICT;
