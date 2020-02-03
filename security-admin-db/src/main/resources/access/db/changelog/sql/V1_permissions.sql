CREATE TABLE sec.permission
(
    name character varying(100) NOT NULL,
    code character varying(100) NOT NULL,
    parent_code character varying(100),
    system_code character varying(50),
    user_level character varying(50),
    CONSTRAINT permission_pkey PRIMARY KEY (code),
    CONSTRAINT permission_parent_fk FOREIGN KEY (parent_code)
        REFERENCES sec.permission (code)
        ON UPDATE RESTRICT
        ON DELETE CASCADE,
    CONSTRAINT permission_system_code_fk FOREIGN KEY (system_code)
        REFERENCES sec.system (code)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

COMMENT ON TABLE sec.permission
    IS 'Права доступа';

COMMENT ON COLUMN sec.permission.name
    IS 'Название';

COMMENT ON COLUMN sec.permission.code
    IS 'Код';

COMMENT ON COLUMN sec.permission.system_code
    IS 'Прикладная система (подсистема, модуль), которой принадлежит привилегия';

COMMENT ON COLUMN sec.permission.user_level
    IS 'Уровень пользователя, для которого предназначена привилегия';

CREATE INDEX permission_parent_idx
    ON sec.permission USING btree
    (parent_code);
 -------------------------------------------------------------------------------------------------------

CREATE TABLE sec.role_permission
(
    role_id integer NOT NULL,
    permission_code character VARYING(100) NOT NULL,
    CONSTRAINT role_permission_pk PRIMARY KEY (role_id, permission_code),
    CONSTRAINT role_permission_permission_fk FOREIGN KEY (permission_code)
        REFERENCES sec.permission (code)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT role_permission_role_fk FOREIGN KEY (role_id)
        REFERENCES sec.role (id)
        ON UPDATE RESTRICT
        ON DELETE CASCADE
);

COMMENT ON TABLE sec.role_permission
    IS 'Таблица связи прав доступа с ролями';

COMMENT ON COLUMN sec.role_permission.role_id
    IS 'Идентификатор роли';



CREATE INDEX role_permission_permission_idx
    ON sec.role_permission USING btree
    (permission_code);


CREATE INDEX role_permission_role_idx
    ON sec.role_permission USING btree
    (role_id);
