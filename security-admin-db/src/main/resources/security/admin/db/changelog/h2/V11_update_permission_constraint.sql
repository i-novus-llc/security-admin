ALTER TABLE sec.permission ADD CONSTRAINT permission_pkey PRIMARY KEY (code);

ALTER TABLE sec.permission ADD CONSTRAINT permission_parent_fk FOREIGN KEY ( parent_code )
  REFERENCES sec.permission( code );

ALTER TABLE sec.role_permission ADD CONSTRAINT role_permission_pk PRIMARY KEY (role_id,permission_code);

ALTER TABLE sec.role_permission ADD CONSTRAINT role_permission_permission_fk FOREIGN KEY ( permission_code )
  REFERENCES sec.permission( code );