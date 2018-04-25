select count(*)
from sec.user_role
where user_id = :id and role_id in (:roleIds);