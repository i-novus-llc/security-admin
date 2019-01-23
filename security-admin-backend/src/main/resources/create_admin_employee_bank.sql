insert into sec.permission(name, code) values('Сотрудники банка', 'sec.admin.employeeBank');
insert into sec.permission(name, code, parent_id) values('Просмотр сведений о сотруднике банка', 'sec.admin.employeeBank.read', (select id from sec.permission where code='sec.admin.employeeBank'));
insert into sec.permission(name, code, parent_id) values('Добавление, редактирование и удаление сотрудника банка', 'sec.admin.employeeBank.edit', (select id from sec.permission where code='sec.admin.employeeBank'));


insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.employeeBank.read'),(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.employeeBank.edit'),(select id from sec.role where code='sec.admin'));
insert into sec.role_permission(permission_id, role_id) values((select id from sec.permission where code='sec.admin.employeeBank'),(select id from sec.role where code='sec.admin'));
