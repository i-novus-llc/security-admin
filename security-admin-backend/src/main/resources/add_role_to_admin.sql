insert into sec.permission(name, code) values('Системы', 'sec.admin.system');
insert into sec.permission(name, code, parent_code) values('Просмотр систем', 'sec.admin.system.read', 'sec.admin.system');
insert into sec.permission(name, code, parent_code) values('Добавление, редактирование и удаление систем', 'sec.admin.system.edit', 'sec.admin.system');

insert into sec.role_permission(permission_code, role_id) values('sec.admin.system.read',(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_code, role_id) values('sec.admin.system.edit',(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_code, role_id) values('sec.admin.system',(select id from sec.role where code='sec.admin'));
