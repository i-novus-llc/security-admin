CREATE TABLE IF NOT EXISTS ${n2o.security.admin.schema}.organization (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  short_name VARCHAR(100) NOT NULL,
  full_name VARCHAR(256) NOT NULL,
  OGRN VARCHAR(500),
  OKPO VARCHAR(500)
);

COMMENT ON TABLE ${n2o.security.admin.schema}.organization IS 'Системы';
COMMENT ON COLUMN ${n2o.security.admin.schema}.organization.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN ${n2o.security.admin.schema}.organization.code IS 'Код организации';
COMMENT ON COLUMN ${n2o.security.admin.schema}.organization.short_name IS 'Краткое наименование организации';
COMMENT ON COLUMN ${n2o.security.admin.schema}.organization.full_name IS 'Полное наименование организации';
COMMENT ON COLUMN ${n2o.security.admin.schema}.organization.OGRN IS 'Код ОГРН (уникальный код организации)';
COMMENT ON COLUMN ${n2o.security.admin.schema}.organization.OKPO IS 'Код ОКПО (используется в стат.формах)';