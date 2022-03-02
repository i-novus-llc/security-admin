ALTER TABLE sec.account
DROP CONSTRAINT account_region_fk,
ADD CONSTRAINT account_region_fk FOREIGN KEY (region_id)
    REFERENCES sec.region(id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE sec.account
DROP CONSTRAINT account_department_fk,
ADD CONSTRAINT account_department_fk FOREIGN KEY (department_id)
    REFERENCES sec.department(id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE sec.account
DROP CONSTRAINT account_organization_fk,
ADD CONSTRAINT account_organization_fk FOREIGN KEY (organization_id)
    REFERENCES sec.organization(id) ON UPDATE NO ACTION ON DELETE NO ACTION;
