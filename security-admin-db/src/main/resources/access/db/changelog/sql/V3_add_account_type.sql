CREATE TABLE sec.account_type
(
    id SERIAL,
    code character varying(100) NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(255),
    user_level character varying(50) NOT NULL,
    status boolean NOT NULL,
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