CREATE TABLE sec.account
(
    id              SERIAL PRIMARY KEY,
    user_id         integer NOT NULL,
    name            varchar,
    region_id       integer,
    organization_id integer,
    department_id   integer,
    user_level      varchar,
    external_system varchar,
    external_uid    varchar,
    is_active       boolean,
    CONSTRAINT account_user_fk FOREIGN KEY (user_id) references sec."user" (id) ON DELETE CASCADE,
    CONSTRAINT account_region_fk FOREIGN KEY (region_id) references sec.region (id),
    CONSTRAINT account_department_fk FOREIGN KEY (department_id) references sec.department (id),
    CONSTRAINT account_organization_fk FOREIGN KEY (organization_id) references sec.organization (id)
);

COMMENT ON TABLE sec.account IS 'Аккаунты пользователя';

COMMENT ON COLUMN sec.account.user_id IS 'Идентификатор пользователя которому принадлежит аккаунт';
COMMENT ON COLUMN sec.account.name IS 'Имя аккаунта';
COMMENT ON COLUMN sec.account.region_id IS 'Идентификатор региона';
COMMENT ON COLUMN sec.account.organization_id IS 'Идентификатор организации';
COMMENT ON COLUMN sec.account.department_id IS 'Идентификатор департамента';
COMMENT ON COLUMN sec.account.user_level IS 'Уровень пользователя';
COMMENT ON COLUMN sec.account.external_system IS 'Наименование внешней системы';
COMMENT ON COLUMN sec.account.external_uid IS 'UID пользователя во внешней системе';
COMMENT ON COLUMN sec.account.is_active IS 'Признак активности аккаунта';


CREATE TABLE sec.account_role
(
    role_id    integer NOT NULL,
    account_id integer NOT NULL,
    CONSTRAINT account_role_pk PRIMARY KEY (role_id, account_id),
    CONSTRAINT account_role_role_fk FOREIGN KEY (role_id)
        REFERENCES sec.role (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT account_role_account_fk FOREIGN KEY (account_id)
        REFERENCES sec.account (id)
        ON UPDATE RESTRICT
        ON DELETE CASCADE
);