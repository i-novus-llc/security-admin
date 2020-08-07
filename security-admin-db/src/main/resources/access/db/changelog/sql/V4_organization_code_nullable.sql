ALTER TABLE sec.organization
    ALTER COLUMN code DROP NOT NULL;

ALTER TABLE sec.organization
    DROP CONSTRAINT organization_code_unq;