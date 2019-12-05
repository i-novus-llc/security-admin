ALTER TABLE ${n2o.security.admin.schema}.system ADD COLUMN short_name VARCHAR,
ADD COLUMN icon VARCHAR,
ADD COLUMN url VARCHAR,
ADD COLUMN public_access BOOLEAN,
ADD COLUMN view_order INTEGER;

COMMENT ON COLUMN ${n2o.security.admin.schema}.system.short_name IS 'Краткое наименование';
COMMENT ON COLUMN ${n2o.security.admin.schema}.system.short_name IS 'Иконка';
COMMENT ON COLUMN ${n2o.security.admin.schema}.system.short_name IS 'Адрес подсистемы';
COMMENT ON COLUMN ${n2o.security.admin.schema}.system.short_name IS 'Признак работы в режиме без авторизации';
COMMENT ON COLUMN ${n2o.security.admin.schema}.system.short_name IS 'Порядок отображения подсистемы';



