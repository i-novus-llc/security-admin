ALTER TABLE sec.organization ADD COLUMN inn VARCHAR(12);
ALTER TABLE sec.organization ADD COLUMN kpp VARCHAR(9);
ALTER TABLE sec.organization ADD COLUMN legal_address VARCHAR(500);
ALTER TABLE sec.organization ADD COLUMN email VARCHAR(50);

COMMENT ON COLUMN sec.organization.inn IS 'ИНН организации или индивидуального предпринимателя';
COMMENT ON COLUMN sec.organization.kpp IS 'КПП организации';
COMMENT ON COLUMN sec.organization.legal_address IS 'Юридический адрес организации или индивидуального предпринимателя';
COMMENT ON COLUMN sec.organization.email IS 'Электронный адрес';


CREATE TABLE sec.org_category
(
    id SERIAL,
    code character varying(50) NOT NULL,
    name character varying(256),
    description character varying(500),
    is_deleted boolean,
    CONSTRAINT org_category_pkey PRIMARY KEY (id)
);

COMMENT ON COLUMN sec.org_category.id IS 'Идентификатор категории организации';
COMMENT ON COLUMN sec.org_category.code IS 'Код категории организации';
COMMENT ON COLUMN sec.org_category.name IS 'Наименование категории организации';
COMMENT ON COLUMN sec.org_category.description IS 'Краткое описание или расшифровка категории';
COMMENT ON COLUMN sec.org_category.is_deleted IS 'Признак удаленной записи';

CREATE TABLE sec.assigned_org_category
(
    org_id INTEGER,
    org_category_id INTEGER,

    CONSTRAINT assigned_org_category_pk PRIMARY KEY (org_id, org_category_id),

    CONSTRAINT assigned_org_category_org_fk FOREIGN KEY (org_id)
        REFERENCES sec.organization (id)
        ON UPDATE RESTRICT
        ON DELETE CASCADE,

    CONSTRAINT assigned_org_category_org_category_fk FOREIGN KEY (org_category_id)
        REFERENCES sec.org_category (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

COMMENT ON COLUMN sec.assigned_org_category.org_id IS 'Идентификатор организации';
COMMENT ON COLUMN sec.assigned_org_category.org_category_id IS 'Идентификатор категории';
