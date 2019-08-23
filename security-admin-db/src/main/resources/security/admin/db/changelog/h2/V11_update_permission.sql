ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table} ADD COLUMN permission_code VARCHAR(100);

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} ADD COLUMN parent_code VARCHAR(100);

UPDATE ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table} rp SET permission_code =(
    SELECT perm.code FROM ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} AS perm
    WHERE perm.id = rp.${n2o.security.admin.rolepermission.column.permission}
);

UPDATE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} AS p_main SET parent_code = (
	SELECT pp_code FROM (
		SELECT pp.id,pp.code as pp_code,pe.id as pe_id,pe.parent_id
											   FROM sec.permission pe join sec.permission pp on (pp.id = pe.parent_id)
	) AS s1 WHERE p_main.id = pe_id
);

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} DROP COLUMN id;

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.permission.table} DROP COLUMN parent_id;

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table} DROP COLUMN ${n2o.security.admin.rolepermission.column.permission};

ALTER TABLE ${n2o.security.admin.schema}.${n2o.security.admin.rolepermission.table} ALTER COLUMN permission_code SET NOT NULL;