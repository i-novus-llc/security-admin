ALTER TABLE sec.role ADD COLUMN system_code VARCHAR(50);
ALTER TABLE sec.role
    ADD CONSTRAINT role_system_code_fk FOREIGN KEY (system_code) REFERENCES sec.system (code);
COMMENT ON COLUMN sec.role.system_code IS 'Прикладная система (подсистема, модуль), которой принадлежит роль';

ALTER TABLE sec.role ADD COLUMN user_level VARCHAR(50);
COMMENT ON COLUMN sec.role.user_level IS 'Уровень пользователя, для которого предназначена роль';