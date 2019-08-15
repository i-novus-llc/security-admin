CREATE TABLE IF NOT EXISTS ${n2o.security.admin.schema}.${n2o.security.admin.client.table} (
  ${n2o.security.admin.client.id} VARCHAR(255) PRIMARY KEY,
  ${n2o.security.admin.client.secret} VARCHAR(255),
  ${n2o.security.admin.client.types} VARCHAR(255),
  ${n2o.security.admin.client.redirect} VARCHAR(1000),
  ${n2o.security.admin.client.token.time} INTEGER
);


COMMENT ON TABLE ${n2o.security.admin.schema}.${n2o.security.admin.client.table} IS 'Клиенты';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.client.table}.${n2o.security.admin.client.id} IS 'Имя клиента';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.client.table}.${n2o.security.admin.client.secret}  IS 'Секрет клиента';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.client.table}.${n2o.security.admin.client.types} IS 'Тип авторизации ';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.client.table}.${n2o.security.admin.client.redirect} IS 'URI разрешённые для редиректа';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.client.table}.${n2o.security.admin.client.token.time} IS 'Время жизни токена';
