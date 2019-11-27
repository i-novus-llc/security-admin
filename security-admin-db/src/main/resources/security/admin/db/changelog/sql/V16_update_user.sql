ALTER TABLE  sec.user ADD COLUMN IF NOT EXISTS role_count INTEGER;

UPDATE sec."user" AS userToUpdate SET role_count = (SELECT count(ur1.role_id) FROM sec."user" AS u1
JOIN sec.user_role AS ur1 ON ur1.user_id=u1.id
WHERE userToUpdate.id = u1.id GROUP BY u1.id )