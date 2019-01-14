select count(*)
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