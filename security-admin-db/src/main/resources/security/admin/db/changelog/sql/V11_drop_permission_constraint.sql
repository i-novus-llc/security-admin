ALTER TABLE sec.role_permission DROP CONSTRAINT role_permission_permission_fk;

ALTER TABLE sec.role_permission DROP CONSTRAINT role_permission_pk;

ALTER TABLE sec.permission DROP CONSTRAINT permission_parent_fk;

ALTER TABLE sec.permission DROP CONSTRAINT permission_pkey;