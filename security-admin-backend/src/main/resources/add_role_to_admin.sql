insert into sec.permission(name, code) values('Системы', 'sec.admin.system');
insert into sec.permission(name, code, parent_id) values('Просмотр систем', 'sec.admin.system.read', (select id from sec.permission where code='sec.admin.system'));
insert into sec.permission(name, code, parent_id) values('Добавление, редактирование и удаление систем', 'sec.admin.system.edit', (select id from sec.permission where code='sec.admin.system'));

insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.system.read'),(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.system.edit'),(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.system'),(select id from sec.role where code='sec.admin'));
