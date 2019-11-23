CREATE TABLE IF NOT EXISTS sec.application (
  code VARCHAR(50) PRIMARY KEY,
  name VARCHAR(256) NOT NULL,
  system_code VARCHAR(50) NOT NULL,
  CONSTRAINT application_system_code_fk FOREIGN KEY (system_code)
    REFERENCES sec.system (code) ON DELETE CASCADE ON UPDATE RESTRICT
);

COMMENT ON TABLE sec.application IS 'Приложения';
COMMENT ON COLUMN sec.application.code IS 'Код приложения';
COMMENT ON COLUMN sec.application.name IS 'Наименование приложения';
COMMENT ON COLUMN sec.application.system_code IS 'Прикладная система (подсистема, модуль), которой принадлежит приложение';