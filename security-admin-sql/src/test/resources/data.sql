INSERT INTO sec.permission (id, name, code)VALUES (1, 'test','test');
INSERT INTO sec.permission (id, name, code, parent_id)VALUES (2, 'test2','test2',1);
--for search
--ROLE
INSERT INTO sec.role(id, name, code, description) VALUES (1, 'user','code1','description1');
INSERT INTO sec.role(id, name, code, description) VALUES (2, 'admin','code2','description2');
--USER
INSERT INTO sec.user(id,username,email,name,surname,patronymic,password, is_active) VALUES (1, 'test', 'test@example.com','name1','surname1','patronymic1','password1',true);
INSERT INTO sec.user(id,username,email,name,surname,patronymic,password, is_active) VALUES (2, 'test2', 'test@example.com','name1','surname1','patronymic1','password1',true);
INSERT INTO sec.user_role(user_id, role_id) VALUES (1,1);
INSERT INTO sec.user_role(user_id, role_id) VALUES (1,2);
INSERT INTO sec.role_permission (role_id, permission_id) VALUES (1,1);
INSERT INTO sec.role_permission (role_id, permission_id) VALUES (1,2);



