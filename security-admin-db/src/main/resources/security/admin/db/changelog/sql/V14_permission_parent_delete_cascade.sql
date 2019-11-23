ALTER TABLE sec.permission
    DROP CONSTRAINT permission_parent_fk;
ALTER TABLE sec.permission
    ADD CONSTRAINT permission_parent_fk FOREIGN KEY ( parent_code )
    REFERENCES sec.permission( code ) ON DELETE CASCADE ON UPDATE RESTRICT;
