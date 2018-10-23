select count(*)
from sec.role r
where (:name is null or (trim(lower(name))) like (lower('%'||trim(:name)||'%'))) and
(:description is null or (trim(lower(description))) like (lower('%'||trim(:description)||'%')))
and (:permissionIds is null or exists (select ur1.permission_id
                        from sec.role_permission ur1
                        where role_id = r.id and permission_id in(:permissionIds)))