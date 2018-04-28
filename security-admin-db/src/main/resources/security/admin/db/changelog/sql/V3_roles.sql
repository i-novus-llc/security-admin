CREATE TABLE IF NOT EXISTS ${n2o.security.admin.schema}.${n2o.security.admin.role.table} (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  code VARCHAR(100),
  description VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS ${n2o.security.admin.schema}.${n2o.security.admin.userrole.table} (
  ${n2o.security.admin.userrole.column.user} INTEGER NOT NULL,
  ${n2o.security.admin.userrole.column.role} INTEGER NOT NULL,
  PRIMARY KEY (${n2o.security.admin.userrole.column.user}, ${n2o.security.admin.userrole.column.role}),
  CONSTRAINT ${n2o.security.admin.userrole.constraint.pk} UNIQUE (${n2o.security.admin.userrole.column.user}, ${n2o.security.admin.userrole.column.role}),
  CONSTRAINT ${n2o.security.admin.userrole.user.constraint.fk} FOREIGN KEY (${n2o.security.admin.userrole.column.user})
  REFERENCES ${n2o.security.admin.schema}.${n2o.security.admin.user.table} (id) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT ${n2o.security.admin.userrole.role.constraint.fk} FOREIGN KEY (${n2o.security.admin.userrole.column.role})
  REFERENCES ${n2o.security.admin.schema}.${n2o.security.admin.role.table} (id)
);


COMMENT ON TABLE ${n2o.security.admin.schema}.${n2o.security.admin.role.table} IS 'Роли';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.role.table}.name IS 'Название роли';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.role.table}.code IS 'Код роли';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.role.table}.description IS 'Описание роли';

COMMENT ON TABLE ${n2o.security.admin.schema}.${n2o.security.admin.userrole.table} IS 'Связь пользователей с ролями';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.userrole.table}.${n2o.security.admin.userrole.column.user} IS 'Идентификатор пользователя';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.userrole.table}.${n2o.security.admin.userrole.column.role} IS 'Идентификатор роли';