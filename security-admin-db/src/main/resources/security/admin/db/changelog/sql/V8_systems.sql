CREATE TABLE IF NOT EXISTS sec.system (
  code VARCHAR(50) PRIMARY KEY,
  name VARCHAR(256) NOT NULL,
  description VARCHAR(500)
);

COMMENT ON TABLE sec.system IS 'Системы';
COMMENT ON COLUMN sec.system.code IS 'Код системы';
COMMENT ON COLUMN sec.system.name IS 'Наименование системы';
COMMENT ON COLUMN sec.system.description IS 'Текстовое описание системы';