CREATE TABLE IF NOT EXISTS sec.permission (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  code VARCHAR(100) NOT NULL,
  parent_id INTEGER,
  CONSTRAINT permission_parent_fk FOREIGN KEY ( parent_id )
  REFERENCES sec.permission( id )
);

CREATE TABLE IF NOT EXISTS sec.role_permission (
  role_id INTEGER NOT NULL,
  permission_id INTEGER NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  CONSTRAINT role_permission_pk UNIQUE (role_id, permission_id),
  CONSTRAINT role_permission_permission_fk FOREIGN KEY (permission_id)
  REFERENCES sec.permission (id),
  CONSTRAINT role_permission_role_fk FOREIGN KEY (role_id)
  REFERENCES sec.role (id) ON DELETE CASCADE ON UPDATE RESTRICT
);



COMMENT ON TABLE sec.permission IS 'Права доступа';
COMMENT ON COLUMN sec.permission.name IS 'Название';
COMMENT ON COLUMN sec.permission.code IS 'Код';

COMMENT ON TABLE sec.role_permission IS 'Таблица связи прав доступа с ролями';
COMMENT ON COLUMN sec.role_permission.permission_id IS 'Идентификатор права доступа';
COMMENT ON COLUMN sec.role_permission.role_id IS 'Идентификатор роли';