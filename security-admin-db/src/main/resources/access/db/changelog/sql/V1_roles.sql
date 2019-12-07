CREATE TABLE sec.role
(
    id SERIAL,
    name character varying(100) NOT NULL,
    code character varying(100),
    description character varying(250),
    system_code character varying(50),
    user_level character varying(50),
    CONSTRAINT role_pkey PRIMARY KEY (id),
    CONSTRAINT role_system_code_fk FOREIGN KEY (system_code)
        REFERENCES sec.system (code)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);


COMMENT ON TABLE sec.role
    IS 'Роли';

COMMENT ON COLUMN sec.role.name
    IS 'Название роли';

COMMENT ON COLUMN sec.role.code
    IS 'Код роли';

COMMENT ON COLUMN sec.role.description
    IS 'Описание роли';

COMMENT ON COLUMN sec.role.system_code
    IS 'Прикладная система (подсистема, модуль), которой принадлежит роль';

COMMENT ON COLUMN sec.role.user_level
    IS 'Уровень пользователя, для которого предназначена роль';