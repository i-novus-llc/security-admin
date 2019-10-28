ALTER TABLE sec.user ADD COLUMN snils VARCHAR(14);

COMMENT ON COLUMN sec.user.snils IS 'СНИЛС пользователя';