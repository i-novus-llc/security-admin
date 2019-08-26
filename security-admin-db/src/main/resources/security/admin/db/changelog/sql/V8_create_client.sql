CREATE TABLE IF NOT EXISTS ${n2o.security.admin.schema}.${n2o.security.admin.client.table} (${n2o.security.admin.client.column.client_id} VARCHAR (255) PRIMARY KEY NOT NULL,
  ${n2o.security.admin.client.column.client_secret} VARCHAR(255),
  ${n2o.security.admin.client.column.grant_types} VARCHAR(255),
  ${n2o.security.admin.client.column.redirect} VARCHAR(1000),
  ${n2o.security.admin.client.column.token_time} INTEGER,
  ${n2o.security.admin.client.column.refresh_token_time} INTEGER,
  ${n2o.security.admin.client.column.logout} VARCHAR(1000)
);


COMMENT ON TABLE ${n2o.security.admin.schema}.${n2o.security.admin.client.table} IS 'Клиенты';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.client.table}.${n2o.security.admin.client.column.client_id} IS 'Имя клиента';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.client.table}.${n2o.security.admin.client.column.client_secret}  IS 'Секрет клиента';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.client.table}.${n2o.security.admin.client.column.grant_types} IS 'Тип авторизации ';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.client.table}.${n2o.security.admin.client.column.redirect} IS 'URI разрешённые для редиректа';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.client.table}.${n2o.security.admin.client.column.token_time} IS 'Время жизни токена';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.client.table}.${n2o.security.admin.client.column.refresh_token_time} IS 'Время жизни refresh токена';
COMMENT ON COLUMN ${n2o.security.admin.schema}.${n2o.security.admin.client.table}.${n2o.security.admin.client.column.logout} IS 'URL для выхода';
