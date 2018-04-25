select *
from sec.user u
where (:username is null or username = :username)
and (:fio is null or (trim(lower(u.surname)) like '%'||trim(lower(:fio))||'%')
or(trim(lower(u.name)) like '%'||trim(lower(:fio))||'%' )or(trim(lower(u.patronymic)) like '%'||trim(lower(:fio))||'%')
or (trim(lower((coalesce(u.surname,'')||' '||coalesce(u.name,'')||' '||coalesce(u.patronymic,'')))) like '%'||trim(lower(:fio))||'%'))
and (:isActive is null or is_active = :isActive) and (:password is null or password = :password);