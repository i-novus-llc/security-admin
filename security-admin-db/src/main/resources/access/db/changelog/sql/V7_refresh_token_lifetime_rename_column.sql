ALTER TABLE sec.client RENAME COLUMN refresh_token_lifetime TO refresh_token_validity_seconds;
COMMENT ON COLUMN sec.client.refresh_token_validity_seconds IS 'Время Refresh-токена (в секундах)';