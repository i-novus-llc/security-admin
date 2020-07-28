CREATE TABLE sec.account_type
(
    id serial,
    code character VARYING(100) NOT NULL,
    name character varying(100) NOT NULL,
    description character VARYING(255),
    user_level character VARYING(50) NOT NULL,
    status character varying NOT NULL,
    CONSTRAINT account_type_pkey PRIMARY KEY (id)
);

COMMENT ON TABLE sec.account_type
    IS 'Типы аккаунтов';
COMMENT ON COLUMN sec.account_type.id
    IS 'Идентификатор типа аккаунта';
COMMENT ON COLUMN sec.account_type.code
    IS 'Наименование типа аккаунта';
COMMENT ON COLUMN sec.account_type.name
    IS 'Код типа аккаунта';
COMMENT ON COLUMN sec.account_type.description
    IS 'Описание типа аккаунта';
COMMENT ON COLUMN sec.account_type.user_level
    IS 'Уровень пользователя, который будет присвоен пользователю';
COMMENT ON COLUMN sec.account_type.status
    IS 'Статус регистрации';

CREATE TABLE sec.account_type_role
(
    account_type_id integer,
    role_id integer,
    org_default_role boolean NOT NULL DEFAULT FALSE,
    CONSTRAINT account_type_role_pkey PRIMARY KEY (account_type_id, role_id),
    CONSTRAINT account_type_role_account_fk FOREIGN KEY ( account_type_id ) REFERENCES sec.account_type( id ),
    CONSTRAINT account_type_role_role_fk FOREIGN KEY ( role_id ) REFERENCES sec.role( id )
);

COMMENT ON TABLE sec.account_type_role
    IS 'Таблица связи типов аккаунтов с ролями';
COMMENT ON COLUMN sec.account_type_role.account_type_id
    IS 'Идентификатор типа аккаунта';
COMMENT ON COLUMN sec.account_type_role.role_id
    IS 'Идентификатор роли';
COMMENT ON COLUMN sec.account_type_role.org_default_role
    IS 'Признак дефолтной роли организации';