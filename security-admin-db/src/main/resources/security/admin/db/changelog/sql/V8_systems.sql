CREATE TABLE IF NOT EXISTS ${n2o.security.admin.schema}.system (
  code VARCHAR(50) PRIMARY KEY,
  name VARCHAR(256) NOT NULL,
  description VARCHAR(500)
);

COMMENT ON TABLE ${n2o.security.admin.schema}.system IS 'Системы';
COMMENT ON COLUMN ${n2o.security.admin.schema}.system.code IS 'Код системы';
COMMENT ON COLUMN ${n2o.security.admin.schema}.system.name IS 'Наименование системы';
COMMENT ON COLUMN ${n2o.security.admin.schema}.system.description IS 'Текстовое описание системы';