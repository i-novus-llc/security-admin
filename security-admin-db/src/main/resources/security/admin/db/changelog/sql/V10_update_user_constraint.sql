ALTER TABLE sec.user
    ADD CONSTRAINT user_region_fk FOREIGN KEY (region_id) REFERENCES sec.region (id);

ALTER TABLE sec.user
    ADD CONSTRAINT user_organization_fk FOREIGN KEY (organization_id) REFERENCES sec.organization (id);

ALTER TABLE sec.user
    ADD CONSTRAINT user_department_fk FOREIGN KEY (department_id) REFERENCES sec.department (id);