CREATE INDEX role_permission_permission_idx
ON sec.role_permission USING btree(permission_id);

CREATE INDEX role_permission_role_idx
ON sec.role_permission USING btree(role_id);

CREATE INDEX permission_parent_idx
ON sec.permission USING btree(parent_id);

