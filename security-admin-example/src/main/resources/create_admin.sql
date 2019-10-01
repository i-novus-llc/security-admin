INSERT INTO sec.permission(name, code) VALUES('Пользователи', 'sec.admin.user') on conflict on constraint permission_pkey do nothing;
INSERT INTO sec.permission(name, code, parent_code) VALUES('Просмотр пользователей', 'sec.admin.user.read', 'sec.admin.user') on conflict on constraint permission_pkey do nothing;
INSERT INTO sec.permission(name, code, parent_code) VALUES('Добавление, редактирование и удаление пользователей', 'sec.admin.user.edit', 'sec.admin.user') on conflict on constraint permission_pkey do nothing;

INSERT INTO sec.permission(name, code) VALUES('Роли', 'sec.admin.role') on conflict on constraint permission_pkey do nothing;
INSERT INTO sec.permission(name, code, parent_code) VALUES('Просмотр ролей', 'sec.admin.role.read', 'sec.admin.role') on conflict on constraint permission_pkey do nothing;
INSERT INTO sec.permission(name, code, parent_code) VALUES('Добавление, редактирование и удаление ролей', 'sec.admin.role.edit', 'sec.admin.role') on conflict on constraint permission_pkey do nothing;

INSERT INTO sec.permission(name, code) VALUES('Системы', 'sec.admin.system') on conflict on constraint permission_pkey do nothing;
INSERT INTO sec.permission(name, code, parent_code) VALUES('Просмотр систем', 'sec.admin.system.read', 'sec.admin.system') on conflict on constraint permission_pkey do nothing;
INSERT INTO sec.permission(name, code, parent_code) VALUES('Добавление, редактирование и удаление систем', 'sec.admin.system.edit', 'sec.admin.system') on conflict on constraint permission_pkey do nothing;

INSERT INTO sec.role(name, code) SELECT 'Администратор прав доступа', 'sec.admin' WHERE NOT EXISTS (SELECT code FROM sec.role r WHERE r.code = 'sec.admin');

INSERT INTO sec.role_permission(permission_code, role_id) VALUES('sec.admin.user.read',(SELECT id FROM sec.role WHERE code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('sec.admin.user.edit',(SELECT id FROM sec.role WHERE code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('sec.admin.user',(SELECT id FROM sec.role WHERE code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('sec.admin.role.read',(SELECT id FROM sec.role WHERE code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('sec.admin.role.edit',(SELECT id FROM sec.role WHERE code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('sec.admin.role',(SELECT id FROM sec.role WHERE code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('sec.admin.system.read',(SELECT id FROM sec.role WHERE code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('sec.admin.system.edit',(SELECT id FROM sec.role WHERE code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
INSERT INTO sec.role_permission(permission_code, role_id) VALUES('sec.admin.system',(SELECT id FROM sec.role WHERE code='sec.admin')) on conflict on constraint role_permission_pk do nothing;

INSERT INTO sec."user" ("id",username,"name",surname,patronymic,email,is_active,"password",ext_uid) VALUES (1,'admin','Администратор','Администраторов','Администраторович','aadmin@test.adm',TRUE,'$2a$10$.aw1JNVIJRFl/BUMX2mDO.O7nRxzsxUK3gxbvLr.Lfgzd6xmvYnfq','2ab6fe13-8f05-4c89-a6c8-bcfb9deeccf1');

INSERT INTO sec.user_role (user_id,role_id) VALUES (1,(SELECT id FROM sec.role WHERE code='sec.admin'));