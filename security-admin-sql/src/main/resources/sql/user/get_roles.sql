select id, name, code, description from sec.role as r
	join sec.user_role as ur on r.id = ur.role_id
    where (:userId is null or user_id = :userId)