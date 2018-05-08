select count(*)
from sec.user u
where (:username is null or username = :username)
and (:fio::varchar is null or (trim(lower(u.surname)) like '%'||trim(lower(:fio))||'%')
or(trim(lower(u.name)) like '%'||trim(lower(:fio))||'%' )or(trim(lower(u.patronymic)) like '%'||trim(lower(:fio))||'%')
or (trim(lower((coalesce(u.surname,'')||' '||coalesce(u.name,'')||' '||coalesce(u.patronymic,'')))) like '%'||trim(lower(:fio))||'%'))
and (:isActive::boolean is null or is_active = :isActive) and (:password::varchar is null or password = :password)
and exists (select ur1.role_id
                        from sec.user_role ur1
                        where user_id = u.id and role_id in (:roleIds));