CREATE TABLE IF NOT EXISTS sec.region (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  name VARCHAR(256) NOT NULL,
  OKATO VARCHAR(11)
);

COMMENT ON TABLE sec.region IS 'Системы';
COMMENT ON COLUMN sec.region.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN sec.region.code IS 'Код субъекта РФ (2-х значный)';
COMMENT ON COLUMN sec.region.name IS 'Наименование субъекта РФ';
COMMENT ON COLUMN sec.region.OKATO IS 'Код по ОКАТО';