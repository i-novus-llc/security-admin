ALTER TABLE sec.account
DROP CONSTRAINT account_region_fk,
ADD CONSTRAINT account_region_fk FOREIGN KEY (region_id)
    REFERENCES sec.region(id) ON DELETE SET NULL;

ALTER TABLE sec.account
DROP CONSTRAINT account_department_fk,
ADD CONSTRAINT account_department_fk FOREIGN KEY (department_id)
    REFERENCES sec.department(id) ON DELETE SET NULL;

ALTER TABLE sec.account
DROP CONSTRAINT account_organization_fk,
ADD CONSTRAINT account_organization_fk FOREIGN KEY (organization_id)
    REFERENCES sec.organization(id) ON DELETE SET NULL;

ALTER TABLE sec.account_role
DROP CONSTRAINT account_role_role_fk,
ADD CONSTRAINT account_role_role_fk FOREIGN KEY (role_id)
    REFERENCES sec.role (id) ON UPDATE CASCADE ON DELETE CASCADE,
DROP CONSTRAINT account_role_account_fk,
ADD CONSTRAINT account_role_account_fk FOREIGN KEY (account_id)
    REFERENCES sec.account (id) ON UPDATE CASCADE ON DELETE CASCADE;
