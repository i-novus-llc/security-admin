ALTER TABLE sec.permission ADD COLUMN parent_code VARCHAR(100);

ALTER TABLE sec.role_permission ADD COLUMN permission_code VARCHAR(100);

UPDATE sec.role_permission rp SET permission_code =(
    SELECT perm.code FROM sec.permission AS perm
    WHERE perm.id = rp.permission_id
);

UPDATE sec.permission AS p_main SET parent_code = (
	SELECT pp_code FROM (
		SELECT pp.id,pp.code as pp_code,pe.id as pe_id,pe.parent_id
											   FROM sec.permission pe join sec.permission pp on (pp.id = pe.parent_id)
	) AS s1 WHERE p_main.id = pe_id
);

ALTER TABLE sec.permission DROP COLUMN id;

ALTER TABLE sec.permission DROP COLUMN parent_id;

ALTER TABLE sec.role_permission DROP COLUMN permission_id;

ALTER TABLE sec.role_permission ALTER COLUMN permission_code SET NOT NULL;