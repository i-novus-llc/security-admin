insert into sec.permission(name, code) values('Банки', 'sec.admin.bank');
insert into sec.permission(name, code, parent_id) values('Просмотр сведений о банке', 'sec.admin.bank.read', (select id from sec.permission where code='sec.admin.bank'));
insert into sec.permission(name, code, parent_id) values('Добавление, редактирование и удаление сведений о банке', 'sec.admin.bank.edit', (select id from sec.permission where code='sec.admin.bank'));


insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.bank.read'),(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.bank.edit'),(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.bank'),(select id from sec.role where code='sec.admin'));
