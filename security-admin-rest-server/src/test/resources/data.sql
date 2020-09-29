
INSERT INTO sec.permission (name, code, user_level)VALUES ('test','test', 'FEDERAL');
INSERT INTO sec.permission (name, code, parent_code, user_level)VALUES ('test2','test2','test', 'ORGANIZATION');
--for search
--ROLE
INSERT INTO sec.role(id, name, code, description) VALUES (10, 'test','test','test');
INSERT INTO sec.role(name, code, description) VALUES ('user','code1','description1');
INSERT INTO sec.role(name, code, description) VALUES ('admin','code2','description2');
--USER
INSERT INTO sec.user(username,email,name,surname,patronymic,password, is_active) VALUES ('test', 'test@example.com','name1','surname1','patronymic1','password1',true);
INSERT INTO sec.user(username,email,name,surname,patronymic,password, is_active) VALUES ('test2', 'test@example.com','name1','surname2','patronymic1','password1',true);
INSERT INTO sec.user(username,email,name,surname,patronymic,password, is_active) VALUES ('test3', 'test@example.com','name3','surname3','patronymic3','password3',true);
INSERT INTO sec.user_role(user_id, role_id) VALUES (1,1);
INSERT INTO sec.user_role(user_id, role_id) VALUES (1,2);
INSERT INTO sec.role_permission (role_id, permission_code) VALUES (1,'test');
INSERT INTO sec.role_permission (role_id, permission_code) VALUES (1,'test2');

-- account_type
INSERT INTO sec.account_type (code, name, description, user_level, status)
VALUES ('testAccountType1', 'testName1', 'testDesc1', 'NOT_SET', 'REGISTERED');
INSERT INTO sec.account_type (code, name, description, user_level, status)
VALUES ('testAccountType2', 'testName2', 'testDesc2', 'FEDERAL', 'AWAITING_MODERATION');




