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
    CONSTRAINT account_user_fk FOREIGN KEY (user_id) references sec."user" (id),
    CONSTRAINT account_region_fk FOREIGN KEY (region_id) references sec.region (id),
    CONSTRAINT account_department_fk FOREIGN KEY (department_id) references sec.department (id),
    CONSTRAINT account_organization_fk FOREIGN KEY (organization_id) references sec.organization (id)
);

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