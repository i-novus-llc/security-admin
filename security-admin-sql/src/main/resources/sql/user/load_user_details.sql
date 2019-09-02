select * from sec.user
    where (:username is null or username = :username)
    and (:email is null or email = :email)
    and (:surname is null or surname = :surname)
    and (:name is null or name = :name)
    and (:extUid is null or ext_uid = :extUid)
