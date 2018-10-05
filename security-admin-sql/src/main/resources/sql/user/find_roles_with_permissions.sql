select
	r.id as r_id,
	r.name as r_name,
	r.code as r_code,
	r.description as r_description,
	p.id as p_id,
	p.name as p_name,
	p.code as p_code,
	p.parent_id as p_parent_id
		from sec.permission as p
			join sec.role_permission as rp on rp.permission_id = p.id
			right join sec.role as r on r.id = rp.role_id
			join sec.user_role as ur on ur.role_id = r.id
			    where (:userId is null or ur.user_id = :userId)
			    order by r.id