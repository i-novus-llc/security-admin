ALTER TABLE sec.system ADD COLUMN short_name VARCHAR;
ALTER TABLE sec.system ADD COLUMN icon VARCHAR;
ALTER TABLE sec.system ADD COLUMN url VARCHAR;
ALTER TABLE sec.system ADD COLUMN public_access VARCHAR;
ALTER TABLE sec.system ADD COLUMN view_order VARCHAR;

COMMENT ON COLUMN sec.system.short_name IS 'Краткое наименование';
COMMENT ON COLUMN sec.system.short_name IS 'Иконка';
COMMENT ON COLUMN sec.system.short_name IS 'Адрес подсистемы';
COMMENT ON COLUMN sec.system.short_name IS 'Признак работы в режиме без авторизации';
COMMENT ON COLUMN sec.system.short_name IS 'Порядок отображения подсистемы';



