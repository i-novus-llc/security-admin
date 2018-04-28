select count(*)
from sec.role r
where (:name is null or (trim(lower(name))) = (trim(lower(:name)))) and
(:description is null or (trim(lower(description))) = (trim(lower(:description))))
and (:permissionIds is null or exists (select ur1.permission_id
                        from sec.role_permission ur1
                        where role_id = r.id and permission_id in(:permissionIds)));