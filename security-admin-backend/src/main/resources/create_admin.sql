insert into sec.permission(name, code) values('Пользователи', 'sec.admin.user');
insert into sec.permission(name, code, parent_code) values('Просмотр пользователей', 'sec.admin.user.read', 'sec.admin.user');
insert into sec.permission(name, code, parent_code) values('Добавление, редактирование и удаление пользователей', 'sec.admin.user.edit', 'sec.admin.user');

insert into sec.permission(name, code) values('Роли', 'sec.admin.role');
insert into sec.permission(name, code, parent_code) values('Просмотр ролей', 'sec.admin.role.read', 'sec.admin.role');
insert into sec.permission(name, code, parent_code) values('Добавление, редактирование и удаление ролей', 'sec.admin.role.edit', 'sec.admin.role');

insert into sec.role(name, code) values('Администратор прав доступа', 'sec.admin');

insert into sec.role_permission(permission_code, role_id) values('sec.admin.user.read',(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_code, role_id) values('sec.admin.user.edit',(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_code, role_id) values('sec.admin.user',(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_code, role_id) values('sec.admin.role.read',(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_code, role_id) values('sec.admin.role.edit',(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_code, role_id) values('sec.admin.role',(select id from sec.role where code='sec.admin'));