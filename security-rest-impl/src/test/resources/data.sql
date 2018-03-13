
INSERT INTO sec.permission (id, name, code)VALUES (1, 'test','test');

--for search
--ROLE
INSERT INTO sec.role(id, name, code, description) VALUES (1, 'user','code1','description1');
INSERT INTO sec.role(id, name, code, description) VALUES (2, 'admin','code2','description2');
--USER
INSERT INTO sec.user(id,username,email,name,surname,patronymic,password, is_active) VALUES (1,'user2','UserEmail','userName2','userSurname1','userPatronymic1','userPassword',true)
INSERT INTO sec.user(id,username,email,name,surname,patronymic,password, is_active) VALUES (2,'user2','UserEmail','userName2','userSurname2','userPatronymic2','userPassword',true)
INSERT INTO sec.user(id,username,email,name,surname,patronymic,password, is_active) VALUES (3,'user3','UserEmail','userName3','userSurname3','userPatronymic3','userPassword3',true)
INSERT INTO sec.user(id,username,email,name,surname,patronymic,password, is_active) VALUES (4,'user4','UserEmail','userName4','userSurname4','userPatronymic4','userPassword4',false)





