CREATE TABLE sec.client
(
    client_id character varying(255) NOT NULL,
    client_secret character varying(255),
    grant_types character varying(255),
    redirect_uris character varying(1000),
    access_token_lifetime integer,
    refresh_token_lifetime integer,
    logout_url character varying(1000),
    CONSTRAINT client_pkey PRIMARY KEY (client_id)
);

COMMENT ON TABLE sec.client
    IS 'Клиенты';

COMMENT ON COLUMN sec.client.client_id
    IS 'Имя клиента';

COMMENT ON COLUMN sec.client.client_secret
    IS 'Секрет клиента';

COMMENT ON COLUMN sec.client.grant_types
    IS 'Тип авторизации ';

COMMENT ON COLUMN sec.client.redirect_uris
    IS 'URI разрешённые для редиректа';

COMMENT ON COLUMN sec.client.access_token_lifetime
    IS 'Время жизни токена';

COMMENT ON COLUMN sec.client.refresh_token_lifetime
    IS 'Время жизни refresh токена';

COMMENT ON COLUMN sec.client.logout_url
    IS 'URL для выхода';
---------------------------------------------------------------------------------------------------------

CREATE TABLE sec.client_role
(
    client_id character varying(255) NOT NULL,
    role_id integer NOT NULL,
    CONSTRAINT client_role_pkey PRIMARY KEY (client_id, role_id),
    CONSTRAINT client_role_client_fk FOREIGN KEY (client_id)
        REFERENCES sec.client (client_id) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE CASCADE,
    CONSTRAINT client_role_role_fk FOREIGN KEY (role_id)
        REFERENCES sec.role (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE INDEX client_role_client_idx
    ON sec.client_role USING btree
    (client_id);


CREATE INDEX client_role_role_idx
    ON sec.client_role USING btree
    (role_id);

---------------------------------------------------------------------------------------------------------------------

CREATE TABLE sec.application
(
    code character varying(50) NOT NULL,
    name character varying(256) NOT NULL,
    system_code character varying(50) NOT NULL,
    CONSTRAINT application_pkey PRIMARY KEY (code),
    CONSTRAINT application_system_code_fk FOREIGN KEY (system_code)
        REFERENCES sec.system (code) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE CASCADE
);

COMMENT ON TABLE sec.application
    IS 'Приложения';

COMMENT ON COLUMN sec.application.code
    IS 'Код приложения';

COMMENT ON COLUMN sec.application.name
    IS 'Наименование приложения';

COMMENT ON COLUMN sec.application.system_code
    IS 'Прикладная система (подсистема, модуль), которой принадлежит приложение';