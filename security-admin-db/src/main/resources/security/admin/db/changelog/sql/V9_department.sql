CREATE TABLE IF NOT EXISTS sec.department (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  name VARCHAR(256) NOT NULL
);

COMMENT ON TABLE sec.department IS 'Системы';
COMMENT ON COLUMN sec.department.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN sec.department.code IS 'Код  подразделения МПС';
COMMENT ON COLUMN sec.department.name IS 'Наименование подразделения (департамента) МПС

';