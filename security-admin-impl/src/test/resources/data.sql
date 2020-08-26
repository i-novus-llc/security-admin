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
INSERT INTO sec.user(username, email, name, surname, patronymic, password, is_active, user_level, last_action_date)
            VALUES ('test', 'test@example.com', 'name1', 'surname1', 'patronymic1', 'password1', TRUE, 'FEDERAL', '2012-08-26 08:27:48.52884');
INSERT INTO sec.user(username, email, name, surname, patronymic, password, is_active, user_level, last_action_date)
            VALUES ('test2', 'test@example.com', 'name1', 'surname1', 'patronymic1', 'password1', TRUE, 'ORGANIZATION', '2020-08-26 08:27:48.52884');
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

INSERT INTO sec.organization(code, short_name, full_name, ogrn, okpo, legal_address, email, inn)
        VALUES ('test_code', 'test_short_name', 'test_full_name' ,'test_ogrn', 'test_okpo', 'test_legal_address', 'test_email', 'test_inn'),
               ('test_code2', 'test_short_name2' , 'test_full_name2' ,'test_ogrn2', 'test_okpo2', 'test_legal_address2', 'test_email2', 'test_inn2'),
               ('test_code3', 'test_short_name3', 'test_full_name3', 'test_ogrn3', 'test_okpo3', 'test_legal_address3', 'test_email3', 'test_inn3'),
               ('test_code4', 'test_short_name4', 'test_full_name4', 'test_ogrn4', 'test_okpo4', 'test_legal_address4', 'test_email4', 'test_inn4'),
               ('test_code5', 'test_short_name5', 'test_full_name5', 'test_ogrn5', 'test_okpo5', 'test_legal_address5', 'test_email5', 'test_inn5'),
               ('test_code6', 'test_short_name6', 'test_full_name6', 'test_ogrn6', 'test_okpo6', 'test_legal_address6', 'test_email6', 'test_inn6');


--for testing account_type
INSERT INTO sec.account_type (code, name, description, user_level, status) VALUES ('testAccountTypeCode', 'testAccountTypeName', 'testDescription', 'PERSONAL', 'REGISTERED');

INSERT INTO sec.account_type_role (account_type_id, role_id, role_type) VALUES (1, 100, 'ORG_AND_USER_ROLE');

INSERT INTO sec.org_category(id, code, name, description)
	    VALUES (1 ,'test_code', 'test_name', 'test_description'),
	           (2 ,'test_code2', 'test_name2', 'test_description2');

