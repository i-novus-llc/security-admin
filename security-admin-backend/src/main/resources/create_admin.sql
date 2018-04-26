insert into sec.permission(name, code) values('Пользователи', 'sec.admin.user');
insert into sec.permission(name, code, parent_id) values('Просмотр пользователей', 'sec.admin.user.read', (select id from sec.permission where code='sec.admin.user'));
insert into sec.permission(name, code, parent_id) values('Добавление, редактирование и удаление пользователей', 'sec.admin.user.edit', (select id from sec.permission where code='sec.admin.user'));

insert into sec.permission(name, code) values('Роли', 'sec.admin.role');
insert into sec.permission(name, code, parent_id) values('Просмотр ролей', 'sec.admin.role.read', (select id from sec.permission where code='sec.admin.role'));
insert into sec.permission(name, code, parent_id) values('Добавление, редактирование и удаление ролей', 'sec.admin.role.edit', (select id from sec.permission where code='sec.admin.role'));

insert into sec.role(name, code) values('Администратор прав доступа', 'sec.admin');

insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.user.read'),(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.user.edit'),(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.user'),(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.role.read'),(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.role.edit'),(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.role'),(select id from sec.role where code='sec.admin'));