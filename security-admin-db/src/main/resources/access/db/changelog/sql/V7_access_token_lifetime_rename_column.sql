ALTER TABLE sec.client RENAME COLUMN access_token_lifetime TO access_token_validity_seconds;
COMMENT ON COLUMN sec.client.access_token_validity_seconds IS 'Время жизни токена доступа (в секундах)';