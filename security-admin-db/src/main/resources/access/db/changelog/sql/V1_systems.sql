CREATE TABLE sec.system
(
    code character varying(50) NOT NULL,
    name character varying(256) NOT NULL,
    description character varying(500),
    short_name character varying,
    icon character varying,
    url character varying,
    public_access character varying,
    view_order character varying,
    CONSTRAINT system_pkey PRIMARY KEY (code)
);

COMMENT ON TABLE sec.system
    IS 'Системы';

COMMENT ON COLUMN sec.system.code
    IS 'Код системы';

COMMENT ON COLUMN sec.system.name
    IS 'Наименование системы';

COMMENT ON COLUMN sec.system.description
    IS 'Текстовое описание системы';

COMMENT ON COLUMN sec.system.short_name
    IS 'Порядок отображения подсистемы';