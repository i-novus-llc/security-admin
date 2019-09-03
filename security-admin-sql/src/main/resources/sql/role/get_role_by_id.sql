select r.id, r.name, r.code, r.description,
(select array_agg(p.name order by p.code)
                from sec.role_permission
                rp join sec.permission p on p.code=rp.permission_code where rp.role_id=r.id) as names,
(select array_agg(p.code order by p.code)
                from sec.role_permission
                rp join sec.permission p on p.code=rp.permission_code where rp.role_id=r.id) as codes
from sec.role r where id = :id;