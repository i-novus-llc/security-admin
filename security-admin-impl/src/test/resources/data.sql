INSERT INTO sec.system(code, name) VALUES ('system1', 'system1');
INSERT INTO sec.system(code, name) VALUES ('system2', 'system2');

INSERT INTO sec.permission (name, code, system_code) VALUES ('test','test', 'system1');
INSERT INTO sec.permission (name, code, parent_code, system_code) VALUES ('test2','test2','test', 'system1');
--for search
--ROLE
INSERT INTO sec.role(id, name, code, description) VALUES (100, 'test','test','test');
INSERT INTO sec.role(name, code, description) VALUES ('user','code1','description1');
INSERT INTO sec.role(name, code, description) VALUES ('admin','code2','description2');
--USER
INSERT INTO sec.user(username, email, name, surname, patronymic, password, is_active, user_level)
            VALUES ('test', 'test@example.com', 'name1', 'surname1', 'patronymic1', 'password1', true, 'FEDERAL');
INSERT INTO sec.user(username, email, name, surname, patronymic, password, is_active, user_level)
            VALUES ('test2', 'test@example.com', 'name1', 'surname1', 'patronymic1', 'password1', true, 'ORGANIZATION');
INSERT INTO sec.user_role(user_id, role_id) VALUES (1,1);
INSERT INTO sec.user_role(user_id, role_id) VALUES (2,1);
INSERT INTO sec.user_role(user_id, role_id) VALUES (1,2);
INSERT INTO sec.role_permission (role_id, permission_code) VALUES (1,'test');
INSERT INTO sec.role_permission (role_id, permission_code) VALUES (1,'test2');

--for testing loaders

INSERT INTO sec.permission (code, name, parent_code, user_level) VALUES ('test-code1', 'name1', NULL, 'FEDERAL');
INSERT INTO sec.permission (code, name, parent_code, user_level) VALUES ('test-code2', 'name2', NULL, 'ORGANIZATION');
INSERT INTO sec.role(id, code, name, description, user_level) VALUES (101, '101', 'name1', 'desc1', 'FEDERAL');
INSERT INTO sec.role(id, code, name, description, user_level) VALUES (102, '102', 'name2', 'desc2', 'REGIONAL');
INSERT INTO sec.role_permission(role_id, permission_code) VALUES (101, 'test-code1');
INSERT INTO sec.role_permission(role_id, permission_code) VALUES (102, 'test-code2');


--for testing account_type
INSERT INTO sec.account_type (code, name, description, user_level, status) VALUES ('testAccountTypeCode', 'testAccountTypeName', 'testDescription', 'PERSONAL', false);



