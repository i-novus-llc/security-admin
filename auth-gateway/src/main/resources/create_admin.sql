INSERT INTO sec.system(code, name)
VALUES ('access', 'Единая подсистема прав доступа')
ON CONFLICT DO NOTHING;

INSERT INTO sec.application(code, name, system_code)
VALUES ('admin-web', 'Веб-модуль единой подсистемы прав доступа', 'access')
ON CONFLICT DO NOTHING;

INSERT INTO sec.client(client_id, grant_types, redirect_uris)
VALUES ('admin-web', 'authorization_code,client_credentials', '*')
ON CONFLICT DO NOTHING;

INSERT INTO sec.permission(name, code, system_code)
VALUES ('Пользователи', 'access.admin.user', 'access')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Просмотр пользователей', 'access.admin.user.read', 'access', 'access.admin.user')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Добавление и редактирование пользователей', 'access.admin.user.edit', 'access', 'access.admin.user')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Удаление пользователей', 'access.admin.user.delete', 'access', 'access.admin.user')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Сброс пароля пользователей', 'access.admin.user.resetPassword', 'access', 'access.admin.user')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;

INSERT INTO sec.permission(name, code, system_code)
VALUES ('Роли', 'access.admin.role', 'access')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Просмотр ролей', 'access.admin.role.read', 'access', 'access.admin.role')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Добавление и редактирование ролей', 'access.admin.role.edit', 'access', 'access.admin.role')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Удаление ролей', 'access.admin.role.delete', 'access', 'access.admin.role')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;

INSERT INTO sec.permission(name, code, system_code)
VALUES ('Системы', 'access.admin.system', 'access')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Просмотр систем', 'access.admin.system.read', 'access', 'access.admin.system')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Добавление и редактирование систем', 'access.admin.system.edit', 'access', 'access.admin.system')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Удаление систем', 'access.admin.system.delete', 'access', 'access.admin.system')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Изменение параметров OAuth 2.0', 'access.admin.system.oauth', 'access', 'access.admin.system')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;

INSERT INTO sec.permission(name, code, system_code)
VALUES ('Права доступа', 'access.admin.permission', 'access')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Просмотр прав доступа', 'access.admin.permission.read', 'access', 'access.admin.permission')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Добавление и редактирование прав доступа', 'access.admin.permission.edit', 'access', 'access.admin.permission')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Удаление прав доступа', 'access.admin.permission.delete', 'access', 'access.admin.permission')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;

INSERT INTO sec.permission(name, code, system_code)
VALUES ('Организации', 'access.admin.organization', 'access')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Просмотр списка организаций', 'access.admin.organization.read', 'access', 'access.admin.organization')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Создание и изменение карточки организации', 'access.admin.organization.edit', 'access',
        'access.admin.organization')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Удаление карточки организации', 'access.admin.organization.delete', 'access', 'access.admin.organization')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;

INSERT INTO sec.permission(name, code, system_code)
VALUES ('Типы аккаунтов', 'access.admin.accountType', 'access')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Просмотр списка типов аккаунтов', 'access.admin.accountType.read', 'access', 'access.admin.accountType')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Создание и изменение типа аккаунта', 'access.admin.accountType.edit', 'access', 'access.admin.accountType')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;
INSERT INTO sec.permission(name, code, system_code, parent_code)
VALUES ('Удаление типа аккаунта', 'access.admin.accountType.delete', 'access', 'access.admin.accountType')
ON CONFLICT ON CONSTRAINT permission_pkey DO NOTHING;

INSERT INTO sec.role(name, code, system_code)
SELECT 'Администратор ЕПБА', 'access.admin', 'access'
WHERE NOT EXISTS(SELECT code FROM sec.role r WHERE r.code = 'access.admin');

INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.user.read', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.user.edit', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.user.delete', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.user.resetPassword', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.user', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.role.read', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.role.edit', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.role.delete', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.role', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.system.read', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.system.edit', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.system.delete', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.system.oauth', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.system', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.permission.read', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.permission.edit', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.permission.delete', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.permission', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.organization', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.organization.read', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.organization.edit', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.organization.delete', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.accountType', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.accountType.read', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.accountType.edit', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;
INSERT INTO sec.role_permission(permission_code, role_id)
VALUES ('access.admin.accountType.delete', (SELECT id FROM sec.role WHERE code = 'access.admin'))
ON CONFLICT ON CONSTRAINT role_permission_pk DO NOTHING;

