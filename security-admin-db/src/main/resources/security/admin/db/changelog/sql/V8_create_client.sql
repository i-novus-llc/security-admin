CREATE TABLE IF NOT EXISTS sec.client (
  client_id VARCHAR (255) PRIMARY KEY NOT NULL,
  client_secret VARCHAR(255),
  grant_types VARCHAR(255),
  redirect_uris VARCHAR(1000),
  access_token_lifetime INTEGER,
  refresh_token_lifetime INTEGER,
  logout_url VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS sec.client_role (
    client_client_id VARCHAR(255) NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (client_client_id ,role_id),
    CONSTRAINT client_role_client_fk FOREIGN KEY (client_client_id)
    REFERENCES sec.client (client_id) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT client_role_role_fk FOREIGN KEY (role_id)
    REFERENCES sec.role (id)
);


COMMENT ON TABLE sec.client IS 'Клиенты';
COMMENT ON COLUMN sec.client.client_id IS 'Имя клиента';
COMMENT ON COLUMN sec.client.client_secret  IS 'Секрет клиента';
COMMENT ON COLUMN sec.client.grant_types IS 'Тип авторизации ';
COMMENT ON COLUMN sec.client.redirect_uris IS 'URI разрешённые для редиректа';
COMMENT ON COLUMN sec.client.access_token_lifetime IS 'Время жизни токена';
COMMENT ON COLUMN sec.client.refresh_token_lifetime IS 'Время жизни refresh токена';
COMMENT ON COLUMN sec.client.logout_url IS 'URL для выхода';
