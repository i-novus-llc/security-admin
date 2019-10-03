insert into sec.permission(name, code) values('Пользователи', 'sec.admin.user') on conflict on constraint permission_pkey do nothing;
insert into sec.permission(name, code, parent_code) values('Просмотр пользователей', 'sec.admin.user.read', 'sec.admin.user') on conflict on constraint permission_pkey do nothing;
insert into sec.permission(name, code, parent_code) values('Добавление, редактирование и удаление пользователей', 'sec.admin.user.edit', 'sec.admin.user') on conflict on constraint permission_pkey do nothing;

insert into sec.permission(name, code) values('Роли', 'sec.admin.role') on conflict on constraint permission_pkey do nothing;
insert into sec.permission(name, code, parent_code) values('Просмотр ролей', 'sec.admin.role.read', 'sec.admin.role') on conflict on constraint permission_pkey do nothing;
insert into sec.permission(name, code, parent_code) values('Добавление, редактирование и удаление ролей', 'sec.admin.role.edit', 'sec.admin.role') on conflict on constraint permission_pkey do nothing;

insert into sec.permission(name, code) values('Системы', 'sec.admin.system') on conflict on constraint permission_pkey do nothing;
insert into sec.permission(name, code, parent_code) values('Просмотр систем', 'sec.admin.system.read', 'sec.admin.system') on conflict on constraint permission_pkey do nothing;
insert into sec.permission(name, code, parent_code) values('Добавление, редактирование и удаление систем', 'sec.admin.system.edit', 'sec.admin.system') on conflict on constraint permission_pkey do nothing;

insert into sec.role(name, code) select 'Администратор прав доступа', 'sec.admin' where not exists (select code from sec.role r where r.code = 'sec.admin');

insert into sec.role_permission(permission_code, role_id) values('sec.admin.user.read',(select id from sec.role where code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('sec.admin.user.edit',(select id from sec.role where code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('sec.admin.user',(select id from sec.role where code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('sec.admin.role.read',(select id from sec.role where code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('sec.admin.role.edit',(select id from sec.role where code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('sec.admin.role',(select id from sec.role where code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('sec.admin.system.read',(select id from sec.role where code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('sec.admin.system.edit',(select id from sec.role where code='sec.admin')) on conflict on constraint role_permission_pk do nothing;
insert into sec.role_permission(permission_code, role_id) values('sec.admin.system',(select id from sec.role where code='sec.admin')) on conflict on constraint role_permission_pk do nothing;