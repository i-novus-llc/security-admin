CREATE TABLE IF NOT EXISTS ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} (
  id INTEGER PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  code VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table} (
  ${n2o.security.admin.rolepermission.column.role} INTEGER NOT NULL,
  ${n2o.security.admin.rolepermission.column.permission} INTEGER NOT NULL,
  PRIMARY KEY (${n2o.security.admin.rolepermission.column.role}, ${n2o.security.admin.rolepermission.column.permission}),
  CONSTRAINT ${n2o.security.admin.rolepermission.constraint.pk} UNIQUE (${n2o.security.admin.rolepermission.column.role}, ${n2o.security.admin.rolepermission.column.permission}),
   CONSTRAINT ${n2o.security.admin.rolepermission.permission.constraint.fk} FOREIGN KEY (${n2o.security.admin.rolepermission.column.permission}) REFERENCES ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} (id),
  CONSTRAINT ${n2o.security.admin.rolepermission.role.constraint.fk} FOREIGN KEY (${n2o.security.admin.rolepermission.column.role}) REFERENCES ${n2o.security.admin.schema}.${n2o.security.admin.role.table} (id)
);



COMMENT ON TABLE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} IS 'Привелегии';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.permission.table}.name IS 'Название';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.permission.table}.code IS 'Код';

COMMENT ON TABLE ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table} IS 'Таблица связи привелегий с ролями';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table}.${n2o.security.admin.rolepermission.column.permission} IS 'Идентификатор привелегии';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table}.${n2o.security.admin.rolepermission.column.role} IS 'Идентификатор роли';