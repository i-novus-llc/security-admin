select r.id, r.name, r.code, r.description,
(select array_agg(p.name order by p.code)
                from sec.role_permission
                rp join sec.permission p on p.code=rp.permission_code where rp.role_id=r.id) as names,
(select array_agg(p.code order by p.code)
                from sec.role_permission
                rp join sec.permission p on p.code=rp.permission_code where rp.role_id=r.id) as codes
from sec.role r
where (:name is null or (trim(lower(name))) like (lower('%'||trim(:name)||'%'))) and
(:description is null or (trim(lower(description))) like (lower('%'||trim(:description)||'%')))
and (:permissionCode is null or exists (select ur1.permission_code
                        from sec.role_permission ur1
                        where role_id = r.id and permission_code in(:permissionCode)))
                        order by
                            case when :sorting = 'id' then r.id end asc,
                            case when :direction = 'DESC' and :sorting = 'name' then r.name end desc,
                            case when :direction = 'ASC' and :sorting = 'name' then r.name end asc,
                            case when :direction = 'DESC' and :sorting = 'description' then r.description end desc,
                            case when :direction = 'ASC' and :sorting = 'description' then r.description end asc
limit :limit offset :offset;