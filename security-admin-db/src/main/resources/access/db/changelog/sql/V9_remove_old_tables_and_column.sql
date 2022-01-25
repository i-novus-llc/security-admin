ALTER TABLE sec."user"
    DROP COLUMN region_id;
ALTER TABLE sec."user"
    DROP COLUMN department_id;
ALTER TABLE sec."user"
    DROP COLUMN organization_id;
ALTER TABLE sec."user"
    DROP COLUMN user_level;
ALTER TABLE sec."user"
    DROP COLUMN ext_uid;
ALTER TABLE sec."user"
    DROP COLUMN ext_sys;

DROP TABLE sec.user_role;