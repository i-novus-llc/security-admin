CREATE UNIQUE INDEX user_username_idx_uniq
ON sec.user USING btree(username);

CREATE INDEX user_role_user_idx
ON sec.user_role USING btree(user_id);

CREATE INDEX user_role_role_idx
ON sec.user_role USING btree(role_id);

