ARG POSTGRES=inovus/postgres:14
FROM $POSTGRES

COPY docker-entrypoint-initdb.d /docker-entrypoint-initdb.d

EXPOSE 5432