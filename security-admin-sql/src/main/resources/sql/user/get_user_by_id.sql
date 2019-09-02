select u.id, u.ext_uid, u.username, u.surname, u.name, u.patronymic, u.email, u.password, u.is_active,
(select array_agg(r.id order by r.id)
from sec.user_role ur join sec.role r on r.id=ur.role_id
where ur.user_id=u.id)  as ids,
(select array_agg(r.name order by r.id)
from sec.user_role ur join sec.role r on r.id=ur.role_id
where ur.user_id=u.id)  as names
from sec.user u where id = :id;