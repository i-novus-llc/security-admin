ALTER TABLE sec.system ADD COLUMN short_name VARCHAR,
ADD COLUMN icon VARCHAR,
ADD COLUMN url VARCHAR,
ADD COLUMN public_access BOOLEAN,
ADD COLUMN view_order INTEGER;

COMMENT ON COLUMN sec.system.short_name IS 'Краткое наименование';
COMMENT ON COLUMN sec.system.short_name IS 'Иконка';
COMMENT ON COLUMN sec.system.short_name IS 'Адрес подсистемы';
COMMENT ON COLUMN sec.system.short_name IS 'Признак работы в режиме без авторизации';
COMMENT ON COLUMN sec.system.short_name IS 'Порядок отображения подсистемы';



