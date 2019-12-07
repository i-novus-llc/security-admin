CREATE TABLE sec."user"
(
    id SERIAL,
    username character varying(100) NOT NULL,
    email character varying(100),
    surname character varying(100),
    name character varying(100),
    patronymic character varying(100),
    password character varying(256),
    is_active boolean,
    snils character varying(14),
    region_id integer,
    organization_id integer,
    department_id integer,
    "position" character varying(50),
    user_level character varying(50),
    ext_sys character varying,
    ext_uid character varying,
    CONSTRAINT user_pkey PRIMARY KEY (id),
    CONSTRAINT user_department_fk FOREIGN KEY (department_id)
        REFERENCES sec.department (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT user_organization_fk FOREIGN KEY (organization_id)
        REFERENCES sec.organization (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT user_region_fk FOREIGN KEY (region_id)
        REFERENCES sec.region (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

COMMENT ON TABLE sec."user"
    IS 'Пользователи';

COMMENT ON COLUMN sec."user".username
    IS 'login пользователя';

COMMENT ON COLUMN sec."user".email
    IS 'E-mail';

COMMENT ON COLUMN sec."user".surname
    IS 'Фамилия';

COMMENT ON COLUMN sec."user".name
    IS 'Имя';

COMMENT ON COLUMN sec."user".patronymic
    IS 'Отчество';

COMMENT ON COLUMN sec."user".password
    IS 'Пароль';

COMMENT ON COLUMN sec."user".is_active
    IS 'Активность учетной записи';

COMMENT ON COLUMN sec."user".snils
    IS 'СНИЛС пользователя';

COMMENT ON COLUMN sec."user"."position"
    IS 'Должность пользователя';

COMMENT ON COLUMN sec."user".user_level
    IS 'Уровень пользователя в Системе.';

COMMENT ON COLUMN sec."user".ext_sys
    IS 'Наимнование SSO';



CREATE UNIQUE INDEX user_username_idx_uniq
    ON sec."user" USING btree
    (username);


-------------------------------------------------------------------------------------------------------

CREATE TABLE sec.user_role
(
    user_id integer NOT NULL,
    role_id integer NOT NULL,
    CONSTRAINT user_role_pk PRIMARY KEY (user_id, role_id),
    CONSTRAINT user_role_role_fk FOREIGN KEY (role_id)
        REFERENCES sec.role (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT user_role_user_fk FOREIGN KEY (user_id)
        REFERENCES sec."user" (id) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE CASCADE
);

COMMENT ON TABLE sec.user_role
    IS 'Связь пользователей с ролями';

COMMENT ON COLUMN sec.user_role.user_id
    IS 'Идентификатор пользователя';

COMMENT ON COLUMN sec.user_role.role_id
    IS 'Идентификатор роли';



CREATE INDEX user_role_role_idx
    ON sec.user_role USING btree
    (role_id);

CREATE INDEX user_role_user_idx
    ON sec.user_role USING btree
    (user_id);

