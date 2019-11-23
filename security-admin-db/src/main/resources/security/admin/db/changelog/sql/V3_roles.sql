CREATE TABLE IF NOT EXISTS sec.role (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  code VARCHAR(100),
  description VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS sec.user_role (
  user_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT user_role_pk UNIQUE (user_id, role_id),
  CONSTRAINT user_role_user_fk FOREIGN KEY (user_id)
  REFERENCES sec.user (id) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT user_role_role_fk FOREIGN KEY (role_id)
  REFERENCES sec.role (id)
);


COMMENT ON TABLE sec.role IS 'Роли';
COMMENT ON COLUMN sec.role.name IS 'Название роли';
COMMENT ON COLUMN sec.role.code IS 'Код роли';
COMMENT ON COLUMN sec.role.description IS 'Описание роли';

COMMENT ON TABLE sec.user_role IS 'Связь пользователей с ролями';
COMMENT ON COLUMN sec.user_role.user_id IS 'Идентификатор пользователя';
COMMENT ON COLUMN sec.user_role.role_id IS 'Идентификатор роли';