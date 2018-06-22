CREATE TABLE IF NOT EXISTS ${n2o.security.admin.schema}.${n2o.security.admin.user.table} (
  id SERIAL PRIMARY KEY,
  ${n2o.security.admin.user.login} VARCHAR(100) NOT NULL,
  email VARCHAR(100),
  surname VARCHAR(100),
  name VARCHAR(100),
  patronymic VARCHAR(100),
  password VARCHAR(256),
  is_active boolean
);

COMMENT ON TABLE ${n2o.security.admin.schema}.${n2o.security.admin.user.table} IS 'Пользователи';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.user.table}.${n2o.security.admin.user.login} IS 'login пользователя';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.user.table}.email IS 'E-mail';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.user.table}.surname IS 'Фамилия';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.user.table}.name IS 'Имя';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.user.table}.patronymic IS 'Отчество';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.user.table}.password IS 'Пароль';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.user.table}.is_active IS 'Активность учетной записи';