CREATE TABLE IF NOT EXISTS ${n2o.security.admin.schema}.region (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  name VARCHAR(256) NOT NULL,
  OKATO VARCHAR(11)
);

COMMENT ON TABLE ${n2o.security.admin.schema}.region IS 'Системы';
COMMENT ON COLUMN ${n2o.security.admin.schema}.region.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN ${n2o.security.admin.schema}.region.code IS 'Код субъекта РФ (2-х значный)';
COMMENT ON COLUMN ${n2o.security.admin.schema}.region.name IS 'Наименование субъекта РФ';
COMMENT ON COLUMN ${n2o.security.admin.schema}.region.OKATO IS 'Код по ОКАТО';