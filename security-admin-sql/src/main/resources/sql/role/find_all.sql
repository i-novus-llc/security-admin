select r.id, r.name, r.code, r.description,
(select array_agg(p.id order by p.id)
                from sec.role_permission
                rp join sec.permission p on p.id=rp.permission_id where rp.role_id=r.id) as ids,
(select array_agg(p.name order by p.id)
                from sec.role_permission
                rp join sec.permission p on p.id=rp.permission_id where rp.role_id=r.id) as names,
(select array_agg(p.code order by p.id)
                from sec.role_permission
                rp join sec.permission p on p.id=rp.permission_id where rp.role_id=r.id) as codes
from sec.role r
where (:name is null or (trim(lower(name))) = (trim(lower(:name)))) and
(:description is null or (trim(lower(description))) = (trim(lower(:description))))
and (:permissionIds is null or exists (select ur1.permission_id
                        from sec.role_permission ur1
                        where role_id = r.id and permission_id in(:permissionIds)))
limit :limit offset :offset;