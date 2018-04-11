insert into sec.permission(name, code) values('Пользователи', 'user');
insert into sec.permission(name, code, parent_id) values('Просмотр пользователей', 'user.read', (select id from sec.permission where code='user'));
insert into sec.permission(name, code, parent_id) values('Добавление, редактирование и удаление пользователей', 'user.edit', (select id from sec.permission where code='user'));

insert into sec.permission(name, code) values('Роли', 'role');
insert into sec.permission(name, code, parent_id) values('Просмотр ролей', 'role.read', (select id from sec.permission where code='role'));
insert into sec.permission(name, code, parent_id) values('Добавление, редактирование и удаление ролей', 'role.edit', (select id from sec.permission where code='role'));

insert into sec.role(name, code, description) values('Администратор', 'admin', 'admin role');
insert into sec.role(name, code, description) values('Пользователь', 'user', 'user role');

insert into sec.user(username, password, email, surname, name, patronymic, is_active) values('admin', '$2a$10$PnjcVUoDapqovfooYZXnnO6zZ9NQbJIpnArhe0zKbPHjoHPFyVAHS', 'sec.admin@gmail.com', 'Админов', 'Админ', 'Админович', true);

insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='user.read'),(select id from sec.role where code='admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='user.edit'),(select id from sec.role where code='admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='user'),(select id from sec.role where code='admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='role.read'),(select id from sec.role where code='admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='role.edit'),(select id from sec.role where code='admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='role'),(select id from sec.role where code='admin'));

insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='user.read'),(select id from sec.role where code='user'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='role.read'),(select id from sec.role where code='user'));


insert into sec.user_role(user_id, role_id) values((select id from sec.user where username='admin'),(select id from sec.role where code='admin'));
insert into sec.user_role(user_id, role_id) values((select id from sec.user where username='admin'),(select id from sec.role where code='user'));
