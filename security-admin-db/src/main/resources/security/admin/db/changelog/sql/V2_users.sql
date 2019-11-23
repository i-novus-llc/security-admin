CREATE TABLE IF NOT EXISTS sec.user (
  id SERIAL PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  email VARCHAR(100),
  surname VARCHAR(100),
  name VARCHAR(100),
  patronymic VARCHAR(100),
  password VARCHAR(256),
  is_active boolean
);

COMMENT ON TABLE sec.user IS 'Пользователи';
COMMENT ON COLUMN sec.user.username IS 'login пользователя';
COMMENT ON COLUMN sec.user.email IS 'E-mail';
COMMENT ON COLUMN sec.user.surname IS 'Фамилия';
COMMENT ON COLUMN sec.user.name IS 'Имя';
COMMENT ON COLUMN sec.user.patronymic IS 'Отчество';
COMMENT ON COLUMN sec.user.password IS 'Пароль';
COMMENT ON COLUMN sec.user.is_active IS 'Активность учетной записи';