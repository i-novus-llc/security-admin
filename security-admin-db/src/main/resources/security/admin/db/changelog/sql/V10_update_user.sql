ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.user.table} ADD COLUMN region_id INTEGER;
COMMENT ON COLUMN ${n2o.security.admin.schema}.region.id IS 'Регион пользователя (заполняется для регионального уровня пользователей)';

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.user.table} ADD COLUMN organization_id INTEGER;
COMMENT ON COLUMN ${n2o.security.admin.schema}.organization.id IS 'Организация пользователя (заполняется для уровня пользователя - организация)';

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.user.table} ADD COLUMN department_id INTEGER;
COMMENT ON COLUMN ${n2o.security.admin.schema}.department.id IS 'Подразделение пользователя (заполняется для федерального уровня пользователя)';

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.user.table} ADD COLUMN position VARCHAR(50);
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.user.table}.position IS 'Должность пользователя';

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.user.table} ADD COLUMN user_level VARCHAR(50);
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.user.table}.user_level IS 'Уровень пользователя в Системе.';