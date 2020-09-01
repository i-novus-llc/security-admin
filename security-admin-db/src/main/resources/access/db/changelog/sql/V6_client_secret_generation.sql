CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE sec.client ALTER COLUMN client_secret SET DEFAULT uuid_generate_v4();