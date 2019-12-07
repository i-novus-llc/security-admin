CREATE TABLE IF NOT EXISTS ${n2o.security.admin.schema}.application (
  code VARCHAR(50) PRIMARY KEY,
  name VARCHAR(256) NOT NULL,
  system_code VARCHAR(50) NOT NULL,
  CONSTRAINT application_system_code_fk FOREIGN KEY (system_code)
    REFERENCES ${n2o.security.admin.schema}.system (code) ON DELETE CASCADE ON UPDATE RESTRICT
);

COMMENT ON TABLE ${n2o.security.admin.schema}.application IS 'Приложения';
COMMENT ON COLUMN ${n2o.security.admin.schema}.application.code IS 'Код приложения';
COMMENT ON COLUMN ${n2o.security.admin.schema}.application.name IS 'Наименование приложения';
COMMENT ON COLUMN ${n2o.security.admin.schema}.application.system_code IS 'Прикладная система (подсистема, модуль), которой принадлежит приложение';