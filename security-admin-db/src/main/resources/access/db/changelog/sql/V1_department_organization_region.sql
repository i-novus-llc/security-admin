CREATE TABLE sec.department
(
    id SERIAL,
    code character varying(50) NOT NULL,
    name character varying(256) NOT NULL,
    is_deleted boolean,
    CONSTRAINT department_pkey PRIMARY KEY (id)
);

COMMENT ON TABLE sec.department
    IS 'Системы';

COMMENT ON COLUMN sec.department.id
    IS 'Подразделение пользователя (заполняется для федерального уровня пользователя)';

COMMENT ON COLUMN sec.department.code
    IS 'Код  подразделения МПС';

COMMENT ON COLUMN sec.department.name
    IS 'Наименование подразделения (департамента) МПС';

--------------------------------------------------------------------------------


CREATE TABLE sec.organization
(
    id SERIAL,
    code character varying(50) NOT NULL,
    short_name character varying(100) NOT NULL,
    full_name character varying(256) NOT NULL,
    ogrn character varying(500),
    okpo character varying(500),
    is_deleted boolean,
    CONSTRAINT organization_pkey PRIMARY KEY (id)
);

COMMENT ON TABLE sec.organization
    IS 'Системы';

COMMENT ON COLUMN sec.organization.id
    IS 'Организация пользователя (заполняется для уровня пользователя - организация)';

COMMENT ON COLUMN sec.organization.code
    IS 'Код организации';

COMMENT ON COLUMN sec.organization.short_name
    IS 'Краткое наименование организации';

COMMENT ON COLUMN sec.organization.full_name
    IS 'Полное наименование организации';

COMMENT ON COLUMN sec.organization.ogrn
    IS 'Код ОГРН (уникальный код организации)';

COMMENT ON COLUMN sec.organization.okpo
    IS 'Код ОКПО (используется в стат.формах)';


--------------------------------------------------------------------------------------------------------

CREATE TABLE sec.region
(
    id SERIAL,
    code character varying(50) NOT NULL,
    name character varying(256) NOT NULL,
    okato character varying(11),
    is_deleted boolean,
    CONSTRAINT region_pkey PRIMARY KEY (id)
);

COMMENT ON TABLE sec.region
    IS 'Системы';

COMMENT ON COLUMN sec.region.id
    IS 'Регион пользователя (заполняется для регионального уровня пользователей)';

COMMENT ON COLUMN sec.region.code
    IS 'Код субъекта РФ (2-х значный)';

COMMENT ON COLUMN sec.region.name
    IS 'Наименование субъекта РФ';

COMMENT ON COLUMN sec.region.okato
    IS 'Код по ОКАТО';