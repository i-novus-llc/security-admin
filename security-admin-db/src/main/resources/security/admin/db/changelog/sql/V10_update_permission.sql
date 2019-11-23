ALTER TABLE sec.permission ADD COLUMN system_code VARCHAR(50);
ALTER TABLE sec.permission
    ADD CONSTRAINT permission_system_code_fk FOREIGN KEY (system_code) REFERENCES sec.system (code);
COMMENT ON COLUMN sec.permission.system_code IS 'Прикладная система (подсистема, модуль), которой принадлежит привилегия';


ALTER TABLE sec.permission ADD COLUMN user_level VARCHAR(50);
COMMENT ON COLUMN sec.permission.user_level IS 'Уровень пользователя, для которого предназначена привилегия';