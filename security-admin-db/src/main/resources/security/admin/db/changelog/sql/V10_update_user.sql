ALTER TABLE sec.user ADD COLUMN region_id INTEGER;
COMMENT ON COLUMN sec.region.id IS 'Регион пользователя (заполняется для регионального уровня пользователей)';

ALTER TABLE sec.user ADD COLUMN organization_id INTEGER;
COMMENT ON COLUMN sec.organization.id IS 'Организация пользователя (заполняется для уровня пользователя - организация)';

ALTER TABLE sec.user ADD COLUMN department_id INTEGER;
COMMENT ON COLUMN sec.department.id IS 'Подразделение пользователя (заполняется для федерального уровня пользователя)';

ALTER TABLE sec.user ADD COLUMN position VARCHAR(50);
COMMENT ON COLUMN sec.user.position IS 'Должность пользователя';

ALTER TABLE sec.user ADD COLUMN user_level VARCHAR(50);
COMMENT ON COLUMN sec.user.user_level IS 'Уровень пользователя в Системе.';