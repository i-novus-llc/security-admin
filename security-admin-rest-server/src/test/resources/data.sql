
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

-- department
INSERT INTO sec.department (code, name)
VALUES ('testDepartment1', 'testDepartment1');
INSERT INTO sec.department (code, name)
VALUES ('testDepartment2', 'testDepartment2');

-- organization
INSERT INTO sec.organization (code, short_name, full_name, ogrn, okpo, inn, kpp, legal_address, email)
VALUES ('testOrganization1', 'testOrganizationShortName1', 'testOrganizationFullName1', '123', '456', '789', '012', 'testOrganizationLegalAddress1', 'testOrganizationEmail1');
INSERT INTO sec.organization (code, short_name, full_name, ogrn, okpo, inn, kpp, legal_address, email)
VALUES ('testOrganization2', 'testOrganizationShortName2', 'testOrganizationFullName2', '123', '456', '789', '012', 'testOrganizationLegalAddress2', 'testOrganizationEmail2');

INSERT INTO sec.org_category (code, name, description)
VALUES ('testCategory1', 'testCategoryName1', 'testCategoryDescription1');
INSERT INTO sec.org_category (code, name, description)
VALUES ('testCategory2', 'testCategoryName2', 'testCategoryDescription2');

INSERT INTO sec.assigned_org_category (org_id, org_category_id)
SELECT t1.id, t2.id FROM  (select id from sec.organization where code = 'testOrganization1') t1 , (select id from sec.org_category where code = 'testCategory2') t2;

INSERT INTO sec.role(name, code, description) VALUES ('testOrgRole','testOrgRole','testOrgRole');
INSERT INTO sec.org_role (org_id, role_id)
SELECT t1.id, t2.id FROM  (select id from sec.organization where code = 'testOrganization1') t1 , (select id from sec.role where code = 'testOrgRole') t2;


-- systems
INSERT INTO sec.system(code, name, description, short_name, icon, url, public_access, view_order, show_on_interface)
VALUES ('testSystemCode1','testSystemName1','testSystemDesc1', 'testSystemShortName1', 'testSystemIcon1', 'testSystemUrl1', true, 1, true);
INSERT INTO sec.system(code, name, description, short_name, icon, url, public_access, view_order, show_on_interface)
VALUES ('testSystemCode2','testSystemName2','testSystemDesc2', 'testSystemShortName2', 'testSystemIcon2', 'testSystemUrl2', false, 2, false);

-- applications
INSERT INTO sec.application(code, name, system_code) VALUES ('testApplicationCode1', 'testApplicationName1', 'testSystemCode1');
INSERT INTO sec.application(code, name, system_code) VALUES ('testApplicationCode2', 'testApplicationName2', 'testSystemCode1');
