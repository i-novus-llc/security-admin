update sec.user set email = :email, password = :password, surname = :surname, name = :name, patronymic = :patronymic, is_active = :isActive, ext_uid = :extUid where id = :id;