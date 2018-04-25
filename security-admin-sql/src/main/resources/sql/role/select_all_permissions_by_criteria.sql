select count(*)
from sec.role_permission
where role_id = :id and permission_id in (:permissionIds);