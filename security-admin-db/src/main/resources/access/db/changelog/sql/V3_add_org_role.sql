CREATE TABLE sec.org_role
(
    role_id integer NOT NULL,
    org_id integer NOT NULL,
    CONSTRAINT org_role_pk PRIMARY KEY (role_id, org_id),
    CONSTRAINT org_role_org_fk FOREIGN KEY (org_id)
        REFERENCES sec.organization(id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT org_role_role_fk FOREIGN KEY (role_id)
        REFERENCES sec.role (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

COMMENT ON TABLE sec.org_role
    IS 'Таблица связи организации с ролями';

COMMENT ON COLUMN sec.org_role.role_id
    IS 'Идентификатор роли';
COMMENT ON COLUMN sec.org_role.org_id
    IS 'Идентификатор оргпнизации';



CREATE INDEX org_role_org_idx
    ON sec.org_role USING btree
    (org_id);


CREATE INDEX org_role_role_idx
    ON sec.org_role USING btree
    (role_id);
