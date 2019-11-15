INSERT INTO sec.permission(name, code) VALUES('Пользователи', 'access.admin.user');
INSERT INTO sec.permission(name, code, parent_code) VALUES('Просмотр пользователей', 'access.admin.user.read', 'access.admin.user');
INSERT INTO sec.permission(name, code, parent_code) VALUES('Добавление, редактирование и удаление пользователей', 'access.admin.user.edit', 'access.admin.user');

INSERT INTO sec.permission(name, code) VALUES('Роли', 'access.admin.role');
INSERT INTO sec.permission(name, code, parent_code) VALUES('Просмотр ролей', 'access.admin.role.read', 'access.admin.role');
INSERT INTO sec.permission(name, code, parent_code) VALUES('Добавление, редактирование и удаление ролей', 'access.admin.role.edit', 'access.admin.role');

INSERT INTO sec.permission(name, code) VALUES('Системы', 'access.admin.system');
INSERT INTO sec.permission(name, code, parent_code) VALUES('Просмотр систем', 'access.admin.system.read', 'access.admin.system');
INSERT INTO sec.permission(name, code, parent_code) VALUES('Добавление, редактирование и удаление систем', 'access.admin.system.edit', 'access.admin.system');

INSERT INTO sec.role(name, code) SELECT 'Администратор прав доступа', 'access.admin' WHERE NOT EXISTS (SELECT code FROM sec.role r WHERE r.code = 'access.admin');

INSERT INTO sec.role_permission(permission_code, role_id) VALUES('access.admin.user.read',(SELECT id FROM sec.role WHERE code='access.admin'));
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('access.admin.user.edit',(SELECT id FROM sec.role WHERE code='access.admin'));
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('access.admin.user',(SELECT id FROM sec.role WHERE code='access.admin'));
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('access.admin.role.read',(SELECT id FROM sec.role WHERE code='access.admin'));
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('access.admin.role.edit',(SELECT id FROM sec.role WHERE code='access.admin'));
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('access.admin.role',(SELECT id FROM sec.role WHERE code='access.admin'));
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('access.admin.system.read',(SELECT id FROM sec.role WHERE code='access.admin'));
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('access.admin.system.edit',(SELECT id FROM sec.role WHERE code='access.admin'));
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('access.admin.system',(SELECT id FROM sec.role WHERE code='access.admin'));

INSERT INTO sec.user(id,username,name,email,is_active,password,ext_uid) VALUES (1,'admin','Администратор','aadmin@test.adm',TRUE,'$2a$10$.aw1JNVIJRFl/BUMX2mDO.O7nRxzsxUK3gxbvLr.Lfgzd6xmvYnfq','2ab6fe13-8f05-4c89-a6c8-bcfb9deeccf1');

INSERT INTO sec.user_role (user_id,role_id) VALUES (1,(SELECT id FROM sec.role WHERE code='access.admin'));