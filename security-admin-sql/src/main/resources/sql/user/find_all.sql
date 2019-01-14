select u.id, u.guid, u.username, u.surname, u.name, u.patronymic, u.email, u.password, u.is_active,
(select array_agg(r.id order by r.id)
from sec.user_role ur join sec.role r on r.id=ur.role_id
where ur.user_id=u.id)  as ids,
(select array_agg(r.name order by r.id)
from sec.user_role ur join sec.role r on r.id=ur.role_id
where ur.user_id=u.id)  as names
from sec.user u
where (:username is null or username like (lower('%'||trim(:username)||'%')))
and (:bank is null or bank_id = :bank)
and (:fio::varchar is null or (trim(lower(u.surname::varchar)) like lower('%'||trim(:fio)||'%'))
or(trim(lower(u.name::varchar)) like lower('%'||trim(:fio)||'%') )or(trim(lower(u.patronymic::varchar)) like lower('%'||trim(:fio)||'%'))
or (trim(lower((coalesce(u.surname,'')||' '||coalesce(u.name,'')||' '||coalesce(u.patronymic,'')))) like lower('%'||trim(:fio)||'%')))
and (:isActive::boolean is null or is_active = :isActive) and (:password::varchar is null or password = :password)
and exists (select ur1.role_id
                        from sec.user_role ur1
                        where user_id = u.id and role_id in (:roleIds))
                        order by
                            case when :sorting = 'id' then u.id end asc,
                            case when :sorting = 'username' and :direction = 'DESC'  then u.username end desc,
                            case when :sorting = 'username' and :direction = 'ASC'  then u.username end asc,
                            case when :sorting = 'fio' and :direction = 'ASC'  then u.surname end asc,
                            case when :sorting = 'fio' and :direction = 'DESC'  then u.surname end desc
limit :limit offset :offset;