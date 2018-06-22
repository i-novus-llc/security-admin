select u.id, u.guid, u.username, u.surname, u.name, u.patronymic, u.email, u.password, u.is_active,
(select array_agg(r.id order by r.id)
from sec.user_role ur join sec.role r on r.id=ur.role_id
where ur.user_id=u.id)  as ids,
(select array_agg(r.name order by r.id)
from sec.user_role ur join sec.role r on r.id=ur.role_id
where ur.user_id=u.id)  as names
from sec.user u
where (:username is null or username = :username)
and (:fio::varchar is null or (trim(lower(u.surname::varchar)) like '%'||trim(lower(:fio))||'%')
or(trim(lower(u.name::varchar)) like '%'||trim(lower(:fio))||'%' )or(trim(lower(u.patronymic::varchar)) like '%'||trim(lower(:fio))||'%')
or (trim(lower((coalesce(u.surname,'')||' '||coalesce(u.name,'')||' '||coalesce(u.patronymic,'')))) like '%'||trim(lower(:fio))||'%'))
and (:isActive::boolean is null or is_active = :isActive) and (:password::varchar is null or password = :password)
and exists (select ur1.role_id
                        from sec.user_role ur1
                        where user_id = u.id and role_id in (:roleIds))
limit :limit offset :offset;