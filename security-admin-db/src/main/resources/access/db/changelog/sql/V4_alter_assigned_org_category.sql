DROP TABLE IF EXISTS sec.assigned_org_category;

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

COMMENT ON COLUMN sec.assigned_org_category.org_id IS 'Уникальный идентификатор организации';
COMMENT ON COLUMN sec.assigned_org_category.org_category_id IS 'Уникальный идентификатор категории';