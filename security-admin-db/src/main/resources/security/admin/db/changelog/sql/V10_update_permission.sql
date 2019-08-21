ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} ADD COLUMN system_code VARCHAR(50);
ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table}
    ADD CONSTRAINT permission_system_code_fk FOREIGN KEY (system_code) REFERENCES ${n2o.security.admin.schema}.system (code);
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.permission.table}.system_code IS 'Прикладная система (подсистема, модуль), которой принадлежит привилегия';


ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} ADD COLUMN user_level VARCHAR(50);
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.permission.table}.user_level IS 'Уровень пользователя, для которого предназначена привилегия';