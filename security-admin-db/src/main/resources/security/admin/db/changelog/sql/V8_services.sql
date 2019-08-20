CREATE TABLE IF NOT EXISTS ${n2o.security.admin.schema}.service (
  code VARCHAR(50) PRIMARY KEY,
  name VARCHAR(256) NOT NULL,
  system_code VARCHAR(50) NOT NULL,
  CONSTRAINT service_system_code_fk FOREIGN KEY (system_code)
    REFERENCES ${n2o.security.admin.schema}.system (code) ON DELETE CASCADE ON UPDATE RESTRICT
);

COMMENT ON TABLE ${n2o.security.admin.schema}.service IS 'Службы ';
COMMENT ON COLUMN ${n2o.security.admin.schema}.service.code IS 'Код cлужбы ';
COMMENT ON COLUMN ${n2o.security.admin.schema}.service.name IS 'Наименование cлужбы ';
COMMENT ON COLUMN ${n2o.security.admin.schema}.service.system_code IS 'Прикладная система (подсистема, модуль), которой принадлежит служба';