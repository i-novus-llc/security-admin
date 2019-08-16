ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.user.table}
    ADD CONSTRAINT user_region_fk FOREIGN KEY (region_id) REFERENCES ${n2o.security.admin.schema}.region (id);

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.user.table}
    ADD CONSTRAINT user_organization_fk FOREIGN KEY (organization_id) REFERENCES ${n2o.security.admin.schema}.organization (id);

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.user.table}
    ADD CONSTRAINT user_department_fk FOREIGN KEY (department_id) REFERENCES ${n2o.security.admin.schema}.department (id);