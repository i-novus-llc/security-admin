ALTER TABLE sec.organization ADD COLUMN inn VARCHAR(12);
ALTER TABLE sec.organization ADD COLUMN kpp VARCHAR(9);
ALTER TABLE sec.organization ADD COLUMN legal_address VARCHAR(500);
ALTER TABLE sec.organization ADD COLUMN email VARCHAR(50);
ALTER TABLE sec.organization ADD CONSTRAINT organization_code_unq UNIQUE (code);

COMMENT ON COLUMN sec.organization.inn IS 'ИНН организации или индивидуального предпринимателя';
COMMENT ON COLUMN sec.organization.kpp IS 'КПП организации';
COMMENT ON COLUMN sec.organization.legal_address IS 'Юридический адрес организации или индивидуального предпринимателя';
COMMENT ON COLUMN sec.organization.email IS 'Электронный адрес';

CREATE TABLE sec.org_category
(
    id SERIAL,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(256),
    description VARCHAR(500),
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
    org_code VARCHAR(50),
    org_category_code VARCHAR(50),

    CONSTRAINT assigned_org_category_pk PRIMARY KEY (org_code, org_category_code),

    CONSTRAINT assigned_org_category_org_fk FOREIGN KEY (org_code)
        REFERENCES sec.organization (code)
        ON UPDATE RESTRICT
        ON DELETE CASCADE,

    CONSTRAINT assigned_org_category_org_category_fk FOREIGN KEY (org_category_code)
        REFERENCES sec.org_category (code)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

COMMENT ON COLUMN sec.assigned_org_category.org_code IS 'Код организации';
COMMENT ON COLUMN sec.assigned_org_category.org_category_code IS 'Код категории';
