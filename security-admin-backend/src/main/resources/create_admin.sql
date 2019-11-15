insert into sec.permission(name, code) values('Пользователи', 'access.admin.user') on conflict on constraint permission_pkey do nothing;
insert into sec.permission(name, code, parent_code) values('Просмотр пользователей', 'access.admin.user.read', 'access.admin.user') on conflict on constraint permission_pkey do nothing;
insert into sec.permission(name, code, parent_code) values('Добавление, редактирование и удаление пользователей', 'access.admin.user.edit', 'access.admin.user') on conflict on constraint permission_pkey do nothing;

insert into sec.permission(name, code) values('Роли', 'access.admin.role') on conflict on constraint permission_pkey do nothing;
insert into sec.permission(name, code, parent_code) values('Просмотр ролей', 'access.admin.role.read', 'access.admin.role') on conflict on constraint permission_pkey do nothing;
insert into sec.permission(name, code, parent_code) values('Добавление, редактирование и удаление ролей', 'access.admin.role.edit', 'access.admin.role') on conflict on constraint permission_pkey do nothing;

insert into sec.permission(name, code) values('Системы', 'access.admin.system') on conflict on constraint permission_pkey do nothing;
insert into sec.permission(name, code, parent_code) values('Просмотр систем', 'access.admin.system.read', 'access.admin.system') on conflict on constraint permission_pkey do nothing;
insert into sec.permission(name, code, parent_code) values('Добавление, редактирование и удаление систем', 'access.admin.system.edit', 'access.admin.system') on conflict on constraint permission_pkey do nothing;

insert into sec.role(name, code) select 'Администратор прав доступа', 'access.admin' where not exists (select code from sec.role r where r.code = 'access.admin');

insert into sec.role_permission(permission_code, role_id) values('access.admin.user.read',(select id from sec.role where code='access.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('access.admin.user.edit',(select id from sec.role where code='access.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('access.admin.user',(select id from sec.role where code='access.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('access.admin.role.read',(select id from sec.role where code='access.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('access.admin.role.edit',(select id from sec.role where code='access.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('access.admin.role',(select id from sec.role where code='access.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('access.admin.system.read',(select id from sec.role where code='access.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('access.admin.system.edit',(select id from sec.role where code='access.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('access.admin.system',(select id from sec.role where code='access.admin')) on conflict on constraint role_permission_pk do nothing;