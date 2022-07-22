ALTER TABLE sec."user"
    ADD COLUMN region_id INTEGER;

ALTER TABLE sec."user"
    ADD CONSTRAINT user_region_id_fk FOREIGN KEY (region_id) REFERENCES sec."region" (id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;