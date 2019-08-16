CREATE TABLE IF NOT EXISTS ${n2o.security.admin.schema}.department (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  name VARCHAR(256) NOT NULL
);

COMMENT ON TABLE ${n2o.security.admin.schema}.department IS 'Системы';
COMMENT ON COLUMN ${n2o.security.admin.schema}.department.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN ${n2o.security.admin.schema}.department.code IS 'Код  подразделения МПС';
COMMENT ON COLUMN ${n2o.security.admin.schema}.department.name IS 'Наименование подразделения (департамента) МПС

';