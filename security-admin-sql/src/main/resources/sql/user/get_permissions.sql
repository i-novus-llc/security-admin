select id, name, code, parent_id from sec.permission as p
	join sec.role_permission as rp on p.id = rp.permission_id
    where (:roleId is null or role_id = :roleId)