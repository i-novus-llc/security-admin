
INSERT INTO sec.permission (name, code) VALUES ('test','test');
INSERT INTO sec.permission (name, code, parent_code) VALUES ('test2','test2','test');
--for search
--ROLE
INSERT INTO sec.role(id, name, code, description) VALUES (100, 'test','test','test');
INSERT INTO sec.role(name, code, description) VALUES ('user','code1','description1');
INSERT INTO sec.role(name, code, description) VALUES ('admin','code2','description2');
--USER
INSERT INTO sec.user(username,email,name,surname,patronymic,password, is_active) VALUES ('test', 'test@example.com','name1','surname1','patronymic1','password1',true);
INSERT INTO sec.user(username,email,name,surname,patronymic,password, is_active) VALUES ('test2', 'test@example.com','name1','surname1','patronymic1','password1',true);
INSERT INTO sec.user_role(user_id, role_id) VALUES (1,1);
INSERT INTO sec.user_role(user_id, role_id) VALUES (2,1);
INSERT INTO sec.user_role(user_id, role_id) VALUES (1,2);
INSERT INTO sec.role_permission (role_id, permission_code) VALUES (1,'test');
INSERT INTO sec.role_permission (role_id, permission_code) VALUES (1,'test2');

--for testing loaders
INSERT INTO sec.system(code, name) VALUES ('system1', 'system1');
INSERT INTO sec.system(code, name) VALUES ('system2', 'system2');
INSERT INTO sec.permission (code, name, parent_code, user_level, system_code) VALUES ('pcode1', 'name1', null, 'FEDERAL', 'system1');
INSERT INTO sec.permission (code, name, parent_code, user_level, system_code) VALUES ('pcode3', 'name3', null, 'ORGANIZATION', 'system1');




