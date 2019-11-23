CREATE  INDEX user_username_idx_uniq
ON sec.user(username);

CREATE INDEX user_role_user_idx
ON sec.user_role(user_id);

CREATE INDEX user_role_role_idx
ON sec.user_role(role_id);

